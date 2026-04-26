package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.EventType
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.LifeEvent
import com.famy.tree.data.model.Relationship
import com.famy.tree.data.model.RelationshipType
import com.famy.tree.presentation.components.MemberAvatar
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberPicker
import com.famy.tree.presentation.components.RelationshipTypePicker
import com.famy.tree.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberProfileScreen(
    state: FamilyState,
    memberId: String,
    onBack: () -> Unit,
    onSave: (FamilyMember) -> Unit,
    onDelete: (String) -> Unit,
    onAddEvent: (String, LifeEvent) -> Unit,
    onAddRelationship: (Relationship) -> Unit
) {
    val member = state.members.firstOrNull { it.id == memberId }
    var editMember by remember { mutableStateOf(false) }
    var addEvent by remember { mutableStateOf(false) }
    var addRelationship by remember { mutableStateOf(false) }
    if (member == null) {
        Text("Member not found", modifier = Modifier.padding(24.dp))
        return
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(member.displayName) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { editMember = true }) { Icon(Icons.Outlined.Edit, contentDescription = "Edit") }
                    IconButton(onClick = { onDelete(member.id) }) { Icon(Icons.Outlined.Delete, contentDescription = "Delete") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MemberAvatar(member, Modifier.size(96.dp))
                    Text(member.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(listOfNotNull(member.birthDate.takeIf { it.isNotBlank() }, member.birthPlace.takeIf { it.isNotBlank() }).joinToString(" • ").ifBlank { "No vital details yet" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { addRelationship = true }) { Icon(Icons.Outlined.Link, null); Text(" Relationship") }
                        Button(onClick = { addEvent = true }) { Icon(Icons.Outlined.Event, null); Text(" Event") }
                    }
                }
            }
            item { SectionHeader("Biography", "Notes and life story") }
            item {
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                    Text(member.notes.ifBlank { "No biography yet." }, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item { SectionHeader("Relationships") }
            val related = state.relationships.filter { it.sourceMemberId == member.id || it.targetMemberId == member.id }
            if (related.isEmpty()) item { Text("No relationships yet.", modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(related, key = { it.id }) { relationship ->
                val otherId = if (relationship.sourceMemberId == member.id) relationship.targetMemberId else relationship.sourceMemberId
                val other = state.members.firstOrNull { it.id == otherId }?.displayName ?: "Unknown"
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(relationship.type.name.replace('_', ' '), fontWeight = FontWeight.SemiBold)
                        Text(other, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { SectionHeader("Timeline") }
            val events = member.events.sortedBy { it.date }
            if (events.isEmpty()) item { Text("No timeline events yet.", modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(events, key = { it.id }) { event ->
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(event.title, fontWeight = FontWeight.SemiBold)
                        Text(listOf(event.date, event.place).filter { it.isNotBlank() }.joinToString(" • "), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
    if (editMember) MemberEditorDialog(member, { editMember = false }, { onSave(it); editMember = false })
    if (addEvent) EventDialog(onDismiss = { addEvent = false }, onSave = { onAddEvent(member.id, it); addEvent = false })
    if (addRelationship) RelationshipDialog(member, state.members, onDismiss = { addRelationship = false }, onSave = { onAddRelationship(it); addRelationship = false })
}

@Composable
private fun EventDialog(onDismiss: () -> Unit, onSave: (LifeEvent) -> Unit) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add life event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title") })
                OutlinedTextField(date, { date = it }, label = { Text("Date") })
                OutlinedTextField(place, { place = it }, label = { Text("Place") })
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 2)
            }
        },
        confirmButton = { Button(enabled = title.isNotBlank(), onClick = { onSave(LifeEvent(type = EventType.CUSTOM, title = title, date = date, place = place, notes = notes)) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RelationshipDialog(member: FamilyMember, members: List<FamilyMember>, onDismiss: () -> Unit, onSave: (Relationship) -> Unit) {
    var otherId by remember { mutableStateOf(members.firstOrNull { it.id != member.id }?.id.orEmpty()) }
    var type by remember { mutableStateOf(RelationshipType.PARENT_CHILD) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add relationship") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("First member: ${member.displayName}")
                RelationshipTypePicker(type, { type = it })
                MemberPicker("Second member", members.filter { it.id != member.id }, otherId, { otherId = it })
                Text("For parent-child, the first member is treated as parent and the second as child.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { Button(enabled = otherId.isNotBlank(), onClick = { onSave(Relationship(sourceMemberId = member.id, targetMemberId = otherId, type = type)) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
