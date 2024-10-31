package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object LineItems: Table<LineItem>("lineitems") {
    val id = long("id").primaryKey().bindTo { it.id }
    val ballotId = long("ballot_id").bindTo { it.ballotId }
    val contest = varchar("contest").bindTo { it.contest }
}

interface LineItem : Entity<LineItem> {
    val id: Long
    val ballotId: Long
    val contest: String
}
