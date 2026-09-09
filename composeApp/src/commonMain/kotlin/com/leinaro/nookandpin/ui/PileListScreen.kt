package com.leinaro.nookandpin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leinaro.nookandpin.domain.Pile

@Composable
fun PileListScreen(
    piles: List<Pile>,
    onPileClick: (Pile) -> Unit,
    onCreatePile: (String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        if (piles.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                Text("No piles yet — start one for someone you miss 💌")
            }
        } else {
            LazyColumn(Modifier.weight(1f)) {
                items(piles, key = { it.id }) { pile ->
                    ListItem(
                        headlineContent = { Text(pile.name) },
                        supportingContent = { Text("${pile.memberIds.size} people") },
                        modifier = Modifier.clickable { onPileClick(pile) }
                    )
                    HorizontalDivider()
                }
            }
        }

        var draft by remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Name a new pile (e.g. \"You & Kei\")") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        onCreatePile(draft.trim())
                        draft = ""
                    }
                }
            ) { Text("Create") }
        }
    }
}
