package com.example.librarymanager.domain.model

import com.example.librarymanager.data.local.entities.ClientEntity

data class Client(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val membershipDate: Long,
    val isActive: Boolean = true
) {
    fun toEntity() = ClientEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        membershipDate = membershipDate,
        isActive = isActive
    )
}
