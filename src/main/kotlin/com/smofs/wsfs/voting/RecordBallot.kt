package com.smofs.wsfs.voting

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.smofs.wsfs.dao.Candidates
import com.smofs.wsfs.dao.Categories
import com.smofs.wsfs.dao.Elections
import com.smofs.wsfs.dao.Eligibilities
import com.smofs.wsfs.dao.Inflight
import com.smofs.wsfs.dao.Votes
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import java.time.OffsetDateTime

data class InboundCat(
    val name: String,
    val id: Long?,
    val votes: List<String>
)

data class Inbound(val uuid: String, val categories: Array<InboundCat>)

data class WhoWhat(val electionId: Long, val memberId: Long)

data class InboundVote(
    val categoryId: Long,
    val memberId: Long,
    val ordinal: Int,
    val castAt: OffsetDateTime,
    val candidateId: Long?,
    val description: String
)

class RecordBallot(val database: Database) {
    companion object {
        private val mapper = jacksonObjectMapper()
        private val mapTypeRef: TypeReference<Inbound> = object : TypeReference<Inbound>() {}

        private val zapSQL = """
            DELETE FROM votes v USING categories c WHERE c.election_id = ? AND v.category_id = c.id AND v.member_id = ?
        """.trimIndent()
    }

    private fun validateUUID(ballotUUID: String) =
        database.from(Inflight)
            .innerJoin(Eligibilities,
                on = (Eligibilities.memberId eq Inflight.memberId) and (Eligibilities.electionId eq Inflight.electionId)
            )
            .innerJoin(Elections, on = (Elections.id eq Inflight.electionId))
            .select(Eligibilities.memberId, Eligibilities.electionId, Elections.allowWriteIns)
            .where((Inflight.voteUUID eq ballotUUID) and (Eligibilities.status eq "ELIGIBLE"))
            .map { row ->
                WhoWhat(row.getLong("eligibilities_election_id"), row.getLong("eligibilities_member_id"))
            }
            .firstOrNull()

    private fun validateCategoriesAndVotes(whoWhat: WhoWhat, categories: Array<InboundCat>): List<InboundVote> {
        val allowWriteIns = database.from(Elections)
            .select(Elections.allowWriteIns)
            .where(Elections.id eq whoWhat.electionId)
            .map{ row -> row.getBoolean("elections_allow_writeins") }
            .first()
        val castAt = OffsetDateTime.now()
        return categories.flatMap { category ->
                var ordinal = 0
                category.votes.flatMap { vote ->
                    database.from(Categories)
                        .leftJoin(Candidates, on = (Categories.category eq category.name))
                        .select(Categories.id, Candidates.id, Candidates.description)
                        .where((Categories.electionId eq whoWhat.electionId) and (Candidates.description eq vote))
                        .mapNotNull { row ->
                            val catId = row.getLong("categories_id")
                            require(category.id == null || catId == category.id) {
                                "Category ID mismatch"
                            }
                            require(row.getString("candidates_description") != null || allowWriteIns) {
                                "Ineligible Candidate"
                            }
                            InboundVote(
                                catId,
                                whoWhat.memberId,
                                ++ordinal,
                                castAt,
                                row.getLong("candidates_id"),
                                row.getString("candidates_description")!!
                            )
                        }
                }
            }
    }

    private fun writeToDatabase(json: String, votes: List<InboundVote>, whoWhat: WhoWhat) {
        database.useTransaction {
            database.useConnection { conn ->
                val stmt = conn.prepareStatement(zapSQL)
                stmt.setLong(1, whoWhat.electionId)
                stmt.setLong(2, whoWhat.memberId)
                stmt.executeUpdate()
            }
            database.batchInsert(Votes) {
                votes.forEach {vote ->
                    item {
                        set(it.categoryId, vote.categoryId)
                        set(it.memberId, whoWhat.memberId)
                        set(it.ordinal, vote.ordinal)
                        set(it.castAt, vote.castAt.toInstant())
                        set(it.candidateId, vote.candidateId)
                        set(it.description, vote.description)
                    }
                }
            }
            database.update(Inflight) {
                set(it.ballot, json)
                where {
                    (it.memberId eq whoWhat.memberId) and (it.electionId eq whoWhat.electionId)
                }
            }
            database.update(Eligibilities) {
                set(it.status, "VOTED")
                where {
                    (it.memberId eq whoWhat.memberId) and (it.electionId eq whoWhat.electionId)
                }
            }
        }
    }

    fun fromJson(json: String): String {
        val meow: Inbound = mapper.readValue(json, mapTypeRef)
        val whoWhat = validateUUID(meow.uuid)
        if (whoWhat == null) {
            return ("You are not eligible to vote in this election.")
        }
        val inboundVotes = validateCategoriesAndVotes(whoWhat, meow.categories)
        writeToDatabase(json, inboundVotes, whoWhat)
        return "OK"
    }
}
