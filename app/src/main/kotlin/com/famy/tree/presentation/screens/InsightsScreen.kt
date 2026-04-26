package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.famy.tree.data.FamilyStats
import com.famy.tree.data.model.FamilyState
import com.famy.tree.presentation.components.SectionHeader
import com.famy.tree.presentation.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(state: FamilyState) {
    val generationMap = remember(state.members, state.relationships) { FamilyStats.generationMap(state) }
    val byGeneration = generationMap.values.groupingBy { it }.eachCount().toSortedMap()
    val max = byGeneration.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Insights") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Members", FamilyStats.totalMembers(state).toString(), Icons.Outlined.Groups, Modifier.weight(1f))
                    StatCard("Generations", FamilyStats.generationCount(state).toString(), Icons.Outlined.AccountTree, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Avg lifespan", FamilyStats.averageLifespan(state)?.let { "$it yrs" } ?: "--", Icons.Outlined.Cake, Modifier.weight(1f))
                    StatCard("Living", FamilyStats.livingCount(state).toString(), Icons.Outlined.QueryStats, Modifier.weight(1f))
                }
            }
            item { SectionHeader("Generation breakdown") }
            items(byGeneration.entries.toList()) { entry ->
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Generation ${entry.key + 1}", fontWeight = FontWeight.SemiBold)
                        LinearProgressIndicator(progress = { entry.value / max.toFloat() }, modifier = Modifier.fillMaxWidth())
                        Text("${entry.value} members", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { SectionHeader("Common names") }
            items(FamilyStats.commonNames(state)) { pair ->
                Card(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                    Text("${pair.first}: ${pair.second}", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
