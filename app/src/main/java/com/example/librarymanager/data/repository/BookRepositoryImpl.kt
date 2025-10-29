package com.example.librarymanager.data.repository

import com.example.librarymanager.data.local.dao.BookDao
import com.example.librarymanager.data.remote.LibraryApiService
import com.example.librarymanager.domain.model.Book
import com.example.librarymanager.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: LibraryApiService
) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchBooks(query: String): Flow<List<Book>> {
        return bookDao.searchBooks(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAvailableBooks(): Flow<List<Book>> {
        return bookDao.getAvailableBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getBookById(bookId: String): Book? {
        return bookDao.getBookById(bookId)?.toDomain()
    }

    override suspend fun syncBooks(): Result<Unit> = try {
        val remoteBooks = apiService.getAllBooks()
        val entities = remoteBooks.map { dto ->
            com.example.librarymanager.data.local.entities.BookEntity(
                id = dto.id,
                title = dto.title,
                author = dto.author,
                isbn = dto.isbn,
                publicationYear = dto.publicationYear,
                availableCopies = dto.availableCopies,
                totalCopies = dto.totalCopies
            )
        }
        bookDao.deleteAllBooks()
        bookDao.insertBooks(entities)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
