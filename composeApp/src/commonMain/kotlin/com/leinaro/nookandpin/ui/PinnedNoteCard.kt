package com.leinaro.nookandpin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leinaro.nookandpin.domain.Note

/**
 * The reveal beat that makes this app worth building: an unread note shows
 * as a folded, pinned card. Tapping it "unpins" it — fades and scales into
 * the actual message. Once read, it stays open and shows the like control.
 */
@Composable
fun PinnedNoteCard(
    note: Note,
    currentUserId: String,
    onReveal: () -> Unit,
    onToggleLike: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = note.isUnread, onClick = onReveal),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isUnread) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        AnimatedContent(
            targetState = note.isUnread,
            transitionSpec = {
                (fadeIn(tween(250)) + scaleIn(initialScale = 0.85f, animationSpec = tween(250)))
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "note-reveal"
        ) { unread ->
            if (unread) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📌", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(12.dp))
                    Text("New note — tap to unpin", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Column(Modifier.padding(16.dp)) {
                    Text(note.text, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onToggleLike)
                    ) {
                        Text(if (note.isLikedBy(currentUserId)) "❤️" else "🤍")
                        if (note.likeCount > 0) {
                            Spacer(Modifier.width(6.dp))
                            Text("${note.likeCount}", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
