package com.braiso_22.elections.parties.data.room

import com.braiso_22.elections.parties.data.defaultParties
import kotlinx.coroutines.test.runTest
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalPathApi::class)
class ElectionsDatabaseTest {
    private val directory = createTempDirectory("elections-db-test")
    private val path = directory.resolve("elections.db").toString()

    @AfterTest
    fun deleteDatabase() = directory.deleteRecursively()

    @Test
    fun votesSurviveReopeningTheDatabase() = runTest {
        createElectionsDatabase(path).use { database ->
            database.voteDao().upsert(VoteEntity(userId = "user-1", partyId = "3"))
        }

        createElectionsDatabase(path).use { database ->
            assertEquals(listOf(VoteEntity(userId = "user-1", partyId = "3")), database.voteDao().getAll())
        }
    }

    @Test
    fun partiesAreSeededOnlyOnce() = runTest {
        createElectionsDatabase(path).close()

        createElectionsDatabase(path).use { database ->
            assertEquals(defaultParties, database.partyDao().getAll().map { it.toPoliticalParty() })
        }
    }
}
