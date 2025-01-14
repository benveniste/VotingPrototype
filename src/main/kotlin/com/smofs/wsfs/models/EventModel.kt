package com.smofs.wsfs.models

import com.smofs.wsfs.dao.Elections
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

data class EventModel(val database: Database, val eventId: Long, val form: WebForm) : ViewModel {
    var elections = database.from(Elections).select(Elections.id, Elections.name)
        .where(Elections.eventId eq eventId)
        .map { row ->
            ElectionModel(database, row.getLong("elections_id"), row.getString("elections_name")!!, form)
        }
}
