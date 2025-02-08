package com.smofs.wsfs.formats

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class XmlCategory(
    val name: String,
    @JacksonXmlProperty(localName = "vote")
    val votes: List<String>
)

data class XmlBallot(
    val event: String,
    val eventDate: String,
    val election: String,
    var electionHash: String?,
    val castAt: String?,
    val memberUUID: String,
    val ballotUUID: String,
    @JacksonXmlProperty(localName = "category")
    val category: List<XmlCategory>,
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xs")
    val xs: String = "http://www.w3.org/2001/XMLSchema-instance",
    @JacksonXmlProperty(isAttribute = true, localName = "xs:noNamespaceSchemaLocation")
    val noname: String = "http://smofs.com/smofml.xsd"
)
