package com.example.librarymanager.ui.navigation

sealed class NavigationRoute(val route: String) {
    object Loans : NavigationRoute("loans")
    object Clients : NavigationRoute("clients")
    object Books : NavigationRoute("books")
    object ClientDetails : NavigationRoute("client_details/{clientId}") {
        fun createRoute(clientId: String) = "client_details/$clientId"
    }
}
