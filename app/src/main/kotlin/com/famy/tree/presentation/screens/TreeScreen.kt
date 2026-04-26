package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.tree.FamilyTreeCanvas
import com.famy.tree.presentation.tree.TreeLayoutMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeScreen(state: FamilyState, onOpenDrawer: () -> Unit, onMemberClick: (String) -> Unit) {
    var mode by remember { mutableStateOf(TreeLayoutMode.VERTICAL) }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Tree") },
                navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Outlined.Menu, contentDescription = "Menu") } }
            )
        }
    ) { padding ->
        if (state.members.isEmpty()) {
            Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState("No tree yet", "Add members and relationships to see your family graph.") { }
            }
        } else {
            Column(Modifier.padding(padding).fillMaxSize()) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TreeLayoutMode.entries.forEach { option ->
                        FilterChip(selected = mode == option, onClick = { mode = option }, label = { Text(option.name.lowercase().replaceFirstChar { it.titlecase() }) })
                    }
                }
                FamilyTreeCanvas(state = state, mode = mode, modifier = Modifier.weight(1f), onMemberClick = onMemberClick)
            }
        }
    }
}
