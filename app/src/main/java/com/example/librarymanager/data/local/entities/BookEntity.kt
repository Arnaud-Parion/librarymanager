package com.example.librarymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.librarymanager.domain.model.Book

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationYear: Int,
    val availableCopies: Int,
    val totalCopies: Int,
) {
    fun toDomain() = Book(
        id = id,
        title = title,
        author = author,
        isbn = isbn,
        publicationYear = publicationYear,
        availableCopies = availableCopies,
        totalCopies = totalCopies
    )
}
