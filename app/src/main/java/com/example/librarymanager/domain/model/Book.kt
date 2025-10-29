package com.example.librarymanager.domain.model

import com.example.librarymanager.data.local.entities.BookEntity

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationYear: Int,
    val availableCopies: Int,
    val totalCopies: Int,
    val isAvailable: Boolean = availableCopies > 0
) {
    fun toEntity() = BookEntity(
        id = id,
        title = title,
        author = author,
        isbn = isbn,
        publicationYear = publicationYear,
        availableCopies = availableCopies,
        totalCopies = totalCopies
    )
}
