package com.example.librarymanager.domain.repository

import com.example.librarymanager.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getAllBooks(): Flow<List<Book>>
    fun searchBooks(query: String): Flow<List<Book>>
    fun getAvailableBooks(): Flow<List<Book>>
    suspend fun getBookById(bookId: String): Book?
    suspend fun syncBooks(): Result<Unit>
}
