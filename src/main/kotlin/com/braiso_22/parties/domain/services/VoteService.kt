package com.braiso_22.parties.domain.services

import com.braiso_22.parties.domain.entities.Vote

interface VoteService {
    fun vote(vote: Vote): Result<Vote>
    fun countVotes(): Map<String, Int>
}