package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(state: FamilyState, onAddMember: (FamilyMember) -> Unit, onOpenMember: (String) -> Unit) {
    var showEditor by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Members") }) },
        floatingActionButton = { FloatingActionButton(onClick = { showEditor = true }) { Icon(Icons.Outlined.Add, contentDescription = "Add") } }
    ) { padding ->
        if (state.members.isEmpty()) {
            androidx.compose.foundation.layout.Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState("No members yet", "Add your first person to begin the tree.") {
                    androidx.compose.material3.Button(onClick = { showEditor = true }) { Text("Add member") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.members.sortedBy { it.displayName }, key = { it.id }) { member ->
                    MemberRow(member = member, onClick = { onOpenMember(member.id) })
                }
            }
        }
    }
    if (showEditor) MemberEditorDialog(null, { showEditor = false }, { onAddMember(it); showEditor = false })
}
