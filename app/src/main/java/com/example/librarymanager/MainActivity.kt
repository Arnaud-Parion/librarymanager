package com.example.librarymanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.librarymanager.data.sync.SyncManager
import com.example.librarymanager.ui.navigation.NavGraph
import com.example.librarymanager.ui.navigation.NavigationRoute
import com.example.librarymanager.ui.theme.LibraryManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule background sync
        syncManager.scheduleSyncWork()

        setContent {
            LibraryManagerTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncManager.cancelSyncWork()
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
