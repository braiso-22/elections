package com.braiso_22.elections.parties.data.room

import androidx.room3.Room

fun createInMemoryElectionsDatabase(): ElectionsDatabase =
    Room.inMemoryDatabaseBuilder<ElectionsDatabase>().buildElectionsDatabase()

// RoomDatabase isn't Closeable, so kotlin.io.use doesn't apply.
inline fun <R> ElectionsDatabase.use(block: (ElectionsDatabase) -> R): R =
    try {
        block(this)
    } finally {
        close()
    }
