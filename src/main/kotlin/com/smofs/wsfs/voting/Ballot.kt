package com.smofs.wsfs.voting

data class Ballot(
    val member: Long,
    val election: Long,
    val categories: MutableList<VoteText>
)
