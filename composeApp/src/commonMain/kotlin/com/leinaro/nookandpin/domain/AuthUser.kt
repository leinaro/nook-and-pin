package com.leinaro.nookandpin.domain

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val photoUrl: String?
)
