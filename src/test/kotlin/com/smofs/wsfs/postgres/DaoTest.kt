package com.smofs.wsfs.postgres

import at.favre.lib.crypto.bcrypt.BCrypt
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.smofs.wsfs.dao.Event
import com.smofs.wsfs.dao.Events
import com.smofs.wsfs.dao.Category
import com.smofs.wsfs.dao.Categories
import com.smofs.wsfs.dao.Election
import com.smofs.wsfs.dao.Elections
import com.smofs.wsfs.dao.Member
import com.smofs.wsfs.dao.Members
import com.smofs.wsfs.dao.Candidate
import com.smofs.wsfs.dao.Candidates
import com.smofs.wsfs.dao.Eligibilities
import com.smofs.wsfs.dao.Person
import com.smofs.wsfs.dao.Persons
import com.smofs.wsfs.dao.Votes

import mu.KotlinLogging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.fail
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.count
import org.ktorm.dsl.eq
import org.ktorm.dsl.forEach
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.first
import org.ktorm.entity.sequenceOf
import org.postgresql.util.PSQLException

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

val Database.personSequence get() = this.sequenceOf(Persons)
val Database.eventSequence get() = this.sequenceOf(Events)

private val BAD_PASSWORD = "Allen_Ludden".toCharArray()

class DaoTest {
    private val logger = KotlinLogging.logger {}
    private val database = assertDoesNotThrow { Database.connect(WSFSDataSource().getTestDataSource()) }

    private fun person(): Person {

        database.insert(Persons) {
            set(it.surName, "Montoya")
            set(it.firstName, "Flamingo")
            set(it.suffix, "III")
            set(it.password, String(BCrypt.withDefaults().hashToChar(13, BAD_PASSWORD)))
            set(it.email, "flamingo@wemightneedthat.com")
            set(it.addr1, "123 Main St.")
            set(it.city, "Anytown")
            set(it.province, "Confused")
            set(it.country, "US")
        }
        for (row in database.from(Persons).select()) {
            logger.info { row[Persons.surName] }
            assert(BCrypt.verifyer().verify(BAD_PASSWORD, row[Persons.password]!!.toCharArray()).verified)
        }
        for (person in database.personSequence) {
            logger.info { "${person.firstName} ${person.surName} ${person.email}" }
        }
        return database.personSequence.first()
    }

    private fun event(): Event {
        database.insert(Events) {
            set(it.name, "Testing")
        }
        val event = database.eventSequence.first()
        database.insert(Events) {
            set(it.name, "More Testing")
        }
        return event
    }

    private fun member(event: Event, person: Person): Member {
        val added = database.insert(Members) {
            set(it.eventId, event.id)
            set(it.personId, person.id)
            set(it.memberNumber, 666)
            set(it.status, "Attending")
            set(it.uuid, UUID.randomUUID().toString())
            set(it.joinedAt, Instant.now())
        }
        assertThat("Not added?", added, equalTo(1))

        val lookup = database.from(Members)
            .select()
            .where {
                (Members.eventId eq event.id) and (Members.personId eq person.id)
            }
            .map {
                Members.createEntity(it)
            }
        assertThat("Not found?", lookup.size, greaterThanOrEqualTo(1))

        // For testing porpoises, make them eligible for all elections.
        database.from(Elections).select(Elections.id).where(Elections.eventId eq(event.id)).forEach { row ->
            database.insert(Eligibilities) {
                set(it.memberId, lookup.first().id)
                set(it.electionId, row[Elections.id])
            }
        }
        return lookup.first()
    }

    private fun election(event: Event): Election {
        val today = LocalDate.now()
        database.insert(Elections) {
            set(it.eventId, event.id)
            set(it.name, "Testor")
            set(it.opens, today.minusWeeks(1))
            set(it.closes, today.plusMonths(2))
        }
        val lookup = database.from(Elections)
            .select()
            .where {
                (Elections.eventId eq event.id) and (Elections.name eq "Testor")
            }
            .map {
                Elections.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun category(election: Election, name: String): Category {
        database.insert(Categories) {
            set(it.electionId, election.id)
            set(it.category, name)
        }
        val lookup = database.from(Categories)
            .select()
            .where {
                (Categories.electionId eq election.id) and (Categories.category eq name)
            }
            .map {
                Categories.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun candidate(category: Category, name: String): Candidate {
        database.insert(Candidates) {
            set(it.categoryId, category.id)
            set(it.description, name)
        }
        val lookup = database.from(Candidates)
            .select()
            .where {
                (Candidates.categoryId eq category.id) and (Candidates.description eq name)
            }
            .map {
                Candidates.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun vote(category: Category, member: Member, rank: Int, candidate: Candidate) {
        require(category.id == candidate.categoryId) {
            "Not a valid candidate"
        }

        val added = database.insert(Votes) {
            set(it.categoryId, category.id)
            set(it.memberId, member.id)
            set(it.candidateId, candidate.id)
            set(it.description, candidate.description)
            set(it.castAt, Instant.now())
            set(it.ordinal, rank)
        }
        assertThat("Not added?", added, equalTo(1))
    }

    private fun vote(category: Category, member: Member, rank: Int, description: String) {
        database.from(Eligibilities)
            .select(count(Eligibilities.status))
            .where(
                (Eligibilities.memberId eq member.id)
                        and (Eligibilities.electionId eq category.electionId)
                        and (Eligibilities.status eq "ELIGIBLE")
            ).forEach { row ->
                assertThat("Ineligible?", row.getInt(1), equalTo(1))
            }

        val added = database.insert(Votes) {
            set(it.categoryId, category.id)
            set(it.memberId, member.id)
            set(it.description, description)
            set(it.castAt, Instant.now())
            set(it.ordinal, rank)
        }
        assertThat("Not added?", added, equalTo(1))
    }

    @Test
    fun schema() {
        val before = database.eventSequence.totalRecords
        database.useTransaction { transaction ->
            try {
                val flamingo = person()
                val event = event()
                val election = election(event)
                val category1 = category(election, "Best Ad Hominem")
                val category2 = category(election, "Best Oxymoron")
                val candidate1 = candidate(category1, "Mother smelled of Elderberries")
                val candidate2 = candidate(category2, "Open Secret")
                val member = member(event, flamingo)
                vote(category1, member, 1, candidate1)
                vote(category1, member, 2, "Write-in")
                vote(category2, member, 1, candidate2)
                vote(category2, member, 2, "Noah Ward")
                try {
                    vote(category2, member, 3, candidate1)
                } catch (expected: Exception) {
                    assert(expected.message == "Not a valid candidate") { "Wrong message?" }
                }
                try {
                    vote(category2, member, 1, "Duplicate")
                } catch (expected: PSQLException) {
                    assert(expected.message!!.contains("duplicate key")) { "Wrong message?" }
                }
            } catch (oops: Exception) {
                fail(oops)
            } finally {
                transaction.rollback()
            }
        }
        assertThat(database.eventSequence.totalRecords, equalTo(before))
    }
}
