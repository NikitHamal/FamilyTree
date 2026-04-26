package com.famy.tree.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.Gender
import com.famy.tree.data.model.RelationshipType
import java.util.UUID

@Composable
fun SectionHeader(title: String, subtitle: String? = null, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            if (!subtitle.isNullOrBlank()) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (action != null) action()
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MemberAvatar(member: FamilyMember, modifier: Modifier = Modifier) {
    val (icon, tint) = when (member.gender) {
        Gender.FEMALE -> Icons.Outlined.Female to MaterialTheme.colorScheme.tertiary
        Gender.MALE -> Icons.Outlined.Male to MaterialTheme.colorScheme.primary
        else -> Icons.Outlined.Person to MaterialTheme.colorScheme.secondary
    }
    Box(
        modifier = modifier.clip(CircleShape).background(tint.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        if (member.photoUri.isBlank()) {
            Text(member.initials, style = MaterialTheme.typography.labelLarge, color = tint, fontWeight = FontWeight.Bold)
        } else {
            Icon(icon, contentDescription = null, tint = tint)
        }
    }
}

@Composable
fun MemberRow(member: FamilyMember, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MemberAvatar(member, Modifier.size(48.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val detail = listOf(member.birthDate.takeIf { it.isNotBlank() }?.let { "Born $it" }, member.birthPlace.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" • ")
                Text(detail.ifBlank { if (member.isLiving) "Living" else "Deceased" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!member.isLiving) AssistChip(onClick = {}, label = { Text("Deceased") })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberEditorDialog(
    initial: FamilyMember?,
    onDismiss: () -> Unit,
    onSave: (FamilyMember) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial?.displayName.orEmpty()) }
    var given by remember(initial) { mutableStateOf(initial?.givenName.orEmpty()) }
    var family by remember(initial) { mutableStateOf(initial?.familyName.orEmpty()) }
    var gender by remember(initial) { mutableStateOf(initial?.gender ?: Gender.UNKNOWN) }
    var birthDate by remember(initial) { mutableStateOf(initial?.birthDate.orEmpty()) }
    var birthPlace by remember(initial) { mutableStateOf(initial?.birthPlace.orEmpty()) }
    var deathDate by remember(initial) { mutableStateOf(initial?.deathDate.orEmpty()) }
    var notes by remember(initial) { mutableStateOf(initial?.notes.orEmpty()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add family member" else "Edit family member") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Display name") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(given, { given = it }, label = { Text("Given") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(family, { family = it }, label = { Text("Family") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = gender.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        Gender.entries.forEach { item ->
                            DropdownMenuItem(text = { Text(item.name.replace('_', ' ')) }, onClick = { gender = item; expanded = false })
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(birthDate, { birthDate = it }, label = { Text("Birth date") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(deathDate, { deathDate = it }, label = { Text("Death date") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(birthPlace, { birthPlace = it }, label = { Text("Birth place") }, singleLine = true)
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(enabled = name.isNotBlank(), onClick = {
                val base = initial ?: FamilyMember(id = UUID.randomUUID().toString(), displayName = name.trim())
                onSave(base.copy(displayName = name.trim(), givenName = given, familyName = family, gender = gender, birthDate = birthDate, birthPlace = birthPlace, deathDate = deathDate, notes = notes))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipTypePicker(value: RelationshipType, onValueChange: (RelationshipType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value.name.replace('_', ' '),
            onValueChange = {},
            readOnly = true,
            label = { Text("Relationship") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RelationshipType.entries.forEach { type ->
                DropdownMenuItem(text = { Text(type.name.replace('_', ' ')) }, onClick = { onValueChange(type); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberPicker(label: String, members: List<FamilyMember>, selectedId: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selected = members.firstOrNull { it.id == selectedId }?.displayName.orEmpty()
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            members.forEach { member ->
                DropdownMenuItem(text = { Text(member.displayName) }, onClick = { onSelected(member.id); expanded = false })
            }
        }
    }
}

@Composable
fun EmptyState(title: String, body: String, action: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        action()
    }
}
