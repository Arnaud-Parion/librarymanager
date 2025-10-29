package com.example.librarymanager.domain.repository

import com.example.librarymanager.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getAllClients(): Flow<List<Client>>
    fun searchClients(query: String): Flow<List<Client>>
    suspend fun getClientById(clientId: String): Client?
    suspend fun syncClients(): Result<Unit>
}
