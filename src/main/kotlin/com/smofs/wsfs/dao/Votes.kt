package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import java.time.Instant

object Votes: Table<Vote>("votes") {
    val id = long("id").primaryKey().bindTo { it.id }
    val categoryId = long("category_id").bindTo { it.categoryId }
    val memberId = long("member_id").bindTo { it.memberId }
    val candidateId = long("candidate_id").bindTo { it.candidateId }
    val description = varchar("description").bindTo { it.description }
    val ordinal = int("ordinal").bindTo { it.ordinal }
    val castAt = timestamp("cast_at").bindTo { it.castAt }
}

interface Vote : Entity<Vote> {
    val id: Long
    val categoryId: Long
    val memberId: Long
    val ordinal: Int
    val castAt: Instant
    val candidateId: Long?
    val description: String?
}
