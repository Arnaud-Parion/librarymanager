package com.example.librarymanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.librarymanager.data.local.entities.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Update
    suspend fun updateClient(client: ClientEntity)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE isActive = 1 ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Query("DELETE FROM clients")
    suspend fun deleteAllClients()
}
