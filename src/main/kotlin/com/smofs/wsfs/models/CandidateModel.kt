package com.smofs.wsfs.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

data class CandidateModel(val candidateId: Long, val description: String, val form: WebForm) : ViewModel
