package com.smofs.wsfs

import com.smofs.wsfs.formats.JacksonMessage
import com.smofs.wsfs.formats.JacksonXmlMessage
import com.smofs.wsfs.formats.jacksonMessageLens
import com.smofs.wsfs.formats.jacksonXmlMessageLens
import com.smofs.wsfs.models.EventModel
import com.smofs.wsfs.models.FixedChoiceModel
import com.smofs.wsfs.models.WriteInModel
import com.smofs.wsfs.models.oopsMyBad
import com.smofs.wsfs.postgres.WSFSDataSource
import com.smofs.wsfs.voting.RecordBallot
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import mu.KotlinLogging
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.MicrometerMetrics
import org.http4k.filter.ServerFilters
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.lens.WebForm
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.ktorm.database.Database

const val PORT = 9000
private val logger = KotlinLogging.logger {}

// this is a micrometer registry used mostly for testing - substitute the correct implementation.
val registry = SimpleMeterRegistry()
val renderer = HandlebarsTemplates().CachingClasspath()
val SetHtmlContentType = Filter { next ->
    { next(it).with(CONTENT_TYPE of TEXT_HTML) }
}
val database = Database.connect(WSFSDataSource().getDataSource())

@Suppress("MagicNumber")
val app: HttpHandler = oopsMyBad(renderer).then(
    routes(
        "/ping" bind GET to {
            Response(OK).body("pong")
        },

        "/formats/xml" bind GET to {
            Response(OK).with(jacksonXmlMessageLens of JacksonXmlMessage("Barry", "Hello there!"))
        },

        "/html" bind static(Classpath("html")),

        "/js" bind static(Classpath("js")),

        "/formats/json/jackson" bind GET to {
            Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
        },

        "/nominate" bind GET to SetHtmlContentType.then {
            val eventModel = EventModel(database, 1, WebForm())
            val electionModel = eventModel.elections.find { it.electionId == 1L }
            val voteModel = WriteInModel(4L, database, 1L, electionModel!!.name, WebForm())
            Response(OK).body(renderer(voteModel))
        },

        "/vote" bind GET to SetHtmlContentType.then {
            val eventModel = EventModel(database, 1, WebForm())
            val electionModel = eventModel.elections.find { it.electionId == 2L }
            val voteModel = FixedChoiceModel(4L, database, 2L, electionModel!!.name, WebForm())
            Response(OK).body(renderer(voteModel))
        },

        "/site" bind GET to SetHtmlContentType.then {
            val eventModel = EventModel(database, 1, WebForm())
            val electionModel = eventModel.elections.find { it.electionId == 3L }
            val voteModel = WriteInModel(4L, database, 3L, electionModel!!.name, WebForm())
            Response(OK).body(renderer(voteModel))
        },

        "/submitBallot" bind POST to { request ->
            val alertText = RecordBallot().fromJson(request.bodyString())
            Response(OK).body(alertText)
        },

        "/testing/hamkrest" bind GET to { request ->
            Response(OK).body("Echo '${request.bodyString()}'")
        },

        "/metrics" bind GET to {
            Response(OK).body("Example metrics route for WSFS")
        }
    )
)

fun main() {
    val printingApp: HttpHandler = PrintRequest()
            .then(ServerFilters.MicrometerMetrics.RequestCounter(registry))
            .then(ServerFilters.MicrometerMetrics.RequestTimer(registry)).then(app)

    val server = printingApp.asServer(Undertow(PORT)).start()
    logger.info("Server started on " + server.port())
}
