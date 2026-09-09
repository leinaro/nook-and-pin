package com.leinaro.nookandpin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leinaro.nookandpin.domain.Note
import kotlinx.coroutines.launch

@Composable
fun PileDetailScreen(
    pileName: String,
    notes: List<Note>,
    currentUserId: String,
    onReveal: (Note) -> Unit,
    onToggleLike: (Note) -> Unit,
    onPinNote: (String) -> Unit,
    onInvite: suspend (email: String) -> Boolean
) {
    var draft by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }
    var inviteStatus by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inviteEmail,
                onValueChange = {
                    inviteEmail = it
                    inviteStatus = null
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Invite by email") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val email = inviteEmail.trim()
                    if (email.isNotBlank()) {
                        scope.launch {
                            val added = onInvite(email)
                            inviteStatus = if (added) {
                                inviteEmail = ""
                                "Added $email"
                            } else {
                                "$email hasn't signed into Nook & Pin yet"
                            }
                        }
                    }
                }
            ) { Text("Invite") }
        }
        inviteStatus?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notes, key = { it.id }) { note ->
                PinnedNoteCard(
                    note = note,
                    currentUserId = currentUserId,
                    onReveal = { onReveal(note) },
                    onToggleLike = { onToggleLike(note) }
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Pin a note for $pileName…") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        onPinNote(draft.trim())
                        draft = ""
                    }
                }
            ) { Text("Pin 📌") }
        }
    }
}
