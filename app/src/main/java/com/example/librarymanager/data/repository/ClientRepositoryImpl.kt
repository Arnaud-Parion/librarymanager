package com.example.librarymanager.data.repository

import com.example.librarymanager.data.local.dao.ClientDao
import com.example.librarymanager.data.remote.LibraryApiService
import com.example.librarymanager.domain.model.Client
import com.example.librarymanager.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao,
    private val apiService: LibraryApiService
) : ClientRepository {
    override fun getAllClients(): Flow<List<Client>> {
        return clientDao.getAllClients().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchClients(query: String): Flow<List<Client>> {
        return clientDao.searchClients(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getClientById(clientId: String): Client? {
        return clientDao.getClientById(clientId)?.toDomain()
    }

    override suspend fun syncClients(): Result<Unit> = try {
        val remoteClients = apiService.getAllClients()
        val entities = remoteClients.map { dto ->
            com.example.librarymanager.data.local.entities.ClientEntity(
                id = dto.id,
                name = dto.name,
                email = dto.email,
                phone = dto.phone,
                membershipDate = dto.membershipDate,
                isActive = dto.isActive
            )
        }
        clientDao.deleteAllClients()
        clientDao.insertClients(entities)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
