package com.smofs.wsfs

import com.smofs.wsfs.formats.JacksonMessage
import com.smofs.wsfs.formats.JacksonXmlMessage
import com.smofs.wsfs.formats.jacksonMessageLens
import com.smofs.wsfs.formats.jacksonXmlMessageLens
import com.smofs.wsfs.models.TestViewModel
import com.smofs.wsfs.models.oopsMyBad
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.MicrometerMetrics
import org.http4k.filter.ServerFilters
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.lens.WebForm
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates

const val PORT = 9000

// this is a micrometer registry used mostly for testing - substitute the correct implementation.
val registry = SimpleMeterRegistry()
val renderer = HandlebarsTemplates().CachingClasspath()
val SetHtmlContentType = Filter { next ->
    { next(it).with(CONTENT_TYPE of TEXT_HTML) }
}

val app: HttpHandler = oopsMyBad(renderer).then(
    routes(
        "/ping" bind GET to {
            Response(OK).body("pong")
        },

        "/formats/xml" bind GET to {
            Response(OK).with(jacksonXmlMessageLens of JacksonXmlMessage("Barry", "Hello there!"))
        },

        "/formats/json/jackson" bind GET to {
            Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
        },

        "testor" bind GET to SetHtmlContentType.then {
            Response(OK).body(renderer(TestViewModel("This is a test", WebForm())))
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

    println("Server started on " + server.port())
}
