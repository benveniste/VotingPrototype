package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar
import java.time.Instant

object Members: Table<Member>("members") {
    val id = long("id").primaryKey().bindTo { it.id }
    val eventId = long("event_id").bindTo { it.eventId }
    val personId = long("person_id").bindTo { it.personId }
    val badgeName = varchar("badge_name").bindTo { it.badgeName }
    val status = varchar("status").bindTo { it.status }
    val memberType = varchar("member_type").bindTo { it.memberType }
    val uuid = varchar("uuid").bindTo { it.uuid }
    val joinedAt = timestamp("joined_at").bindTo { it.joinedAt }
    val memberNumber = int("member_number").bindTo { it.memberNumber }
}

interface Member : Entity<Member> {
    val id: Long
    val eventId: Long
    val personId: Long
    val badgeName: String?
    val status: String?
    val memberType: String?
    val uuid: String
    val joinedAt: Instant
    val memberNumber: Int
}
