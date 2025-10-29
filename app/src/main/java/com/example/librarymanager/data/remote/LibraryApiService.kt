package com.example.librarymanager.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationYear: Int,
    val availableCopies: Int,
    val totalCopies: Int
)

data class ClientDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val membershipDate: Long,
    val isActive: Boolean = true
)

data class LoanDto(
    val id: String,
    val clientId: String,
    val bookId: String,
    val loanDate: Long,
    val dueDate: Long,
    val returnDate: Long? = null
)

interface LibraryApiService {
    // Books
    @GET("api/books")
    suspend fun getAllBooks(): List<BookDto>

    @GET("api/books/{id}")
    suspend fun getBook(@Path("id") bookId: String): BookDto

    @POST("api/books")
    suspend fun createBook(@Body book: BookDto): BookDto

    @PUT("api/books/{id}")
    suspend fun updateBook(@Path("id") bookId: String, @Body book: BookDto): BookDto

    // Clients
    @GET("api/clients")
    suspend fun getAllClients(): List<ClientDto>

    @GET("api/clients/{id}")
    suspend fun getClient(@Path("id") clientId: String): ClientDto

    @POST("api/clients")
    suspend fun createClient(@Body client: ClientDto): ClientDto

    @PUT("api/clients/{id}")
    suspend fun updateClient(@Path("id") clientId: String, @Body client: ClientDto): ClientDto

    // Loans
    @GET("api/loans")
    suspend fun getAllLoans(): List<LoanDto>

    @GET("api/loans/{id}")
    suspend fun getLoan(@Path("id") loanId: String): LoanDto

    @POST("api/loans")
    suspend fun createLoan(@Body loan: LoanDto): LoanDto

    @PUT("api/loans/{id}")
    suspend fun updateLoan(@Path("id") loanId: String, @Body loan: LoanDto): LoanDto

    @GET("api/loans/client/{clientId}")
    suspend fun getLoansByClient(@Path("clientId") clientId: String): List<LoanDto>

    @GET("api/loans/book/{bookId}")
    suspend fun getLoansByBook(@Path("bookId") bookId: String): List<LoanDto>
}
