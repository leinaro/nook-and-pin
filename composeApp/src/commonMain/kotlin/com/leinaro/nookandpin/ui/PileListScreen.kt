package com.leinaro.nookandpin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.leinaro.nookandpin.domain.Pile

@Composable
fun PileListScreen(
    piles: List<Pile>,
    onPileClick: (Pile) -> Unit
) {
    if (piles.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No piles yet — start one for someone you miss 💌")
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
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
