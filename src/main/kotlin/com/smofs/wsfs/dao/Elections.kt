package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import java.time.LocalDate

object Elections: Table<Election>("elections") {
    val id = long("id").primaryKey().bindTo { it.id }
    val eventId = long("event_id").bindTo { it.eventId }
    val name = varchar("name").bindTo { it.name }
    val opens = date("voting_opens").bindTo { it.opens }
    val closes = date("voting_closes").bindTo { it.closes }
}

interface Election : Entity<Election> {
    val id: Long
    val eventId: Long
    val name: String
    val opens: LocalDate?
    val closes: LocalDate?
}
