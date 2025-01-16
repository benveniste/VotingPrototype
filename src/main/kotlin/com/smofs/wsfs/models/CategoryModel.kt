package com.smofs.wsfs.models

import com.smofs.wsfs.dao.Candidates
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

@Suppress("unused")
data class CategoryModel(val database: Database, val catId: Long, val name: String, val form: WebForm) : ViewModel {
    val candidates = database.from(Candidates).select(Candidates.id, Candidates.description)
        .where(Candidates.categoryId eq catId)
        .map {
                row -> CandidateModel(row.getLong("candidates_id"), row.getString("candidates_description")!!, form)
        }
}
