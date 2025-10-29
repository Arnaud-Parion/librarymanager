package com.example.librarymanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.librarymanager.data.local.entities.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoans(loans: List<LoanEntity>)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun getLoanById(loanId: String): LoanEntity?

    @Query("""
        SELECT l.*, c.name as clientName, b.title as bookTitle 
        FROM loans l
        JOIN clients c ON l.clientId = c.id
        JOIN books b ON l.bookId = b.id
        ORDER BY l.loanDate DESC
    """)
    fun getAllLoans(): Flow<List<LoanWithDetails>>

    @Query("""
        SELECT l.*, c.name as clientName, b.title as bookTitle 
        FROM loans l
        JOIN clients c ON l.clientId = c.id
        JOIN books b ON l.bookId = b.id
        WHERE l.returnDate IS NULL
        ORDER BY l.dueDate ASC
    """)
    fun getActiveLoans(): Flow<List<LoanWithDetails>>

    @Query("""
        SELECT l.*, c.name as clientName, b.title as bookTitle 
        FROM loans l
        JOIN clients c ON l.clientId = c.id
        JOIN books b ON l.bookId = b.id
        WHERE l.clientId = :clientId
        ORDER BY l.loanDate DESC
    """)
    fun getLoansByClient(clientId: String): Flow<List<LoanWithDetails>>

    @Query("""
        SELECT l.*, c.name as clientName, b.title as bookTitle 
        FROM loans l
        JOIN clients c ON l.clientId = c.id
        JOIN books b ON l.bookId = b.id
        WHERE l.bookId = :bookId
        ORDER BY l.loanDate DESC
    """)
    fun getLoansByBook(bookId: String): Flow<List<LoanWithDetails>>

    @Query("DELETE FROM loans")
    suspend fun deleteAllLoans()
}

data class LoanWithDetails(
    val id: String,
    val clientId: String,
    val bookId: String,
    val loanDate: Long,
    val dueDate: Long,
    val returnDate: Long?,
    val clientName: String,
    val bookTitle: String
)
