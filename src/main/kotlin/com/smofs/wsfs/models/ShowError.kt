package com.smofs.wsfs.models

import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Response.Companion.invoke
import org.http4k.core.Status.Companion.SERVICE_UNAVAILABLE
import org.http4k.core.with
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

@Suppress("TooGenericExceptionCaught")
fun oopsMyBad(templates: TemplateRenderer) = Filter { next ->
    {
        try {
            next(it)
        } catch (oops: Exception) {
            Response(SERVICE_UNAVAILABLE).with(Body.viewModel(templates, TEXT_HTML).toLens() of (Error(oops)))
        }
    }
}

@Suppress("Unused")
class Error(oops: Exception) : ViewModel {
    val message = oops.localizedMessage
}
