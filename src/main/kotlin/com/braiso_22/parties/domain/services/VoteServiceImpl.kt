package com.braiso_22.parties.domain.services

import com.braiso_22.parties.domain.entities.Vote
import com.braiso_22.parties.domain.repositories.PartyRepository
import com.braiso_22.parties.domain.repositories.VoteRepository

class VoteServiceImpl(
    private val partyRepository: PartyRepository,
    private val voteRepository: VoteRepository
) : VoteService {

    override fun vote(vote: Vote): Result<Vote> {
        partyRepository.getPartyById(vote.partyId) ?: return Result.failure(Exception("Party not found"))

        voteRepository.addVote(vote)
        return Result.success(vote)
    }

    override fun countVotes(): Map<String, Int> {
        val votes: List<Vote> = voteRepository.getVotes()
        return votes.groupingBy { it.partyId }.eachCount()
    }
}