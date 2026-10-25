package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.domain.repositories.VoteRepository

class VoteRepositoryImpl : VoteRepository {
    private val votes = mutableMapOf<String, String>()
    override fun addVote(vote: Vote) {
        votes[vote.userId] = vote.partyId
    }

    override fun getVotes(): List<Vote> {
        return votes.map {
            Vote(it.value, it.key)
        }
    }
}