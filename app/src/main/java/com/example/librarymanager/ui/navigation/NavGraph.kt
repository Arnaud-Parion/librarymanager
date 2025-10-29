package com.example.librarymanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.librarymanager.domain.model.Client
import com.example.librarymanager.ui.screens.BooksScreen
import com.example.librarymanager.ui.screens.ClientDetailsScreen
import com.example.librarymanager.ui.screens.ClientsScreen
import com.example.librarymanager.ui.screens.LoansScreen
import com.example.librarymanager.ui.viewmodel.BookViewModel
import com.example.librarymanager.ui.viewmodel.ClientViewModel
import com.example.librarymanager.ui.viewmodel.LoanViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@Composable
fun NavGraph(
    navController: NavHostController,
    onRefreshSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Loans.route,
        modifier = modifier,
    ) {
        composable(NavigationRoute.Loans.route) {
            val viewModel: LoanViewModel = hiltViewModel()
            LoansScreen(
                viewModel = viewModel,
                onRefresh = onRefreshSync
            )
        }

        composable(NavigationRoute.Clients.route) {
            val viewModel: ClientViewModel = hiltViewModel()
            ClientsScreen(
                viewModel = viewModel,
                onClientSelected = { client ->
                    navController.navigate(NavigationRoute.ClientDetails.createRoute(client.id))
                }
            )
        }

        composable(NavigationRoute.Books.route) {
            val viewModel: BookViewModel = hiltViewModel()
            BooksScreen(
                viewModel = viewModel,
                onBookSelected = { }
            )
        }

        composable(NavigationRoute.ClientDetails.route) { backStackEntry ->
            val clientId = backStackEntry.arguments?.getString("clientId") ?: return@composable
            val clientViewModel: ClientViewModel = hiltViewModel()
            val loanViewModel: LoanViewModel = hiltViewModel()
            
            val clientUiState = clientViewModel.uiState
            val selectedClient = clientUiState.collectAsState().value.selectedClient
            
            if (selectedClient != null) {
                ClientDetailsScreen(
                    client = selectedClient,
                    viewModel = loanViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
