package com.braiso_22.parties.domain.repositories

import com.braiso_22.parties.domain.entities.Vote

interface VoteRepository {
    fun addVote(vote:Vote)
    fun getVotes(): List<Vote>
}