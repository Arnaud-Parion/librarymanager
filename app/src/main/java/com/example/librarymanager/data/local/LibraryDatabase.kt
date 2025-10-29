package com.example.librarymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.librarymanager.data.local.dao.BookDao
import com.example.librarymanager.data.local.dao.ClientDao
import com.example.librarymanager.data.local.dao.LoanDao
import com.example.librarymanager.data.local.entities.BookEntity
import com.example.librarymanager.data.local.entities.ClientEntity
import com.example.librarymanager.data.local.entities.LoanEntity

@Database(
    entities = [BookEntity::class, ClientEntity::class, LoanEntity::class],
    version = 3,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun clientDao(): ClientDao
    abstract fun loanDao(): LoanDao
}
