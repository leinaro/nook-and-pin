package com.leinaro.nookandpin.data

import com.leinaro.nookandpin.domain.Note
import com.leinaro.nookandpin.domain.Pile
import com.leinaro.nookandpin.domain.PileRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Real Firestore-backed implementation of [PileRepository]. Layout:
 *
 *   piles/{pileId}                 -> PileDocument
 *   piles/{pileId}/notes/{noteId}  -> NoteDocument
 *
 * [firestore.rules] enforces membership server-side, so this class doesn't
 * re-check it client-side — a query a non-member somehow issued would just
 * come back permission-denied.
 */
class FirebasePileRepository : PileRepository {

    private fun piles(): CollectionReference = Firebase.firestore.collection("piles")
    private fun notes(pileId: String): CollectionReference = piles().document(pileId).collection("notes")

    override fun observePiles(userId: String): Flow<List<Pile>> =
        piles()
            .where { "memberIds" contains userId }
            .snapshots()
            .map { snapshot -> snapshot.documents.map { it.toPile() } }

    override fun observeNotes(pileId: String): Flow<List<Note>> =
        notes(pileId)
            .orderBy("createdAt", Direction.DESCENDING)
            .snapshots()
            .map { snapshot -> snapshot.documents.map { it.toNote(pileId) } }

    override suspend fun createPile(name: String, memberIds: Set<String>, createdBy: String): Pile {
        val document = PileDocument(name = name, memberIds = memberIds.toList(), createdBy = createdBy)
        val ref = piles().add(document)
        return Pile(id = ref.id, name = name, memberIds = memberIds, createdBy = createdBy)
    }

    override suspend fun addMember(pileId: String, uid: String) {
        piles().document(pileId).update("memberIds" to FieldValue.arrayUnion(uid))
    }

    override suspend fun pinNote(pileId: String, authorId: String, text: String): Note {
        val now = Timestamp.now()
        val document = NoteDocument(authorId = authorId, text = text, createdAt = now)
        val ref = notes(pileId).add(document)
        return Note(id = ref.id, pileId = pileId, authorId = authorId, text = text, createdAt = now.toInstant())
    }

    override suspend fun markRead(pileId: String, noteId: String, userId: String) {
        val ref = notes(pileId).document(noteId)
        val existing = ref.get().data<NoteDocument>()
        if (existing.readAt == null) {
            ref.update("readAt" to Timestamp.now())
        }
    }

    override suspend fun toggleLike(pileId: String, noteId: String, userId: String) {
        val ref = notes(pileId).document(noteId)
        val existing = ref.get().data<NoteDocument>()
        val change = if (userId in existing.likedByUserIds) FieldValue.arrayRemove(userId) else FieldValue.arrayUnion(userId)
        ref.update("likedByUserIds" to change)
    }

    private fun DocumentSnapshot.toPile(): Pile {
        val document = data<PileDocument>()
        return Pile(id = id, name = document.name, memberIds = document.memberIds.toSet(), createdBy = document.createdBy)
    }

    private fun DocumentSnapshot.toNote(pileId: String): Note {
        val document = data<NoteDocument>()
        return Note(
            id = id,
            pileId = pileId,
            authorId = document.authorId,
            text = document.text,
            createdAt = document.createdAt.toInstant(),
            readAt = document.readAt?.toInstant(),
            likedByUserIds = document.likedByUserIds.toSet()
        )
    }

    private fun Timestamp.toInstant(): Instant = Instant.fromEpochSeconds(seconds, nanoseconds)
}

@Serializable
private data class PileDocument(
    val name: String = "",
    val memberIds: List<String> = emptyList(),
    val createdBy: String = ""
)

@Serializable
private data class NoteDocument(
    val authorId: String = "",
    val text: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val readAt: Timestamp? = null,
    val likedByUserIds: List<String> = emptyList()
)
