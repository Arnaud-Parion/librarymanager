package com.example.librarymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanager.domain.model.Book
import com.example.librarymanager.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookUiState(
    val books: List<Book> = emptyList(),
    val availableBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedBook: Book? = null
)

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                bookRepository.getAllBooks().collect { books ->
                    _uiState.value = _uiState.value.copy(
                        books = books,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun loadAvailableBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                bookRepository.getAvailableBooks().collect { books ->
                    _uiState.value = _uiState.value.copy(
                        availableBooks = books,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun searchBooks(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    loadBooks()
                } else {
                    bookRepository.searchBooks(query).collect { books ->
                        _uiState.value = _uiState.value.copy(
                            books = books
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun selectBook(book: Book) {
        _uiState.value = _uiState.value.copy(selectedBook = book)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
