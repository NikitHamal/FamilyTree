package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.InfoChip
import com.famy.tree.presentation.components.MemberRow
import com.famy.tree.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(state: FamilyState, onOpenMember: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var livingOnly by remember { mutableStateOf(false) }
    val results = remember(query, livingOnly, state.members) {
        state.members.filter { member ->
            val text = listOf(member.displayName, member.givenName, member.familyName, member.birthPlace, member.notes)
                .joinToString(" ")
                .lowercase()
            text.contains(query.lowercase()) && (!livingOnly || member.isLiving)
        }.sortedBy { it.displayName }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Search", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Find people by names, places, or notes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search names, places, notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FilterChip(selected = livingOnly, onClick = { livingOnly = !livingOnly }, label = { Text("Living only") })
                    InfoChip("${results.size} results")
                }
            }
            item {
                SectionHeader("Results")
            }
            items(results, key = { it.id }) { member ->
                MemberRow(member, onClick = { onOpenMember(member.id) })
            }
        }
    }
}
