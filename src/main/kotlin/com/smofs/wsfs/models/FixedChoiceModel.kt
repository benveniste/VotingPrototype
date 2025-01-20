package com.smofs.wsfs.models

import com.smofs.wsfs.dao.Categories
import com.smofs.wsfs.dao.Elections
import com.smofs.wsfs.dao.Inflight
import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("unused")
@OptIn(ExperimentalUuidApi::class)
data class FixedChoiceModel(
    val memberId: Long,
    val database: Database,
    val electionId: Long,
    val name: String,
    val form: WebForm
) : ViewModel {
    var voteUUID = database.from(Inflight)
        .select(Inflight.voteUUID)
        .where((Inflight.electionId eq electionId) and (Inflight.memberId eq memberId))
        .map { row -> row.getString("inflight_vote_uuid") }
        .firstOrNull() ?: let {
            val gnu = Uuid.random().toString()
            database.insert(Inflight) {
                set(it.electionId, electionId)
                set(it.memberId, memberId)
                set(it.ballot, "<Ballot></Ballot>")
                set(it.voteUUID, gnu)
            }
            gnu
        }
    var categories = database.from(Elections)
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
