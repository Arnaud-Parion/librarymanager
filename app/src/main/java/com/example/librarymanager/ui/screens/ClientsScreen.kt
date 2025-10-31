package com.example.librarymanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.librarymanager.domain.model.Client
import com.example.librarymanager.ui.components.ClientCard
import com.example.librarymanager.ui.components.ErrorMessage
import com.example.librarymanager.ui.components.LoadingIndicator
import com.example.librarymanager.ui.viewmodel.ClientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    viewModel: ClientViewModel,
    onClientSelected: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clients") }
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
                onValueChange = { viewModel.searchClients(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search clients...") },
                leadingIcon = {},
                singleLine = true
            )

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.clients.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No clients found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.clients) { client ->
                        ClientCard(
                            client = client,
                            onClick = {
                                viewModel.selectClient(it)
                                onClientSelected(it)
                            }
                        )
                    }
                }
            }
        }
    }
}
