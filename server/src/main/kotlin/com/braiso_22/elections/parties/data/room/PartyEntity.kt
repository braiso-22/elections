package com.braiso_22.elections.parties.data.room

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.braiso_22.elections.parties.domain.entities.PoliticalParty

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val acronym: String,
    val email: String,
    val web: String,
)

fun PartyEntity.toPoliticalParty() = PoliticalParty(
    id = id,
    name = name,
    acronym = acronym,
    email = email,
    web = web,
)
