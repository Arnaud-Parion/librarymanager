package com.example.librarymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanager.domain.model.Loan
import com.example.librarymanager.domain.repository.BookRepository
import com.example.librarymanager.domain.repository.ClientRepository
import com.example.librarymanager.domain.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanUiState(
    val loans: List<Loan> = emptyList(),
    val activeLoans: List<Loan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLoan: Loan? = null
)

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val clientRepository: ClientRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        loadActiveLoans()
    }

    private fun loadActiveLoans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                loanRepository.getActiveLoans().collect { loans ->
                    _uiState.value = _uiState.value.copy(
                        activeLoans = loans,
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

    fun loadAllLoans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                loanRepository.getAllLoans().collect { loans ->
                    _uiState.value = _uiState.value.copy(
                        loans = loans,
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

    fun getLoansByClient(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                loanRepository.getLoansByClient(clientId).collect { loans ->
                    _uiState.value = _uiState.value.copy(
                        loans = loans,
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

    fun returnLoan(loanId: String) {
        viewModelScope.launch {
            try {
                val result = loanRepository.returnLoan(loanId)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        error = null
                    )
                    loadActiveLoans()
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.exceptionOrNull()?.message ?: "Failed to return loan"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
