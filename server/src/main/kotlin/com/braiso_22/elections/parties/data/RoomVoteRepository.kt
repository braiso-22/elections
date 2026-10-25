package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.data.room.VoteDao
import com.braiso_22.elections.parties.data.room.toEntity
import com.braiso_22.elections.parties.data.room.toVote
import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.domain.repositories.VoteRepository

class RoomVoteRepository(
    private val voteDao: VoteDao
) : VoteRepository {

    override suspend fun addVote(vote: Vote) {
        voteDao.upsert(vote.toEntity())
    }

    override suspend fun getVotes(): List<Vote> {
        return voteDao.getAll().map { it.toVote() }
    }
}
