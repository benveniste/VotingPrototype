package com.smofs.wsfs.models

import com.smofs.wsfs.dao.Categories
import com.smofs.wsfs.dao.Elections
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

@Suppress("unused")
data class FixedChoiceModel(val database: Database, val electionId: Long, val name: String, val form: WebForm) :
    ViewModel {
    var categories = database
        .from(Elections)
        .innerJoin(Categories, on = Elections.id eq Categories.electionId)
        .select(Elections.maxVotes, Categories.id, Categories.category)
        .where(Elections.id eq electionId)
        .map { row ->
            CategoryModel(
                database,
                row.getLong("categories_id"),
                row.getString("categories_category")!!,
                row.getInt("elections_max_votes"),
                form
            )
        }
}
