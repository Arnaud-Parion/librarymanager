package com.example.librarymanager.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.librarymanager.data.local.dao.BookDao
import com.example.librarymanager.data.local.entities.BookEntity
import com.example.librarymanager.data.remote.BookPagingSource
import com.example.librarymanager.data.remote.LibraryApiService
import com.example.librarymanager.domain.model.Book
import com.example.librarymanager.domain.repository.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: LibraryApiService
) : BookRepository {
    override fun getAllBooks(): Flow<List<Book>> {
        this.syncBooks()
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

    override fun syncBooks(): Result<Unit> {
        CoroutineScope(Dispatchers.IO).launch {
            bookDao.deleteAllBooks()
            var page = 1
            val pageSize = 10
            do {
                val entities = apiService.searchBooks(page = page, limit = pageSize)
                    .docs.map { BookEntity(
                        id = it.key ?: "",
                        title = it.title ?: "",
                        author = it.author_name?.firstOrNull() ?: "",
                        isbn = "",
                        publicationYear = it.first_publish_year ?: 0,
                        availableCopies = it.edition_count ?: 0,
                        totalCopies = it.edition_count ?: 0,
                    ) }
                bookDao.insertBooks(entities)
                page++
            } while (entities.isNotEmpty())
        }
        return Result.success(Unit)
    }

}
