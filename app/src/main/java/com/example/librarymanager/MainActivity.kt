package com.example.librarymanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.librarymanager.data.sync.SyncManager
import com.example.librarymanager.ui.navigation.NavGraph
import com.example.librarymanager.ui.navigation.NavigationRoute
import com.example.librarymanager.ui.screens.LoginScreen
import com.example.librarymanager.ui.theme.LibraryManagerTheme
import com.example.librarymanager.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private var isLoggedIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (isLoggedIn) {
                MainScreen()
            } else {
                LoginScreen(
                    onLoginSuccess = { isLoggedIn = true },
                    authViewModel = authViewModel
                )
            }
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
