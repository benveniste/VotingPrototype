package com.smofs.wsfs.formats

import org.http4k.core.Body
import org.http4k.format.JacksonXml.auto

data class JacksonXmlMessage(val subject: String, val message: String)

val jacksonXmlMessageLens = Body.auto<JacksonXmlMessage>().toLens()
