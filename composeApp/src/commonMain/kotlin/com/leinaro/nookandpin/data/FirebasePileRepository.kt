package com.leinaro.nookandpin.data

import com.leinaro.nookandpin.domain.Note
import com.leinaro.nookandpin.domain.Pile
import com.leinaro.nookandpin.domain.PileRepository
import kotlinx.coroutines.flow.Flow

/**
 * TODO: wire to `dev.gitlive:firebase-firestore` once the Firebase project
 * exists. Create it at console.firebase.google.com, add an Android app
 * (package `com.leinaro.nookandpin`) and an iOS app (bundle id
 * `com.leinaro.nookandpin`), then drop `google-services.json` into
 * `composeApp/` and `GoogleService-Info.plist` into `iosApp/iosApp/`.
 *
 * Firestore layout:
 *   piles/{pileId}                 -> Pile
 *   piles/{pileId}/notes/{noteId}  -> Note
 *
 * Pinning a note should be paired with a Cloud Function (or client-side
 * FCM call from a trusted backend) that pushes to every other member's
 * device token — that's the real expect/actual-worthy piece: registering
 * for push and handling the incoming notification differs enough between
 * Android and iOS that it deserves its own `PushNotifier` expect/actual,
 * not just relying on GitLive's Firebase wrapper to paper over it.
 */
class FirebasePileRepository : PileRepository {
    override fun observePiles(userId: String): Flow<List<Pile>> =
        TODO("Firestore query: piles where memberIds array-contains userId")

    override fun observeNotes(pileId: String): Flow<List<Note>> =
        TODO("Firestore query: piles/{pileId}/notes ordered by createdAt desc")

    override suspend fun createPile(name: String, memberIds: Set<String>, createdBy: String): Pile =
        TODO("Firestore write + return created Pile")

    override suspend fun pinNote(pileId: String, authorId: String, text: String): Note =
        TODO("Firestore write with server timestamp; triggers push to other members")

    override suspend fun markRead(noteId: String, userId: String): Unit =
        TODO("Firestore update: set readAt if null")

    override suspend fun toggleLike(noteId: String, userId: String): Unit =
        TODO("Firestore update: add/remove userId from likedByUserIds")
}
