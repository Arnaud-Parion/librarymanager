package com.example.librarymanager.domain.model

import com.example.librarymanager.data.local.entities.LoanEntity

data class Loan(
    val id: String,
    val clientId: String,
    val bookId: String,
    val loanDate: Long,
    val dueDate: Long,
    val returnDate: Long? = null,
    val isReturned: Boolean = returnDate != null,
    val clientName: String = "",
    val bookTitle: String = ""
) {
    fun toEntity() = LoanEntity(
        id = id,
        clientId = clientId,
        bookId = bookId,
        loanDate = loanDate,
        dueDate = dueDate,
        returnDate = returnDate
    )
}
