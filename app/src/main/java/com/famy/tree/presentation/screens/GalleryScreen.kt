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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyState
import com.famy.tree.data.model.MediaItem
import com.famy.tree.presentation.components.MemberPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(state: FamilyState, onAddMedia: (MediaItem) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("Media Gallery") }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Outlined.Add, contentDescription = "Add") } }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.media, key = { it.id }) { media ->
                val person = state.members.firstOrNull { it.id == media.memberId }?.displayName
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(media.title, fontWeight = FontWeight.SemiBold)
                        Text(media.uri, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (person != null) Text("Linked to $person", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
    if (showDialog) MediaDialog(state, onDismiss = { showDialog = false }, onSave = { onAddMedia(it); showDialog = false })
}

@Composable
private fun MediaDialog(state: FamilyState, onDismiss: () -> Unit, onSave: (MediaItem) -> Unit) {
    var title by remember { mutableStateOf("") }
    var uri by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf(state.members.firstOrNull()?.id.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add media reference") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Title") })
                OutlinedTextField(uri, { uri = it }, label = { Text("URI or file path") })
                MemberPicker("Linked member", state.members, memberId, { memberId = it })
            }
        },
        confirmButton = { Button(enabled = title.isNotBlank() && uri.isNotBlank(), onClick = { onSave(MediaItem(title = title, uri = uri, memberId = memberId.takeIf { it.isNotBlank() })) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
