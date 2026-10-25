package com.braiso_22.elections.parties.data.room

import androidx.room3.Dao
import androidx.room3.Query

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties ORDER BY CAST(id AS INTEGER)")
    suspend fun getAll(): List<PartyEntity>

    @Query("SELECT * FROM parties WHERE id = :id")
    suspend fun getById(id: String): PartyEntity?
}
