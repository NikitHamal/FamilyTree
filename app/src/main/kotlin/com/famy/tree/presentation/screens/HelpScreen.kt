package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Help") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HelpCard("Build your tree", "Start from the Members tab or Home dashboard. Add people first, then connect them using parent-child, spouse, or sibling relationships.") }
            item { HelpCard("Explore visually", "Use one finger to pan, pinch to zoom, and tap a card to open its profile. Switch layouts from the Tree screen chips.") }
            item { HelpCard("Keep it private", "Famy stores data locally in app storage. Use JSON export for full backups and GEDCOM for genealogy interoperability.") }
            item { HelpCard("Performance notes", "The tree renderer draws only visible nodes and edges on a Canvas, which keeps interactions responsive for large trees.") }
        }
    }
}

@Composable
private fun HelpCard(title: String, body: String) {
    Card(Modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
