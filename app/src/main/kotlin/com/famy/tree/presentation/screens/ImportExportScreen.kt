package com.famy.tree.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.model.FamilyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    state: FamilyState,
    exportJson: ((String) -> Unit) -> Unit,
    importJson: (String) -> Unit,
    exportGedcom: ((String) -> Unit) -> Unit,
    importGedcom: (String) -> Unit
) {
    val context = LocalContext.current
    var buffer by remember { mutableStateOf("") }
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Import / Export") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Portable backups", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("JSON preserves all Famy data. GEDCOM supports core member fields for genealogy tools.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { exportJson { text -> buffer = text; copy(context, text) } }) { Text("Export JSON") }
                            OutlinedButton(onClick = { exportGedcom { text -> buffer = text; copy(context, text) } }) { Text("Export GEDCOM") }
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = buffer,
                    onValueChange = { buffer = it },
                    label = { Text("Paste JSON backup or GEDCOM here") },
                    minLines = 10,
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { importJson(buffer) }, enabled = buffer.trim().startsWith("{")) { Text("Import JSON") }
                    OutlinedButton(onClick = { importGedcom(buffer) }, enabled = buffer.contains("0 HEAD")) { Text("Import GEDCOM") }
                }
            }
        }
    }
}

private fun copy(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Famy export", text))
}
