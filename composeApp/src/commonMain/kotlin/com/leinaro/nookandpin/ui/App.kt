package com.leinaro.nookandpin.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.leinaro.nookandpin.data.InMemoryPileRepository
import com.leinaro.nookandpin.domain.AuthRepository
import com.leinaro.nookandpin.domain.Pile
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object PileList : Screen
    data class PileDetail(val pile: Pile) : Screen
}

/**
 * Shared entry point rendered identically on Android (via `MainActivity`)
 * and iOS (via `MainViewController`), both of which build the real
 * [AuthRepository] (Google sign-in token acquisition is platform-specific;
 * see [com.leinaro.nookandpin.data.FirebaseAuthRepository]'s doc).
 *
 * Piles/notes are still backed by [InMemoryPileRepository] — a pure
 * in-memory fake, no network — so that flow is fully demoable while
 * `FirebasePileRepository`'s real Firestore wiring is still TODO. Auth is
 * real: signing in gets you an actual Firebase-verified identity, it's just
 * not used to sync anything yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(authRepository: AuthRepository) {
    val scope = rememberCoroutineScope()
    val user by authRepository.currentUser.collectAsState()

    MaterialTheme {
        val signedInUser = user
        if (signedInUser == null) {
            SignInScreen(onSignInClick = { scope.launch { authRepository.signInWithGoogle() } })
            return@MaterialTheme
        }

        val pileRepository = remember(signedInUser.uid) { InMemoryPileRepository(signedInUser.uid) }
        var screen by remember(signedInUser.uid) { mutableStateOf<Screen>(Screen.PileList) }

        when (val current = screen) {
            is Screen.PileList -> {
                val piles by pileRepository.observePiles(signedInUser.uid).collectAsState(initial = emptyList())
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Nook & Pin") },
                            actions = {
                                TextButton(onClick = { scope.launch { authRepository.signOut() } }) {
                                    Text("Sign out")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding)) {
                        PileListScreen(piles = piles, onPileClick = { screen = Screen.PileDetail(it) })
                    }
                }
            }

            is Screen.PileDetail -> {
                val notes by pileRepository.observeNotes(current.pile.id).collectAsState(initial = emptyList())
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(current.pile.name) },
                            navigationIcon = {
                                TextButton(onClick = { screen = Screen.PileList }) { Text("← Back") }
                            }
                        )
                    }
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding)) {
                        PileDetailScreen(
                            pileName = current.pile.name,
                            notes = notes,
                            currentUserId = signedInUser.uid,
                            onReveal = { note ->
                                scope.launch { pileRepository.markRead(note.id, signedInUser.uid) }
                            },
                            onToggleLike = { note ->
                                scope.launch { pileRepository.toggleLike(note.id, signedInUser.uid) }
                            },
                            onPinNote = { text ->
                                scope.launch { pileRepository.pinNote(current.pile.id, signedInUser.uid, text) }
                            }
                        )
                    }
                }
            }
        }
    }
}
