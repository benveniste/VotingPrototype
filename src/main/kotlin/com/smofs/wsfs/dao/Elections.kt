package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.date
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import java.time.LocalDate

object Elections: Table<Election>("elections") {
    val id = long("id").primaryKey().bindTo { it.id }
    val eventId = long("event_id").bindTo { it.eventId }
    val name = varchar("name").bindTo { it.name }
    val maxVotes = int("max_votes").bindTo { it.maxVotes }
    val allowWriteIns = boolean("allow_writeins").bindTo { it.allowWriteIns }
    val opens = date("voting_opens").bindTo { it.opens }
    val closes = date("voting_closes").bindTo { it.closes }
    val contractAddress = varchar("contract_address").bindTo { it.contractAddress }
}

interface Election : Entity<Election> {
    val id: Long
    val eventId: Long
    val name: String
    val maxVotes: Int
    val allowWriteIns: Boolean
    val opens: LocalDate?
    val closes: LocalDate?
    val contractAddress: String
}
