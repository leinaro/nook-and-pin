package com.leinaro.nookandpin.data

import com.leinaro.nookandpin.domain.AuthUser
import com.leinaro.nookandpin.domain.UserDirectory
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

/**
 * `users/{uid}` holds just enough of a public profile to look someone up by
 * email when inviting them to a pile. It's readable by anyone signed in —
 * findable only by an exact email match, not listable/enumerable — which is
 * the tradeoff of doing this without a Cloud Function: no billing plan
 * required, but it does mean any signed-in user of this app who already
 * knows an email can confirm someone else uses Nook & Pin. Revisit with a
 * Cloud Function (`auth.getUserByEmail`, never exposed client-side) if that
 * tradeoff stops being acceptable.
 */
class FirebaseUserDirectory : UserDirectory {
    override suspend fun findUidByEmail(email: String): String? {
        val snapshot = Firebase.firestore.collection("users")
            .where { "email" equalTo email.trim().lowercase() }
            .get()
        return snapshot.documents.firstOrNull()?.id
    }
}

@Serializable
private data class UserProfileDocument(
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null
)

/** Called on every successful sign-in so [FirebaseUserDirectory] has something to find. */
suspend fun upsertUserProfile(user: AuthUser) {
    val email = user.email?.trim()?.lowercase() ?: return
    Firebase.firestore.collection("users").document(user.uid)
        .set(UserProfileDocument(email = email, displayName = user.displayName, photoUrl = user.photoUrl))
}
