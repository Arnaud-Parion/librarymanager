package com.example.librarymanager.domain.repository

import com.example.librarymanager.domain.model.Loan
import kotlinx.coroutines.flow.Flow

interface LoanRepository {
    fun getAllLoans(): Flow<List<Loan>>
    fun getActiveLoans(): Flow<List<Loan>>
    fun getLoansByClient(clientId: String): Flow<List<Loan>>
    fun getLoansByBook(bookId: String): Flow<List<Loan>>
    suspend fun getLoanById(loanId: String): Loan?
    suspend fun createLoan(loan: Loan): Result<Unit>
    suspend fun returnLoan(loanId: String): Result<Unit>
    suspend fun syncLoans(): Result<Unit>
}
