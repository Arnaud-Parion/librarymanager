package com.example.librarymanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.librarymanager.domain.model.Client

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val membershipDate: Long,
    val isActive: Boolean = true,
) {
    fun toDomain() = Client(
        id = id,
        name = name,
        email = email,
        phone = phone,
        membershipDate = membershipDate,
        isActive = isActive
    )
}
