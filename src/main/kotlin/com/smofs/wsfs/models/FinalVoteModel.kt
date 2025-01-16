package com.smofs.wsfs.models

import com.smofs.wsfs.dao.Categories
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

@Suppress("unused")
data class FinalVoteModel(val database: Database, val electionId: Long, val name: String, val form: WebForm) :
    ViewModel {
    var categories = database.from(Categories).select(Categories.id, Categories.category)
        .where(Categories.electionId eq electionId)
        .map { row ->
            CategoryModel(database, row.getLong("categories_id"), row.getString("categories_category")!!, form)
        }
}
