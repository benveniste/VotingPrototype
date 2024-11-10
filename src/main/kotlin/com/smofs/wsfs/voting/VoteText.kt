package com.smofs.wsfs.voting

data class VoteText(
    val category: String,
    val candidates: MutableList<String>
)
