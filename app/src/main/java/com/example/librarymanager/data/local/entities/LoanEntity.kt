package com.example.librarymanager.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.librarymanager.domain.model.Loan

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("clientId"),
        Index("bookId")
    ]
)
data class LoanEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val bookId: String,
    val loanDate: Long,
    val dueDate: Long,
    val returnDate: Long? = null,
) {
    fun toDomain(clientName: String = "", bookTitle: String = "") = Loan(
        id = id,
        clientId = clientId,
        bookId = bookId,
        loanDate = loanDate,
        dueDate = dueDate,
        returnDate = returnDate,
        clientName = clientName,
        bookTitle = bookTitle
    )
}
