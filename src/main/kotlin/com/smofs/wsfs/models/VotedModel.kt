package com.smofs.wsfs.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class VotedModel(val ballotContract: String, val form: WebForm) : ViewModel
