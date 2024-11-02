package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Candidates: Table<Candidate>("candidates") {
    val id = long("id").primaryKey().bindTo { it.id }
    val categoryId = long("category_id").bindTo { it.categoryId }
    val description = varchar("description").bindTo { it.description }
}

interface Candidate : Entity<Candidate> {
    val id: Long
    val categoryId: Long
    val description: String
}
