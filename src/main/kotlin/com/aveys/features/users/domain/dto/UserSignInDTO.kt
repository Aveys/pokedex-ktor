package com.aveys.features.users.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSignInDTO(
    val username: String,
    val password: String,
)
