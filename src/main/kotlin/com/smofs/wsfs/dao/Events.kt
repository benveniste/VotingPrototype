package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import java.time.LocalDate

object Events: Table<Event>("events") {
    val id = long("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val established = date("established").bindTo { it.established }
    val startDate = date("start_date").bindTo { it.startDate }
    val endDate = date("end_date").bindTo { it.endDate }
}

interface Event : Entity<Event> {
    val id: Long
    val name: String
    val established: LocalDate?
    val startDate: LocalDate?
    val endDate: LocalDate?
}
