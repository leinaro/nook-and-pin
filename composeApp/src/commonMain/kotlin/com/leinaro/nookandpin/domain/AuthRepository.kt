package com.leinaro.nookandpin.domain

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>
    suspend fun signInWithGoogle()
    suspend fun signOut()
}
