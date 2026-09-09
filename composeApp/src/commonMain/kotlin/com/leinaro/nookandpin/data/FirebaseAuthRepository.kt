package com.leinaro.nookandpin.data

import com.leinaro.nookandpin.domain.AuthRepository
import com.leinaro.nookandpin.domain.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Turns a Google ID token into a Firebase session. Getting that token is
 * platform-specific enough (Credential Manager on Android, the GoogleSignIn
 * SDK from Swift on iOS) that it's injected as a plain suspend lambda rather
 * than an `expect`/`actual` class — Credential Manager needs an Android
 * `Activity`, which has no equivalent to declare in commonMain, so
 * constructor injection from each platform's entry point is the cleaner fit
 * here (see `MainActivity` for the Android side).
 */
class FirebaseAuthRepository(
    private val requestGoogleIdToken: suspend () -> String
) : AuthRepository {

    private val _currentUser = MutableStateFlow(Firebase.auth.currentUser?.toAuthUser())
    override val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    override suspend fun signInWithGoogle() {
        val idToken = requestGoogleIdToken()
        val credential = GoogleAuthProvider.credential(idToken, null)
        val result = Firebase.auth.signInWithCredential(credential)
        val user = result.user?.toAuthUser()
        _currentUser.value = user
        // So UserDirectory.findUidByEmail can find this person later — see
        // FirebaseUserDirectory.kt for the tradeoffs of doing it this way.
        user?.let { upsertUserProfile(it) }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
        _currentUser.value = null
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(uid = uid, displayName = displayName, photoUrl = photoURL, email = email)
}
