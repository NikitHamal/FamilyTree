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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.famy.tree.data.FamilyStats
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberRow
import com.famy.tree.presentation.components.SectionHeader
import com.famy.tree.presentation.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: FamilyState,
    onOpenDrawer: () -> Unit,
    onAddDemo: () -> Unit,
    onAddMember: (FamilyMember) -> Unit,
    onOpenMember: (String) -> Unit,
    onOpenTree: () -> Unit
) {
    var showEditor by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(state.tree.name) },
                navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Outlined.Menu, contentDescription = "Menu") } }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showEditor = true }) { Icon(Icons.Outlined.Add, contentDescription = "Add member") } }
    ) { padding ->
        if (state.members.isEmpty()) {
            Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState(
                    title = "Start your family archive",
                    body = "Add your first person, or load a polished demo tree to explore Famy's workflow."
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { showEditor = true }) { Text("Add member") }
                        OutlinedButton(onClick = onAddDemo) { Text("Demo tree") }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Members", FamilyStats.totalMembers(state).toString(), Icons.Outlined.Groups, Modifier.weight(1f))
                        StatCard("Generations", FamilyStats.generationCount(state).toString(), Icons.Outlined.AccountTree, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Living", FamilyStats.livingCount(state).toString(), Icons.Outlined.Groups, Modifier.weight(1f))
                        StatCard("Events", state.members.sumOf { it.events.size }.toString(), Icons.Outlined.Timeline, Modifier.weight(1f))
                    }
                }
                item {
                    Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onOpenTree, modifier = Modifier.weight(1f)) { Text("Open tree") }
                        OutlinedButton(onClick = { showEditor = true }, modifier = Modifier.weight(1f)) { Text("Add person") }
                    }
                }
                item { SectionHeader("Recent additions", "Keep building your living family record") }
                items(FamilyStats.recentMembers(state), key = { it.id }) { member ->
                    MemberRow(member = member, modifier = Modifier.padding(horizontal = 20.dp), onClick = { onOpenMember(member.id) })
                }
            }
        }
    }
    if (showEditor) {
        MemberEditorDialog(initial = null, onDismiss = { showEditor = false }, onSave = { onAddMember(it); showEditor = false })
    }
}
