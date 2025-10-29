package com.example.librarymanager.data.repository

import com.example.librarymanager.data.local.dao.LoanDao
import com.example.librarymanager.data.remote.LibraryApiService
import com.example.librarymanager.domain.model.Loan
import com.example.librarymanager.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LoanRepositoryImpl @Inject constructor(
    private val loanDao: LoanDao,
    private val apiService: LibraryApiService
) : LoanRepository {
    override fun getAllLoans(): Flow<List<Loan>> {
        return loanDao.getAllLoans().map { details ->
            details.map { 
                Loan(
                    id = it.id,
                    clientId = it.clientId,
                    bookId = it.bookId,
                    loanDate = it.loanDate,
                    dueDate = it.dueDate,
                    returnDate = it.returnDate,
                    clientName = it.clientName,
                    bookTitle = it.bookTitle
                )
            }
        }
    }

    override fun getActiveLoans(): Flow<List<Loan>> {
        return loanDao.getActiveLoans().map { details ->
            details.map { 
                Loan(
                    id = it.id,
                    clientId = it.clientId,
                    bookId = it.bookId,
                    loanDate = it.loanDate,
                    dueDate = it.dueDate,
                    returnDate = it.returnDate,
                    clientName = it.clientName,
                    bookTitle = it.bookTitle
                )
            }
        }
    }

    override fun getLoansByClient(clientId: String): Flow<List<Loan>> {
        return loanDao.getLoansByClient(clientId).map { details ->
            details.map { 
                Loan(
                    id = it.id,
                    clientId = it.clientId,
                    bookId = it.bookId,
                    loanDate = it.loanDate,
                    dueDate = it.dueDate,
                    returnDate = it.returnDate,
                    clientName = it.clientName,
                    bookTitle = it.bookTitle
                )
            }
        }
    }

    override fun getLoansByBook(bookId: String): Flow<List<Loan>> {
        return loanDao.getLoansByBook(bookId).map { details ->
            details.map { 
                Loan(
                    id = it.id,
                    clientId = it.clientId,
                    bookId = it.bookId,
                    loanDate = it.loanDate,
                    dueDate = it.dueDate,
                    returnDate = it.returnDate,
                    clientName = it.clientName,
                    bookTitle = it.bookTitle
                )
            }
        }
    }

    override suspend fun getLoanById(loanId: String): Loan? {
        return loanDao.getLoanById(loanId)?.toDomain()
    }

    override suspend fun createLoan(loan: Loan): Result<Unit> = try {
        val loanEntity = com.example.librarymanager.data.local.entities.LoanEntity(
            id = loan.id.ifEmpty { UUID.randomUUID().toString() },
            clientId = loan.clientId,
            bookId = loan.bookId,
            loanDate = loan.loanDate,
            dueDate = loan.dueDate,
            returnDate = loan.returnDate
        )
        loanDao.insertLoan(loanEntity)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun returnLoan(loanId: String): Result<Unit> = try {
        val loan = loanDao.getLoanById(loanId) ?: return Result.failure(Exception("Loan not found"))
        val updatedLoan = loan.copy(returnDate = System.currentTimeMillis())
        loanDao.updateLoan(updatedLoan)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun syncLoans(): Result<Unit> = try {
        val remoteLoans = apiService.getAllLoans()
        val entities = remoteLoans.map { dto ->
            com.example.librarymanager.data.local.entities.LoanEntity(
                id = dto.id,
                clientId = dto.clientId,
                bookId = dto.bookId,
                loanDate = dto.loanDate,
                dueDate = dto.dueDate,
                returnDate = dto.returnDate
            )
        }
        loanDao.deleteAllLoans()
        loanDao.insertLoans(entities)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
