package com.smofs.wsfs.voting

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.smofs.BallotBox
import com.smofs.wsfs.aws.AwsApi
import com.smofs.wsfs.dao.Candidates
import com.smofs.wsfs.dao.Categories
import com.smofs.wsfs.dao.Elections
import com.smofs.wsfs.dao.Eligibilities
import com.smofs.wsfs.dao.Events
import com.smofs.wsfs.dao.Inflight
import com.smofs.wsfs.dao.Members
import com.smofs.wsfs.dao.Votes
import com.smofs.wsfs.formats.XmlBallot
import com.smofs.wsfs.formats.XmlCategory
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.forEach
import org.ktorm.dsl.from
import org.ktorm.dsl.greater
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.isNotNull
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletFile
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.security.MessageDigest
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Inbound(val uuid: String, val categories: Array<InboundCat>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }
        other as Inbound
        if (uuid != other.uuid) {
            return false
        }
        return categories.contentEquals(other.categories)
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + categories.contentHashCode()
        return result
    }
}

data class InboundCat(
    val name: String,
    val id: Long?,
    val votes: List<String>
)

data class WhoWhat(
    val event: String,
    val eventDate: LocalDate,
    val election: String,
    val electionId: Long,
    val electionContract: String,
    val memberId: Long,
    val memberUuid: String,
    val ballotUuid: String
)

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
        private val logger = KotlinLogging.logger {}

        private val zapSQL = """
            DELETE FROM votes v USING categories c WHERE c.election_id = ? AND v.category_id = c.id AND v.member_id = ?
        """.trimIndent()

        private val xmlMapper = XmlMapper.builder()
            .configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true )
            .defaultUseWrapper(false)
            .build()
            .registerKotlinModule()
            .enable(SerializationFeature.INDENT_OUTPUT)

        private val credMap = AwsApi.getSecretMap("WSFS-Blockchain")
        private val web3j: Web3j = Web3j.build(HttpService(credMap["NodeURL"]))
        private val wallet: WalletFile = mapper.readValue(credMap["AccountKey"], WalletFile::class.java)
        private val credentials: Credentials = Credentials.create(Wallet.decrypt(credMap["AccountPassword"], wallet))
        private val chainId = web3j.ethChainId().send().chainId.toLong()
        private val mangler = RawTransactionManager(web3j, credentials, chainId)
    }

    private fun newElectionContract(electionId: Long): String {
        val newContract = BallotBox.deploy(web3j, mangler, DefaultGasProvider()).send()
        database.update(Elections) {
            set(Elections.contractAddress, newContract.contractAddress)
            where { Elections.id eq electionId }
        }
        logger.warn { "Created new ballot box for election ${electionId}: ${newContract.contractAddress}"}
        return newContract.contractAddress
    }

    private fun validateUUID(ballotUUID: String) =
        database.from(Inflight)
            .innerJoin(Eligibilities,
                on = (Eligibilities.memberId eq Inflight.memberId) and (Eligibilities.electionId eq Inflight.electionId)
            )
            .innerJoin(Elections, on = (Elections.id eq Inflight.electionId))
            .innerJoin(Events, on = (Events.id eq Elections.eventId))
            .innerJoin(Members, on = Members.personId eq Eligibilities.memberId)
            .select(
                Events.name,
                Events.startDate,
                Elections.name,
                Elections.contractAddress,
                Eligibilities.electionId,
                Eligibilities.memberId,
                Members.uuid
            )
            .where((Inflight.voteUUID eq ballotUUID) and (Eligibilities.status eq "ELIGIBLE"))
            .map { row ->
                val electionId =  row.getLong("eligibilities_election_id")
                WhoWhat(
                    row.getString("events_name")!!,
                    row.getLocalDate("events_start_date")!!,
                    row.getString("elections_name")!!,
                    electionId,
                    row.getString("elections_contract_address") ?: newElectionContract(electionId),
                    row.getLong("eligibilities_member_id"),
                    row.getString("members_uuid")!!,
                    ballotUUID
                )
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
                        .leftJoin(Candidates,
                            on = (Candidates.categoryId eq category.id!!) and (Candidates.description eq vote))
                        .select(Categories.id, Candidates.id, Candidates.description)
                        .where(Categories.id eq category.id)
                        .mapNotNull { row ->
                            require(row.getString("candidates_description") != null || allowWriteIns) {
                                "Ineligible Candidate"
                            }
                            InboundVote(
                                category.id,
                                whoWhat.memberId,
                                ++ordinal,
                                castAt,
                                row.getLong("candidates_id"),
                                vote
                            )
                        }
                }
            }
    }

    private fun writeToDatabase(json: String, votes: List<InboundVote>, whoWhat: WhoWhat) {
        database.useConnection { conn ->
            val stmt = conn.prepareStatement(zapSQL)
            stmt.setLong(1, whoWhat.electionId)
            stmt.setLong(2, whoWhat.memberId)
            stmt.executeUpdate()
        }
        database.useTransaction {
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

    private fun writeToXmlDocument(votes: List<InboundVote>, cats: Array<InboundCat>, whoWhat: WhoWhat): String {
        val cList = cats.map {cat ->
            val vList = votes.filter { it.categoryId == cat.id }.sortedBy { it.ordinal }.map { it.description }
            XmlCategory(cat.name, vList)
        }
        val voteAt = if (votes.isEmpty()) {
            OffsetDateTime.now()
        } else {
            votes.first().castAt
        }
        val hashBytes = MessageDigest.getInstance("SHA-256").digest(whoWhat.electionContract.toByteArray())
        val xmlBallot = XmlBallot(
            whoWhat.event,
            whoWhat.eventDate.toString(),
            whoWhat.election,
            hashBytes.joinToString("") { "%02x".format(it) },
            DateTimeFormatter.ISO_DATE_TIME.format(voteAt),
            whoWhat.memberUuid,
            whoWhat.ballotUuid,
            cList
        )
        return xmlMapper.writeValueAsString(xmlBallot)
    }

    private fun writeToBlockChain(smofMLString: String, whoWhat: WhoWhat): String {
        val electionContract = BallotBox.load(whoWhat.electionContract, web3j, mangler, DefaultGasProvider())
        val ballotContract = com.smofs.Ballot.deploy(web3j, mangler, DefaultGasProvider()).send()
        ballotContract.set(smofMLString).send()
        electionContract.cast(ballotContract.contractAddress).send()
        return ballotContract.contractAddress
    }

    fun reset() {
        database.useConnection { conn ->
            database.from(Elections)
                .select(Elections.contractAddress)
                .where(Elections.contractAddress.isNotNull())
                .forEach { row ->
                    val address = row.getString("elections_contract_address")
                    val smartContract = BallotBox.load(address, web3j, mangler, DefaultGasProvider())
                    smartContract.set("")
                }
            database.update(Elections) {
                set(Elections.contractAddress, null)
            }
            database.update(Eligibilities) {
                set(Eligibilities.status, "ELIGIBLE")
            }
            database.delete(Inflight) { it.id greater 0 }
            database.delete(Votes) { it.id greater 0 }
        }
    }

    fun fromJson(json: String): String {
        val meow: Inbound = mapper.readValue(json, mapTypeRef)
        val whoWhat = validateUUID(meow.uuid)
        if (whoWhat == null) {
            return ("You are not eligible to vote in this election.")
        }
        val inboundVotes = validateCategoriesAndVotes(whoWhat, meow.categories)
        val smofMLString = writeToXmlDocument(inboundVotes, meow.categories, whoWhat)
        val ballotContract = writeToBlockChain(smofMLString, whoWhat)
        writeToDatabase(json, inboundVotes, whoWhat)
        return "OK|$ballotContract"
    }
}
