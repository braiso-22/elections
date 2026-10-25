package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.domain.repositories.PartyRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Behaviour every [PartyRepository] must share. Each implementation gets a subclass,
 * so the in-memory fake used in unit tests can't drift from the Room one.
 */
abstract class PartyRepositoryContract {

    /** Must return a repository holding [defaultParties]. */
    abstract fun createRepository(): PartyRepository

    @Test
    fun getAllPartiesReturnsDefaultPartiesInOrder() = runTest {
        assertEquals(defaultParties, createRepository().getAllParties())
    }

    @Test
    fun getPartyByIdReturnsTheParty() = runTest {
        assertEquals("Partido test", createRepository().getPartyById("3")?.name)
    }

    @Test
    fun getPartyByIdReturnsNullWhenMissing() = runTest {
        assertNull(createRepository().getPartyById("99"))
    }

    @Test
    fun searchByIdMatchesExactly() = runTest {
        assertEquals(listOf("2"), createRepository().search(id = "2"))
    }

    @Test
    fun searchByNameMatchesSegmentsIgnoringCase() = runTest {
        assertEquals(listOf("3", "4", "5", "6", "7"), createRepository().search(name = "PARTIDO"))
    }

    @Test
    fun searchByAcronymMatchesSegmentsIgnoringCase() = runTest {
        assertEquals(listOf("3"), createRepository().search(acronym = "pt"))
    }

    @Test
    fun searchCombinesParamsWithOr() = runTest {
        assertEquals(listOf("1", "2", "7"), createRepository().search(id = "1", name = "nulo", acronym = "PF"))
    }

    @Test
    fun searchWithoutParamsReturnsNothing() = runTest {
        assertTrue(createRepository().search().isEmpty())
    }

    private suspend fun PartyRepository.search(
        id: String = "",
        name: String = "",
        acronym: String = "",
    ): List<String> = getPartiesByParams(id, name, acronym).map { it.id }
}
