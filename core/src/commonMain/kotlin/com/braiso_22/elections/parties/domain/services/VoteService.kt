package com.braiso_22.elections.parties.domain.services

import com.braiso_22.elections.parties.domain.entities.Vote

interface VoteService {
    suspend fun vote(vote: Vote): Result<Vote>
    suspend fun countVotes(): Map<String, Int>
}