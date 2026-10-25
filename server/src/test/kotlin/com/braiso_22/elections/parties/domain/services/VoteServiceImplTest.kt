package com.braiso_22.elections.parties.domain.services

import com.braiso_22.elections.parties.data.InMemoryPartyRepository
import com.braiso_22.elections.parties.data.InMemoryVoteRepository
import com.braiso_22.elections.parties.domain.entities.PoliticalParty
import com.braiso_22.elections.parties.domain.entities.Vote
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VoteServiceImplTest {

    private val voteRepository = InMemoryVoteRepository()
    private val service = VoteServiceImpl(
        partyRepository = InMemoryPartyRepository(
            parties = listOf(party("1"), party("2"), party("3"))
        ),
        voteRepository = voteRepository,
    )

    @Test
    fun voteForExistingPartyIsStored() = runTest {
        val vote = Vote(partyId = "2", userId = "user-1")

        val result = service.vote(vote)

        assertEquals(vote, result.getOrNull())
        assertEquals(listOf(vote), voteRepository.getVotes())
    }

    @Test
    fun voteForUnknownPartyFailsAndIsNotStored() = runTest {
        val result = service.vote(Vote(partyId = "99", userId = "user-1"))

        assertTrue(result.isFailure)
        assertEquals("Party not found", result.exceptionOrNull()?.message)
        assertTrue(voteRepository.getVotes().isEmpty())
    }

    @Test
    fun votingAgainReplacesThePreviousVote() = runTest {
        service.vote(Vote(partyId = "1", userId = "user-1"))
        service.vote(Vote(partyId = "2", userId = "user-1"))

        assertEquals(mapOf("1" to 0, "2" to 1, "3" to 0), service.countVotes())
    }

    @Test
    fun countVotesIncludesPartiesWithoutVotes() = runTest {
        assertEquals(mapOf("1" to 0, "2" to 0, "3" to 0), service.countVotes())
    }

    @Test
    fun countVotesAddsUpVotesFromDifferentUsers() = runTest {
        service.vote(Vote(partyId = "1", userId = "user-1"))
        service.vote(Vote(partyId = "1", userId = "user-2"))
        service.vote(Vote(partyId = "3", userId = "user-3"))

        assertEquals(mapOf("1" to 2, "2" to 0, "3" to 1), service.countVotes())
    }

    private fun party(id: String) = PoliticalParty(
        id = id,
        name = "Party $id",
        acronym = "P$id",
        email = "p$id@test.com",
        web = "http://p$id.test",
    )
}
