package com.smofs.wsfs.postgres

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.smofs.wsfs.dao.Events
import com.smofs.wsfs.dao.Members
import com.smofs.wsfs.dao.Persons
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.first
import org.ktorm.entity.sequenceOf
import java.time.Instant
import java.util.UUID

val Database.personSequence get() = this.sequenceOf(Persons)
val Database.eventSequence get() = this.sequenceOf(Events)

class DaoTest {
    private val logger = KotlinLogging.logger {}
    private val database = assertDoesNotThrow { Database.connect(WSFSDataSource().getDataSource()) }

    @Test
    fun persons() {
        for (row in database.from(Persons).select()) {
            logger.info { row[Persons.surName] }
        }
        for (person in database.personSequence) {
            logger.info { "${person.firstName} ${person.surName} ${person.email}" }
        }
    }

    @Test
    fun members() {
        database.useTransaction { transaction ->
            try {
                val flamingo = database.personSequence.first()
                database.insert(Events) {
                    set(it.name, "Testing")
                }
                val event = database.eventSequence.first()
                database.insert(Events) {
                    set(it.name, "More Testing")
                }
                assertThat("Wrong count?", database.eventSequence.totalRecords, equalTo(2))
                assertThat("oops", event.name, equalTo("Testing"))
                val added = database.insert(Members) {
                    set(it.eventId, event.id)
                    set(it.personId, flamingo.id)
                    set(it.memberNumber, 666)
                    set(it.status, "Attending")
                    set(it.uuid, UUID.randomUUID().toString())
                    set(it.joinedAt, Instant.now())
                }
                assertThat("Not added?", added, equalTo(1))
                val lookup = database.from(Members)
                    .select()
                    .where {
                        (Members.eventId eq event.id) and (Persons.id eq flamingo.id)
                    }
                    .totalRecords
                assertThat("Not found?", lookup, equalTo(1))
            } finally {
                transaction.rollback()
            }
        }
    }
}
