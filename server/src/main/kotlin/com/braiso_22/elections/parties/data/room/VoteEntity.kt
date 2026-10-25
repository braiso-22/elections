package com.braiso_22.elections.parties.data.room

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.braiso_22.elections.parties.domain.entities.Vote

// One vote per user: voting again replaces the previous vote.
@Entity(tableName = "votes")
data class VoteEntity(
    @PrimaryKey val userId: String,
    val partyId: String,
)

fun VoteEntity.toVote() = Vote(partyId = partyId, userId = userId)

fun Vote.toEntity() = VoteEntity(userId = userId, partyId = partyId)
