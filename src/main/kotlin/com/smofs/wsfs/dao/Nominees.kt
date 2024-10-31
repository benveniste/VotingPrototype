package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Nominees: Table<Nominee>("nominees") {
    val id = long("id").primaryKey().bindTo { it.id }
    val lineItemId = long("lineitem_id").bindTo { it.lineItemId }
    val description = varchar("description").bindTo { it.description }
}

interface Nominee : Entity<Nominee> {
    val id: Long
    val lineItemId: Long
    val description: String
}
