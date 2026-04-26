package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyState

private data class TimelineRow(val date: String, val title: String, val person: String, val place: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(state: FamilyState) {
    val rows = remember(state.members) {
        state.members.flatMap { member ->
            buildList {
                if (member.birthDate.isNotBlank()) add(TimelineRow(member.birthDate, "Birth", member.displayName, member.birthPlace))
                if (member.marriageDate.isNotBlank()) add(TimelineRow(member.marriageDate, "Marriage", member.displayName, member.marriagePlace))
                if (member.deathDate.isNotBlank()) add(TimelineRow(member.deathDate, "Death", member.displayName, member.deathPlace))
                member.events.forEach { add(TimelineRow(it.date, it.title, member.displayName, it.place)) }
            }
        }.sortedBy { it.date }
    }
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Timeline") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(rows) { row ->
                Card(Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {
                        Text(row.date.ifBlank { "Unknown date" }, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(row.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(listOf(row.person, row.place).filter { it.isNotBlank() }.joinToString(" • "), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
