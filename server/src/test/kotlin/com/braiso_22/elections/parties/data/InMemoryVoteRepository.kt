package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.domain.repositories.VoteRepository

class InMemoryVoteRepository : VoteRepository {
    private val votes = mutableMapOf<String, String>()

    override suspend fun addVote(vote: Vote) {
        votes[vote.userId] = vote.partyId
    }

    override suspend fun getVotes(): List<Vote> {
        return votes.map {
            Vote(it.value, it.key)
        }
    }
}
