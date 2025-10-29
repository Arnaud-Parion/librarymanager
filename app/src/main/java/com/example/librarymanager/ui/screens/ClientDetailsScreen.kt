package com.example.librarymanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.librarymanager.domain.model.Client
import com.example.librarymanager.ui.components.ErrorMessage
import com.example.librarymanager.ui.components.LoadingIndicator
import com.example.librarymanager.ui.components.LoanCard
import com.example.librarymanager.ui.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    client: Client,
    viewModel: LoanViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(client.id) {
        viewModel.getLoansByClient(client.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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

            // Client info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Email: ${client.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Phone: ${client.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Loans",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.loans.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No loans for this client")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.loans) { loan ->
                        LoanCard(
                            loan = loan,
                            onReturnClick = { viewModel.returnLoan(it) }
                        )
                    }
                }
            }
        }
    }
}
