package com.example.librarymanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.librarymanager.data.sync.SyncManager
import com.example.librarymanager.ui.navigation.NavGraph
import com.example.librarymanager.ui.navigation.NavigationRoute
import com.example.librarymanager.ui.screens.LoginScreen
import com.example.librarymanager.ui.theme.LibraryManagerTheme
import com.example.librarymanager.ui.viewmodel.AuthState
import com.example.librarymanager.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryManagerTheme {
                val authState by authViewModel.authState.collectAsState()
                
                when (val state = authState) {
                    is AuthState.Authenticated -> {
                        MainScreen()
                    }
                    is AuthState.Unauthenticated -> {
                        LoginScreen(
                            onLoginSuccess = { /* Handled by auth state */ },
                            authViewModel = authViewModel
                        )
                    }
                    is AuthState.Error -> {
                        // Show error state with retry option
                        ErrorScreen(
                            message = state.message,
                            onRetry = { /* Handle retry if needed */ }
                        )
                    }
                    AuthState.Initial -> {
                        // Show loading or splash screen
                        LoadingScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != null && !currentRoute.startsWith("client_details")) {
                NavigationBar {
                    NavigationBarItem(
                        icon = {},
                        label = { Text("Loans") },
                        selected = currentRoute == NavigationRoute.Loans.route,
                        onClick = {
                            navController.navigate(NavigationRoute.Loans.route) {
                                popUpTo(NavigationRoute.Loans.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = {},
                        label = { Text("Clients") },
                        selected = currentRoute == NavigationRoute.Clients.route,
                        onClick = {
                            navController.navigate(NavigationRoute.Clients.route) {
                                popUpTo(NavigationRoute.Clients.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = {},
                        label = { Text("Books") },
                        selected = currentRoute == NavigationRoute.Books.route,
                        onClick = {
                            navController.navigate(NavigationRoute.Books.route) {
                                popUpTo(NavigationRoute.Books.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            onRefreshSync = {
                // Trigger manual sync if needed
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
