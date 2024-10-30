package com.smofs.wsfs.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Persons: Table<Person>("persons") {
    val id = long("id").primaryKey().bindTo { it.id }
    val firstName = varchar("first_name").bindTo { it.firstName }
    val middleName = varchar("middle_name").bindTo { it.middleName }
    val surName = varchar("surname").bindTo { it.surName }
    val addr1 = varchar("addr_line_1").bindTo { it.addr1 }
    val addr2 = varchar("addr_line_2").bindTo { it.addr2 }
    val state = varchar("state").bindTo { it.state }
    val country = varchar("country").bindTo { it.country }
    val postal = varchar("postal_code").bindTo { it.postal }
    val email = varchar("email").bindTo { it.email }
    val phone = varchar("phone_number").bindTo { it.phone }
}

interface Person : Entity<Person> {
    val id: Long
    val firstName: String?
    val middleName: String?
    val surName: String
    val addr1: String
    val addr2: String?
    val state: String?
    val country: String
    val postal: String?
    val email: String?
    val phone: String?
}
