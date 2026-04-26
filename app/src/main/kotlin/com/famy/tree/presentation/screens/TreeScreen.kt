package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.FilterCenterFocus
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.famy.tree.data.FamilyStats
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.components.InfoChip
import com.famy.tree.presentation.components.MemberPicker
import com.famy.tree.presentation.components.SectionHeader
import com.famy.tree.presentation.components.StatCard
import com.famy.tree.presentation.tree.FamilyTreeCanvas
import com.famy.tree.presentation.tree.TreeLayoutMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeScreen(
    state: FamilyState,
    onOpenDrawer: () -> Unit,
    onMemberClick: (String) -> Unit,
    onSetRootMember: (String?) -> Unit
) {
    var mode by remember { mutableStateOf(TreeLayoutMode.PEDIGREE) }
    val rootId = state.tree.rootMemberId ?: state.members.firstOrNull()?.id.orEmpty()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Family tree", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        Text("Cleaner layout, better focus, smoother exploration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Outlined.Menu, contentDescription = "Menu") } }
            )
        }
    ) { padding ->
        if (state.members.isEmpty()) {
            Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState(
                    title = "No tree yet",
                    body = "Add members and relationships to unlock the upgraded tree view."
                ) {}
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        StatCard("People", FamilyStats.totalMembers(state).toString(), Icons.Outlined.PeopleAlt, Modifier.weight(1f))
                        StatCard("Generations", FamilyStats.generationCount(state).toString(), Icons.Outlined.AccountTree, Modifier.weight(1f))
                    }
                    SectionHeader("View controls", "Choose a layout and optionally focus on a person.")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TreeLayoutMode.entries.forEach { option ->
                            FilterChip(
                                selected = mode == option,
                                onClick = { mode = option },
                                label = { Text(option.name.lowercase().replaceFirstChar { it.titlecase() }) }
                            )
                        }
                    }
                    if (state.members.isNotEmpty()) {
                        MemberPicker(
                            label = "Focus person",
                            members = state.members.sortedBy { it.displayName },
                            selectedId = rootId,
                            onSelected = onSetRootMember
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip("Blue = paternal")
                        InfoChip("Pink = maternal")
                        InfoChip("Tap a card to open profile")
                    }
                    OutlinedButton(onClick = { onSetRootMember(null) }) {
                        Icon(Icons.Outlined.FilterCenterFocus, contentDescription = null)
                        Text(" Reset focus", modifier = Modifier.padding(start = 4.dp))
                    }
                }
                FamilyTreeCanvas(
                    state = state,
                    mode = mode,
                    modifier = Modifier.weight(1f),
                    onMemberClick = onMemberClick
                )
            }
        }
    }
}
