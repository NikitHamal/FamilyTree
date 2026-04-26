package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType
import com.famy.tree.presentation.components.MemberPicker
import com.famy.tree.presentation.components.RelationshipTypePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipsScreen(state: FamilyState, onAdd: (Relationship) -> Unit, onRemove: (String) -> Unit) {
    var first by remember(state.members) { mutableStateOf(state.members.firstOrNull()?.id.orEmpty()) }
    var second by remember(state.members) { mutableStateOf(state.members.drop(1).firstOrNull()?.id.orEmpty()) }
    var type by remember { mutableStateOf(RelationshipType.PARENT_CHILD) }
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Relationships") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Add relationship", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        MemberPicker("First member", state.members, first, { first = it })
                        RelationshipTypePicker(type, { type = it })
                        MemberPicker("Second member", state.members, second, { second = it })
                        Button(onClick = { onAdd(Relationship(sourceMemberId = first, targetMemberId = second, type = type)) }, enabled = first.isNotBlank() && second.isNotBlank()) { Text("Add relationship") }
                    }
                }
            }
            items(state.relationships, key = { it.id }) { relationship ->
                val source = state.members.firstOrNull { it.id == relationship.sourceMemberId }?.displayName ?: "Unknown"
                val target = state.members.firstOrNull { it.id == relationship.targetMemberId }?.displayName ?: "Unknown"
                Card(Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(relationship.type.name.replace('_', ' '), fontWeight = FontWeight.SemiBold)
                            Text("$source -> $target", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onRemove(relationship.id) }) { Icon(Icons.Outlined.Delete, contentDescription = "Delete") }
                    }
                }
            }
        }
    }
}
