package com.braiso_22.elections.parties.domain.repositories

import com.braiso_22.elections.parties.domain.entities.Vote

interface VoteRepository {
    suspend fun addVote(vote:Vote)
    suspend fun getVotes(): List<Vote>
}