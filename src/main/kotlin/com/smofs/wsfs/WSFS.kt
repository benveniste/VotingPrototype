package com.smofs.wsfs

import com.smofs.wsfs.formats.JacksonMessage
import com.smofs.wsfs.formats.JacksonXmlMessage
import com.smofs.wsfs.formats.jacksonMessageLens
import com.smofs.wsfs.formats.jacksonXmlMessageLens
import com.smofs.wsfs.models.JTEViewModel
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.filter.MicrometerMetrics
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.JTETemplates
import org.http4k.template.viewModel

// this is a micrometer registry used mostly for testing - substitute the correct implementation.
val registry = SimpleMeterRegistry()
// AWS config and credentials
val awsRegion = "us-east-1"
val awsService = "s3"
val awsAccessKey = "myGreatAwsAccessKey"
val awsSecretKey = "myGreatAwsSecretKey"
val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/formats/xml" bind GET to {
        Response(OK).with(jacksonXmlMessageLens of JacksonXmlMessage("Barry", "Hello there!"))
    },

    "/formats/json/jackson" bind GET to {
        Response(OK).with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))
    },

    "/templates/jte" bind GET to {
        val renderer = JTETemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = JTEViewModel("Hello there!")
        Response(OK).with(view of viewModel)
    },

    "/testing/hamkrest" bind GET to {request ->
        Response(OK).body("Echo '${request.bodyString()}'")
    },

    "/metrics" bind GET to {
        Response(OK).body("Example metrics route for WSFS")
    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest()
            .then(ServerFilters.MicrometerMetrics.RequestCounter(registry))
            .then(ServerFilters.MicrometerMetrics.RequestTimer(registry)).then(app)

    val server = printingApp.asServer(Undertow(9000)).start()

    println("Server started on " + server.port())
}
