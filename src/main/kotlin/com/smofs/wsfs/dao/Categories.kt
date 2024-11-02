package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Categories: Table<Category>("categories") {
    val id = long("id").primaryKey().bindTo { it.id }
    val electionId = long("election_id").bindTo { it.electionId }
    val category = varchar("category").bindTo { it.category }
}

interface Category : Entity<Category> {
    val id: Long
    val electionId: Long
    val category: String
}
