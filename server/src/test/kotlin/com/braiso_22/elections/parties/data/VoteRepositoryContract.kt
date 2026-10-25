package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.domain.entities.Vote
import com.braiso_22.elections.parties.domain.repositories.VoteRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Behaviour every [VoteRepository] must share. See [PartyRepositoryContract]. */
abstract class VoteRepositoryContract {

    /** Must return an empty repository. */
    abstract fun createRepository(): VoteRepository

    @Test
    fun startsEmpty() = runTest {
        assertTrue(createRepository().getVotes().isEmpty())
    }

    @Test
    fun storesVotesFromDifferentUsers() = runTest {
        val repository = createRepository()
        val votes = listOf(
            Vote(partyId = "1", userId = "user-1"),
            Vote(partyId = "3", userId = "user-2"),
        )

        votes.forEach { repository.addVote(it) }

        assertEquals(votes.toSet(), repository.getVotes().toSet())
    }

    @Test
    fun votingAgainReplacesThePreviousVote() = runTest {
        val repository = createRepository()

        repository.addVote(Vote(partyId = "1", userId = "user-1"))
        repository.addVote(Vote(partyId = "2", userId = "user-1"))

        assertEquals(listOf(Vote(partyId = "2", userId = "user-1")), repository.getVotes())
    }
}
