package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Eligibilities: Table<Eligibility>("eligibilities") {
    val id = long("id").primaryKey().bindTo { it.id }
    val memberId = long("member_id").bindTo { it.memberId }
    val electionId = long("election_id").bindTo { it.electionId }
    val status = varchar("status").bindTo { it.status }
}

interface Eligibility: Entity<Eligibility> {
    val id: Long
    val memberId: Long
    val electionId: Long
    val status: String
}
