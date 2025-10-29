package com.example.librarymanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.librarymanager.domain.model.Book
import com.example.librarymanager.ui.components.BookCard
import com.example.librarymanager.ui.components.ErrorMessage
import com.example.librarymanager.ui.components.LoadingIndicator
import com.example.librarymanager.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
    viewModel: BookViewModel,
    onBookSelected: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Books") }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.error != null) {
                ErrorMessage(
                    message = uiState.error!!,
                    onDismiss = { viewModel.clearError() }
                )
            }

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.searchBooks(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search books...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.books.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No books found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.books) { book ->
                        BookCard(
                            book = book,
                            onClick = {
                                viewModel.selectBook(it)
                                onBookSelected(it)
                            }
                        )
                    }
                }
            }
        }
    }
}
