package com.example.librarymanager.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.librarymanager.domain.model.Book

class BookPagingSource(
    private val apiService: LibraryApiService
) : PagingSource<Int, Book>() {

    private val numOfOffScreenPage: Int = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val pageIndex = params.key ?: 1
        val pageSize = params.loadSize
        return try {
            val responseData = apiService.searchBooks(page = pageIndex, limit = pageSize)

            LoadResult.Page(
                data = responseData.docs.map { Book(
                    id = it.key ?: "",
                    title = it.title ?: "",
                    author = if(it.author_name == null) "" else it.author_name[0],
                    isbn = "",
                    publicationYear = it.first_publish_year ?: 0,
                    availableCopies = it.edition_count ?: 0,
                    totalCopies = it.edition_count ?: 0,
                    isAvailable = true
                ) },
                prevKey = if (pageIndex == 1) null else pageIndex - 1,
                nextKey = if (responseData.docs.isEmpty()) null else pageIndex + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(numOfOffScreenPage)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(numOfOffScreenPage)
        }
    }
}