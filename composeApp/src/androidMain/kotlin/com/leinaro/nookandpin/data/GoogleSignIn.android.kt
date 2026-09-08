package com.leinaro.nookandpin.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

// The Web client id Firebase generated when Google sign-in was enabled for
// the `nook-and-pin` project (client_type 3 in composeApp/google-services.json).
// Credential Manager needs this specific one, not the Android client id —
// it's what Google uses to mint an ID token this project's backend (Firebase
// Auth) can verify. Forking this repo for your own Firebase project means
// swapping this for yours.
private const val WEB_CLIENT_ID = "206683780835-901pk5s16oh9he7uivt2dklu6ci99ccm.apps.googleusercontent.com"

/**
 * Shows the system Google account picker and returns a verifiable ID token
 * for whichever account the person chose. [context] needs to be an Activity
 * — Credential Manager renders its UI attached to one.
 */
suspend fun requestGoogleIdToken(context: Context): String {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(WEB_CLIENT_ID)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()
    val response = CredentialManager.create(context).getCredential(context, request)
    return GoogleIdTokenCredential.createFrom(response.credential.data).idToken
}
