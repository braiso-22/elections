package com.braiso_22.elections.parties.data.room

import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.braiso_22.elections.parties.data.defaultParties
import kotlinx.coroutines.Dispatchers

@Database(entities = [PartyEntity::class, VoteEntity::class], version = 1)
abstract class ElectionsDatabase : RoomDatabase() {
    abstract fun partyDao(): PartyDao
    abstract fun voteDao(): VoteDao
}

fun createElectionsDatabase(path: String): ElectionsDatabase =
    Room.databaseBuilder<ElectionsDatabase>(name = path).buildElectionsDatabase()

// Shared configuration so tests can build the same database on top of Room.inMemoryDatabaseBuilder.
internal fun RoomDatabase.Builder<ElectionsDatabase>.buildElectionsDatabase(): ElectionsDatabase =
    setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(SeedPartiesCallback)
        .build()

// Runs only when the database file is created, so the parties are inserted once.
private object SeedPartiesCallback : RoomDatabase.Callback() {
    override suspend fun onCreate(connection: SQLiteConnection) {
        connection.prepare(
            "INSERT INTO parties (id, name, acronym, email, web) VALUES (?, ?, ?, ?, ?)"
        ).use { statement ->
            defaultParties.forEach { party ->
                statement.bindText(1, party.id)
                statement.bindText(2, party.name)
                statement.bindText(3, party.acronym)
                statement.bindText(4, party.email)
                statement.bindText(5, party.web)
                statement.step()
                statement.reset()
            }
        }
    }
}
