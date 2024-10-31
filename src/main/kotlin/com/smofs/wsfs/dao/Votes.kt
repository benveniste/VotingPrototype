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
    val lineItemId = long("lineitem_id").bindTo { it.lineItemId }
    val memberId = long("member_id").bindTo { it.memberId }
    val nomineeId = long("nominee_id").bindTo { it.nominee_id }
    val description = varchar("description").bindTo { it.description }
    val ordinal = int("ordinal").bindTo { it.ordinal }
    val castAt = timestamp("cast_at").bindTo { it.castAt }
}

interface Vote : Entity<Vote> {
    val id: Long
    val lineItemId: Long
    val memberId: Long
    val ordinal: Int
    val castAt: Instant
    val nominee_id: Long?
    val description: String?
}
