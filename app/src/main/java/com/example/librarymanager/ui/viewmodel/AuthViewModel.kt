package com.example.librarymanager.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanager.data.local.AuthDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val authDataStore: AuthDataStore
) : AndroidViewModel(application) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val context = application.applicationContext

    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://dev-0b0adrfpduv1kcx3.us.auth0.com/authorize"),
        Uri.parse("https://dev-0b0adrfpduv1kcx3.us.auth0.com/oauth/token")
    )

    private val clientId = "oPfF1JdiN0j4fkbD1qOXIcUOyq7L7szT"
    private val redirectUri = Uri.parse("com.example.librarymanager:/callback")

    private val authRequest = AuthorizationRequest.Builder(
        serviceConfig,
        clientId,
        ResponseTypeValues.CODE,
        redirectUri
    )
        .setScope("openid profile email")
        .build()

    private val authService = AuthorizationService(context)

    fun getLoginIntent(): Intent {
        return authService.getAuthorizationRequestIntent(authRequest)
    }

    fun handleAuthResponse(
        data: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val resp = AuthorizationResponse.fromIntent(data!!)
        val ex = AuthorizationException.fromIntent(data)
        if (resp != null) {
            val tokenRequest = resp.createTokenExchangeRequest()
            authService.performTokenRequest(tokenRequest) { tokenResponse, tokenEx ->
                if (tokenResponse != null) {
                    // ✅ Tokens received successfully
                    viewModelScope.launch {
                        authDataStore.saveTokens(
                            accessToken = tokenResponse.accessToken ?: "",
                            idToken = tokenResponse.idToken ?: "",
                            refreshToken = tokenResponse.refreshToken ?: ""
                        )
                        _authState.value = AuthState.Authenticated(
                            accessToken = tokenResponse.accessToken ?: "",
                            idToken = tokenResponse.idToken ?: ""
                        )
                        onSuccess()
                    }
                } else {
                    _authState.value = AuthState.Error("Token exchange failed: ${tokenEx?.errorDescription}")
                    onError("Token exchange failed: ${tokenEx?.errorDescription}")
                }
            }
        } else {
            _authState.value = AuthState.Error("Authorization failed: ${ex?.errorDescription}")
            onError("Authorization failed: ${ex?.errorDescription}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authDataStore.clearTokens()
            _authState.value = AuthState.Unauthenticated
        }
    }

    init {
        viewModelScope.launch {
            authDataStore.authTokens.collect { tokens ->
                _authState.value = if (tokens.isLoggedIn) {
                    AuthState.Authenticated(
                        accessToken = tokens.accessToken,
                        idToken = tokens.idToken
                    )
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    companion object {
        const val AUTH_REQUEST_CODE = 100
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(
        val accessToken: String,
        val idToken: String
    ) : AuthState()
    
    data class Error(val message: String) : AuthState()
}
