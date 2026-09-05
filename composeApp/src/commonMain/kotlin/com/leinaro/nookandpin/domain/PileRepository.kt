package com.leinaro.nookandpin.domain

import kotlinx.coroutines.flow.Flow

/**
 * Shared-code contract for pile/note persistence and sync. The Firebase
 * implementation lives in [com.leinaro.nookandpin.data.FirebasePileRepository];
 * swapping it for Supabase or a custom Ktor backend later only means writing
 * a new implementation of this interface — nothing above this layer changes.
 */
interface PileRepository {
    fun observePiles(userId: String): Flow<List<Pile>>
    fun observeNotes(pileId: String): Flow<List<Note>>

    suspend fun createPile(name: String, memberIds: Set<String>, createdBy: String): Pile
    suspend fun pinNote(pileId: String, authorId: String, text: String): Note
    suspend fun markRead(noteId: String, userId: String)
    suspend fun toggleLike(noteId: String, userId: String)
}
