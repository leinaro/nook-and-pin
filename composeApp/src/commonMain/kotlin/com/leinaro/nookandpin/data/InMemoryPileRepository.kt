package com.leinaro.nookandpin.data

import com.leinaro.nookandpin.domain.Note
import com.leinaro.nookandpin.domain.Pile
import com.leinaro.nookandpin.domain.PileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Pure in-memory stand-in for [FirebasePileRepository] so the UI is
 * demoable before there's a Firebase project to point at. Same contract,
 * zero network — swap this for the real implementation in `App.kt` once
 * Firebase is wired (see [FirebasePileRepository]'s TODOs); nothing in the
 * UI layer needs to change to do that.
 */
class InMemoryPileRepository(currentUserId: String) : PileRepository {

    private val seedPile = Pile(
        id = "demo-pile",
        name = "You & Kei",
        memberIds = setOf(currentUserId, "kei"),
        createdBy = "kei"
    )

    private val piles = MutableStateFlow(listOf(seedPile))

    private val notesByPile = MutableStateFlow(
        mapOf(
            seedPile.id to listOf(
                Note(
                    id = nextId(),
                    pileId = seedPile.id,
                    authorId = "kei",
                    text = "Miss you already, call me when you land 🛬",
                    createdAt = Clock.System.now()
                )
            )
        )
    )

    override fun observePiles(userId: String): Flow<List<Pile>> =
        piles.map { all -> all.filter { userId in it.memberIds } }

    override fun observeNotes(pileId: String): Flow<List<Note>> =
        notesByPile.map { byPile -> byPile[pileId].orEmpty().sortedByDescending { it.createdAt } }

    override suspend fun createPile(name: String, memberIds: Set<String>, createdBy: String): Pile {
        val pile = Pile(id = nextId(), name = name, memberIds = memberIds, createdBy = createdBy)
        piles.update { it + pile }
        return pile
    }

    override suspend fun addMember(pileId: String, uid: String) {
        piles.update { all -> all.map { if (it.id == pileId) it.copy(memberIds = it.memberIds + uid) else it } }
    }

    override suspend fun pinNote(pileId: String, authorId: String, text: String): Note {
        val note = Note(
            id = nextId(),
            pileId = pileId,
            authorId = authorId,
            text = text,
            createdAt = Clock.System.now()
        )
        notesByPile.update { byPile -> byPile + (pileId to (byPile[pileId].orEmpty() + note)) }
        return note
    }

    override suspend fun markRead(pileId: String, noteId: String, userId: String) {
        updateNote(pileId, noteId) { note -> if (note.readAt == null) note.copy(readAt = Clock.System.now()) else note }
    }

    override suspend fun toggleLike(pileId: String, noteId: String, userId: String) {
        updateNote(pileId, noteId) { note ->
            if (note.isLikedBy(userId)) note.copy(likedByUserIds = note.likedByUserIds - userId)
            else note.copy(likedByUserIds = note.likedByUserIds + userId)
        }
    }

    private fun updateNote(pileId: String, noteId: String, transform: (Note) -> Note) {
        notesByPile.update { byPile ->
            val notes = byPile[pileId].orEmpty()
            byPile + (pileId to notes.map { if (it.id == noteId) transform(it) else it })
        }
    }

    private fun nextId(): String = "n-${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt(1000)}"
}
