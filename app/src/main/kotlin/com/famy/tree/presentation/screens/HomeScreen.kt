package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.famy.tree.data.model.FamilyMember
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.EmptyState
import com.famy.tree.presentation.components.InfoChip
import com.famy.tree.presentation.components.InsightBanner
import com.famy.tree.presentation.components.MemberEditorDialog
import com.famy.tree.presentation.components.MemberRow
import com.famy.tree.presentation.components.QuickActionCard
import com.famy.tree.presentation.components.SectionHeader
import com.famy.tree.presentation.components.StatCard
import com.famy.tree.presentation.theme.FamyAccent
import com.famy.tree.presentation.theme.FamySecondary

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
    val eventCount = state.members.sumOf { it.events.size }
    val recentMembers = FamilyStats.recentMembers(state, limit = 6)
    val commonNames = FamilyStats.commonNames(state)
    val averageLifespan = FamilyStats.averageLifespan(state)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Workspace", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(state.tree.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEditor = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add member")
            }
        }
    ) { padding ->
        if (state.members.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                EmptyState(
                    title = "Build a family archive that feels premium",
                    body = "Start with your first family member, or load the demo tree to explore the new dashboard, cleaner tree view, and better profile experience."
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { showEditor = true }) { Text("Add first person") }
                        OutlinedButton(onClick = onAddDemo) { Text("Load demo") }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HeroOverviewCard(
                        memberCount = FamilyStats.totalMembers(state),
                        generationCount = FamilyStats.generationCount(state),
                        treeName = state.tree.name,
                        onOpenTree = onOpenTree,
                        onAddMember = { showEditor = true }
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("People", FamilyStats.totalMembers(state).toString(), Icons.Outlined.Groups, Modifier.weight(1f))
                        StatCard("Generations", FamilyStats.generationCount(state).toString(), Icons.Outlined.AccountTree, Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("Living", FamilyStats.livingCount(state).toString(), Icons.Outlined.PersonAddAlt1, Modifier.weight(1f))
                        StatCard("Events", eventCount.toString(), Icons.Outlined.Timeline, Modifier.weight(1f))
                    }
                }
                item {
                    SectionHeader("Quick actions", "A cleaner, faster flow to keep your archive moving.")
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        QuickActionCard(
                            title = "Open tree",
                            subtitle = "Pan, zoom, switch layouts and focus on branches.",
                            icon = Icons.Outlined.AccountTree,
                            modifier = Modifier.weight(1f),
                            emphasized = true,
                            onClick = onOpenTree
                        )
                        QuickActionCard(
                            title = "Add person",
                            subtitle = "Capture someone new with cleaner profile fields.",
                            icon = Icons.Outlined.PersonAddAlt1,
                            modifier = Modifier.weight(1f),
                            onClick = { showEditor = true }
                        )
                    }
                }
                if (commonNames.isNotEmpty()) {
                    item {
                        SectionHeader("Patterns in this family", "Useful signals surfaced automatically.")
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            commonNames.take(3).forEach { (name, count) ->
                                InfoChip("$name · $count")
                            }
                        }
                    }
                }
                item {
                    InsightBanner(
                        title = averageLifespan?.let { "Average recorded lifespan: $it years" } ?: "Make the tree feel richer",
                        body = if (averageLifespan != null) {
                            "The archive already contains enough dates to estimate longevity. Add more life events and relationships to surface deeper family patterns."
                        } else {
                            "Add birth and death years, pair spouses, and connect parents to children to unlock richer insights and more meaningful tree layouts."
                        },
                        accent = FamyAccent
                    )
                }
                item {
                    InsightBanner(
                        title = "Performance tuned for growth",
                        body = "The updated tree canvas keeps navigation lighter and clearer, even as the family grows across multiple generations.",
                        accent = FamySecondary
                    )
                }
                item {
                    SectionHeader("Recently updated people", "Jump back into the members you touched most recently.")
                }
                items(recentMembers, key = { it.id }) { member ->
                    MemberRow(member = member, onClick = { onOpenMember(member.id) })
                }
            }
        }
    }

    if (showEditor) {
        MemberEditorDialog(
            initial = null,
            onDismiss = { showEditor = false },
            onSave = {
                onAddMember(it)
                showEditor = false
            }
        )
    }
}

@Composable
private fun HeroOverviewCard(
    memberCount: Int,
    generationCount: Int,
    treeName: String,
    onOpenTree: () -> Unit,
    onAddMember: () -> Unit
) {
    androidx.compose.material3.Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("Minimal family workspace")
                Text(
                    text = treeName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "A more editorial, Notion-inspired dashboard for people, branches, relationships, and memory keeping.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(memberCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                    Text("People in archive", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(generationCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                    Text("Generations mapped", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onOpenTree, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null)
                    Text(" Explore tree", modifier = Modifier.padding(start = 4.dp))
                }
                OutlinedButton(onClick = onAddMember, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Bolt, contentDescription = null)
                    Text(" Add person", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
