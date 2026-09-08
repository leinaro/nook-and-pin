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
import com.leinaro.nookandpin.domain.Pile
import kotlinx.coroutines.launch

// TODO: replace with a real signed-in user id once Firebase Auth is wired.
private const val CURRENT_USER_ID = "you"

private sealed interface Screen {
    data object PileList : Screen
    data class PileDetail(val pile: Pile) : Screen
}

/**
 * Shared entry point rendered identically on Android (via `MainActivity`)
 * and iOS (via `MainViewController`).
 *
 * Backed by [InMemoryPileRepository] for now — a pure in-memory fake, no
 * network — so the pile/note/like/reveal flow is fully demoable before
 * there's a Firebase project to point at. Swapping the fake for
 * `FirebasePileRepository` here is the only change the sync work needs;
 * [PileListScreen], [PileDetailScreen] and [PinnedNoteCard] don't know or
 * care which one is behind the [com.leinaro.nookandpin.domain.PileRepository]
 * interface.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val repository = remember { InMemoryPileRepository(CURRENT_USER_ID) }
    val scope = rememberCoroutineScope()
    var screen by remember { mutableStateOf<Screen>(Screen.PileList) }

    MaterialTheme {
        when (val current = screen) {
            is Screen.PileList -> {
                val piles by repository.observePiles(CURRENT_USER_ID).collectAsState(initial = emptyList())
                Scaffold(topBar = { TopAppBar(title = { Text("Nook & Pin") }) }) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding)) {
                        PileListScreen(piles = piles, onPileClick = { screen = Screen.PileDetail(it) })
                    }
                }
            }

            is Screen.PileDetail -> {
                val notes by repository.observeNotes(current.pile.id).collectAsState(initial = emptyList())
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
                            currentUserId = CURRENT_USER_ID,
                            onReveal = { note -> scope.launch { repository.markRead(note.id, CURRENT_USER_ID) } },
                            onToggleLike = { note -> scope.launch { repository.toggleLike(note.id, CURRENT_USER_ID) } },
                            onPinNote = { text ->
                                scope.launch { repository.pinNote(current.pile.id, CURRENT_USER_ID, text) }
                            }
                        )
                    }
                }
            }
        }
    }
}
