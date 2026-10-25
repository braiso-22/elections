package com.braiso_22.elections.parties.data

import com.braiso_22.elections.parties.data.room.createInMemoryElectionsDatabase
import kotlin.test.AfterTest

class RoomPartyRepositoryTest : PartyRepositoryContract() {
    private val database = createInMemoryElectionsDatabase()

    override fun createRepository() = RoomPartyRepository(database.partyDao())

    @AfterTest
    fun closeDatabase() = database.close()
}

class RoomVoteRepositoryTest : VoteRepositoryContract() {
    private val database = createInMemoryElectionsDatabase()

    override fun createRepository() = RoomVoteRepository(database.voteDao())

    @AfterTest
    fun closeDatabase() = database.close()
}
