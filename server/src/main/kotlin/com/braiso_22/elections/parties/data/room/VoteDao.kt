package com.braiso_22.elections.parties.data.room

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface VoteDao {
    @Upsert
    suspend fun upsert(vote: VoteEntity)

    @Query("SELECT * FROM votes")
    suspend fun getAll(): List<VoteEntity>
}
