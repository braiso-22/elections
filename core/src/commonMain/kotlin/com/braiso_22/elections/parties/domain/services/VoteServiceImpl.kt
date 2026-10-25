package com.braiso_22.elections.parties.domain.services

import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.domain.repositories.PartyRepository
import com.braiso_22.elections.parties.domain.repositories.VoteRepository

class VoteServiceImpl(
    private val partyRepository: PartyRepository,
    private val voteRepository: VoteRepository
) : VoteService {

    override suspend fun vote(vote: Vote): Result<Vote> {
        partyRepository.getPartyById(vote.partyId) ?: return Result.failure(Exception("Party not found"))

        voteRepository.addVote(vote)
        return Result.success(vote)
    }

    override suspend fun countVotes(): Map<String, Int> {
        val parties = partyRepository.getAllParties().map { it.id }
        val votes: List<Vote> = voteRepository.getVotes()
        return parties.associateWith { partyId ->
            votes.count { it.partyId == partyId }
        }
    }
}