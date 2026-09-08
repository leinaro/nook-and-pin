package com.leinaro.nookandpin

import androidx.compose.ui.window.ComposeUIViewController
import com.leinaro.nookandpin.data.FirebaseAuthRepository
import com.leinaro.nookandpin.ui.App

fun MainViewController() = ComposeUIViewController {
    // TODO: once iosApp exists, wire this to the GoogleSignIn CocoaPod from
    // Swift (GIDSignIn.sharedInstance.signIn(...)) and bridge the resulting
    // ID token back into Kotlin. Google Sign-In on iOS is a native UI flow
    // driven by GIDSignIn, not something to reimplement in Kotlin/Native —
    // this lambda is where that bridge plugs in.
    val authRepository = FirebaseAuthRepository(
        requestGoogleIdToken = {
            throw NotImplementedError("Google Sign-In on iOS isn't wired up yet — see MainViewController.kt")
        }
    )
    App(authRepository)
}
