package com.famy.tree.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.famy.tree.presentation.components.InfoChip
import com.famy.tree.presentation.components.InsightBanner
import com.famy.tree.presentation.components.MemberAvatar
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberPicker
import com.famy.tree.presentation.components.MemberRow
import com.famy.tree.presentation.components.RelationshipTypePicker
import com.famy.tree.presentation.components.SectionHeader
import com.famy.tree.presentation.theme.FamyAccent

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

    val peopleById = state.members.associateBy { it.id }
    val connections = state.relationships.filter { it.sourceMemberId == member.id || it.targetMemberId == member.id }
    val relatedMembers = connections.mapNotNull { relation ->
        val otherId = if (relation.sourceMemberId == member.id) relation.targetMemberId else relation.sourceMemberId
        peopleById[otherId]?.let { relation.type to it }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(member.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { editMember = true }) { Icon(Icons.Outlined.Edit, contentDescription = "Edit") }
                    IconButton(onClick = { onDelete(member.id) }) { Icon(Icons.Outlined.Delete, contentDescription = "Delete") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ProfileHero(member = member, connectionCount = connections.size)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(onClick = { addEvent = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Event, contentDescription = null)
                        Text(" Add event", modifier = Modifier.padding(start = 4.dp))
                    }
                    OutlinedButton(onClick = { addRelationship = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Link, contentDescription = null)
                        Text(" Link person", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            item {
                InsightBanner(
                    title = "Profile quality",
                    body = if (member.notes.isNotBlank() || member.events.isNotEmpty()) {
                        "This profile already has meaningful context. Add more events and structured relationships to turn it into a rich family record."
                    } else {
                        "This person has the basics. Add life events, relationship links, and notes to make the profile feel truly complete."
                    },
                    accent = FamyAccent
                )
            }
            item {
                SectionHeader("Overview", "High-signal facts only.")
            }
            item {
                ProfileFactCard(
                    facts = listOf(
                        "Given name" to member.givenName.ifBlank { "Not added" },
                        "Family name" to member.familyName.ifBlank { "Not added" },
                        "Birth" to listOf(member.birthDate.takeIf { it.isNotBlank() }, member.birthPlace.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" • ").ifBlank { "Not added" },
                        "Death" to listOf(member.deathDate.takeIf { it.isNotBlank() }, member.deathPlace.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" • ").ifBlank { if (member.isLiving) "Living" else "Not added" }
                    )
                )
            }
            if (member.notes.isNotBlank()) {
                item {
                    SectionHeader("Notes")
                }
                item {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = member.notes,
                            modifier = Modifier.padding(18.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                SectionHeader("Connections", "Relationships connected to this person.")
            }
            if (relatedMembers.isEmpty()) {
                item {
                    InsightBanner(
                        title = "No linked relatives yet",
                        body = "Use Link person to connect parents, children, spouses, or siblings so this profile becomes part of the wider family graph."
                    )
                }
            } else {
                items(relatedMembers, key = { it.second.id + it.first.name }) { (type, relative) ->
                    MemberRow(
                        member = relative,
                        supportiveText = type.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() },
                        onClick = {}
                    )
                }
            }
            item {
                SectionHeader("Timeline", "Moments that shaped this person.")
            }
            if (member.events.isEmpty()) {
                item {
                    InsightBanner(
                        title = "No events yet",
                        body = "Add a birth, marriage, residence, education, or custom milestone to build a stronger timeline."
                    )
                }
            } else {
                items(member.events, key = { it.id }) { event ->
                    EventCard(event = event)
                }
            }
        }
    }

    if (editMember) {
        MemberEditorDialog(initial = member, onDismiss = { editMember = false }, onSave = { onSave(it); editMember = false })
    }
    if (addEvent) {
        EventDialog(onDismiss = { addEvent = false }, onSave = { onAddEvent(member.id, it); addEvent = false })
    }
    if (addRelationship) {
        RelationshipDialog(member = member, members = state.members, onDismiss = { addRelationship = false }, onSave = { onAddRelationship(it); addRelationship = false })
    }
}

@Composable
private fun ProfileHero(member: FamilyMember, connectionCount: Int) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                MemberAvatar(member, Modifier.size(82.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(member.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        listOf(member.birthDate.takeIf { it.isNotBlank() }, member.birthPlace.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" • ").ifBlank { "Family profile" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(if (member.isLiving) "Living" else "Archive")
                        InfoChip("$connectionCount links")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileFactCard(facts: List<Pair<String, String>>) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            facts.forEach { (label, value) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: LifeEvent) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                listOf(event.date.takeIf { it.isNotBlank() }, event.place.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" • ").ifBlank { event.type.name.replace('_', ' ') },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (event.notes.isNotBlank()) {
                Text(event.notes, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
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
                OutlinedTextField(title, { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(date, { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(place, { place = it }, label = { Text("Place") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(enabled = title.isNotBlank(), onClick = {
                onSave(LifeEvent(type = EventType.CUSTOM, title = title.trim(), date = date.trim(), place = place.trim(), notes = notes.trim()))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RelationshipDialog(member: FamilyMember, members: List<FamilyMember>, onDismiss: () -> Unit, onSave: (Relationship) -> Unit) {
    var otherId by remember { mutableStateOf(members.firstOrNull { it.id != member.id }?.id.orEmpty()) }
    var type by remember { mutableStateOf(RelationshipType.PARENT_CHILD) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link family member") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Primary profile: ${member.displayName}")
                RelationshipTypePicker(type, { type = it })
                MemberPicker("Related member", members.filter { it.id != member.id }, otherId, { otherId = it })
                Text(
                    "For parent-child links, the current profile is treated as the parent and the selected person as the child.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(enabled = otherId.isNotBlank(), onClick = {
                onSave(Relationship(sourceMemberId = member.id, targetMemberId = otherId, type = type))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
