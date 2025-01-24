package com.smofs.wsfs.formats

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class XmlCategory(
    val name: String,
    @JacksonXmlProperty(localName = "vote")
    val votes: Array<String>
)

data class XmlBallot(
    val event: String,
    val election: String,
    val castAt: String?,
    val memberUUID: String,
    @JacksonXmlProperty(localName = "category")
    val category: List<XmlCategory>
)
