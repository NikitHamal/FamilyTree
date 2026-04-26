package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
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
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.components.InfoChip
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberRow
import com.famy.tree.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(state: FamilyState, onAddMember: (FamilyMember) -> Unit, onOpenMember: (String) -> Unit) {
    var showEditor by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("People", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Browse the family directory in a cleaner list", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showEditor = true }) { Icon(Icons.Outlined.Add, contentDescription = "Add") } }
    ) { padding ->
        if (state.members.isEmpty()) {
            Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState("No members yet", "Add your first person to begin the family graph.") {
                    androidx.compose.material3.Button(onClick = { showEditor = true }) { Text("Add member") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SectionHeader("Directory", "All profiles, alphabetically ordered.") {
                        InfoChip("${state.members.size} total")
                    }
                }
                items(state.members.sortedBy { it.displayName }, key = { it.id }) { member ->
                    MemberRow(member = member, onClick = { onOpenMember(member.id) })
                }
            }
        }
    }
    if (showEditor) MemberEditorDialog(null, { showEditor = false }, { onAddMember(it); showEditor = false })
}
