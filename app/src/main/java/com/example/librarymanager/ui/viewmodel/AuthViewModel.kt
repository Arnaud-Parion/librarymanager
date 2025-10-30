package com.example.librarymanager.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import net.openid.appauth.*

class AuthViewModel(application: Application) : AndroidViewModel(application) {

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
                    val accessToken = tokenResponse.accessToken
                    val idToken = tokenResponse.idToken
                    // TODO: Store tokens securely (e.g. DataStore or EncryptedSharedPrefs)
                    onSuccess()
                } else {
                    onError("Token exchange failed: ${tokenEx?.errorDescription}")
                }
            }
        } else {
            onError("Authorization failed: ${ex?.errorDescription}")
        }
    }

    companion object {
        const val AUTH_REQUEST_CODE = 100
    }
}
