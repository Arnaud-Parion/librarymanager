package com.example.librarymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanager.domain.model.Client
import com.example.librarymanager.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientUiState(
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedClient: Client? = null
)

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                clientRepository.getAllClients().collect { clients ->
                    _uiState.value = _uiState.value.copy(
                        clients = clients,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    fun searchClients(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            try {
                if (query.isEmpty()) {
                    loadClients()
                } else {
                    clientRepository.searchClients(query).collect { clients ->
                        _uiState.value = _uiState.value.copy(
                            clients = clients
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun selectClient(client: Client) {
        _uiState.value = _uiState.value.copy(selectedClient = client)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
