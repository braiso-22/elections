package com.braiso_22.elections.parties.data

class InMemoryPartyRepositoryTest : PartyRepositoryContract() {
    override fun createRepository() = InMemoryPartyRepository()
}

class InMemoryVoteRepositoryTest : VoteRepositoryContract() {
    override fun createRepository() = InMemoryVoteRepository()
}
