package com.smofs.wsfs.postgres

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.smofs.wsfs.dao.Ballot
import com.smofs.wsfs.dao.Ballots
import com.smofs.wsfs.dao.Event
import com.smofs.wsfs.dao.Events
import com.smofs.wsfs.dao.LineItem
import com.smofs.wsfs.dao.LineItems
import com.smofs.wsfs.dao.Member
import com.smofs.wsfs.dao.Members
import com.smofs.wsfs.dao.Nominee
import com.smofs.wsfs.dao.Nominees
import com.smofs.wsfs.dao.Person
import com.smofs.wsfs.dao.Persons
import com.smofs.wsfs.dao.Votes

import mu.KotlinLogging

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.fail
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
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

class DaoTest {
    private val logger = KotlinLogging.logger {}
    private val database = assertDoesNotThrow { Database.connect(WSFSDataSource().getDataSource()) }

    private fun person(): Person {
        database.insert(Persons) {
            set(it.surName, "Montoya")
            set(it.firstName, "Flamingo")
            set(it.email, "flamingo@wemightneedthat.com")
            set(it.addr1, "123 Main St.")
            set(it.city, "Anytown")
            set(it.province, "Confused")
            set(it.country, "US")
        }
        for (row in database.from(Persons).select()) {
            logger.info { row[Persons.surName] }
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
        return lookup.first()
    }

    private fun ballot(event: Event): Ballot {
        val today = LocalDate.now()
        database.insert(Ballots) {
            set(it.eventId, event.id)
            set(it.name, "Testor")
            set(it.opens, today.minusWeeks(1))
            set(it.closes, today.plusMonths(2))
        }
        val lookup = database.from(Ballots)
            .select()
            .where {
                (Ballots.eventId eq event.id) and (Ballots.name eq "Testor")
            }
            .map {
                Ballots.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun lineitem(ballot: Ballot, name: String): LineItem {
        database.insert(LineItems) {
            set(it.ballotId, ballot.id)
            set(it.contest, name)
        }
        val lookup = database.from(LineItems)
            .select()
            .where {
                (LineItems.ballotId eq ballot.id) and (LineItems.contest eq name)
            }
            .map {
                LineItems.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun nominee(lineItem: LineItem, name: String): Nominee {
        database.insert(Nominees) {
            set(it.lineItemId, lineItem.id)
            set(it.description, name)
        }
        val lookup = database.from(Nominees)
            .select()
            .where {
                (Nominees.lineItemId eq lineItem.id) and (Nominees.description eq name)
            }
            .map {
                Nominees.createEntity(it)
            }
        assertThat("Not found?", lookup.size, equalTo(1))
        return lookup.first()
    }

    private fun vote(lineItem: LineItem, member: Member, rank: Int, nominee: Nominee) {
        if (lineItem.id != nominee.lineItemId) {
            throw IllegalArgumentException("Not a valid nominee")
        }
        val added = database.insert(Votes) {
            set(it.lineItemId, lineItem.id)
            set(it.memberId, member.id)
            set(it.nomineeId, nominee.id)
            set(it.description, nominee.description)
            set(it.castAt, Instant.now())
            set(it.ordinal, rank)
        }
        assertThat("Not added?", added, equalTo(1))
    }

    private fun vote(lineItem: LineItem, member: Member, rank: Int, description: String) {
        val added = database.insert(Votes) {
            set(it.lineItemId, lineItem.id)
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
                val ballot = ballot(event)
                val lineitem1 = lineitem(ballot, "Best AdHominem")
                val lineitem2 = lineitem(ballot, "Best Oxymoron")
                val nominee1 = nominee(lineitem1, "Mother smelled of Elderberries")
                val nominee2 = nominee(lineitem2, "Open Secret")
                val member = member(event, flamingo)
                vote(lineitem1, member, 1, nominee1)
                vote(lineitem1, member, 2, "Write-in")
                vote(lineitem2, member, 1, nominee2)
                vote(lineitem2, member, 2, "Noah Ward")
                try {
                    vote(lineitem2, member, 3, nominee1)
                } catch (expected: Exception) {
                    assert(expected.message == "Not a valid nominee") { "Wrong message?" }
                }
                try {
                    vote(lineitem2, member, 1, "Duplicate")
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
