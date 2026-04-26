package com.famy.tree.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
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
import com.famy.tree.presentation.components.MemberRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(state: FamilyState, onOpenMember: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var livingOnly by remember { mutableStateOf(false) }
    val results = remember(query, livingOnly, state.members) {
        state.members.filter { member ->
            val text = listOf(member.displayName, member.givenName, member.familyName, member.birthPlace, member.notes).joinToString(" ").lowercase()
            text.contains(query.lowercase()) && (!livingOnly || member.isLiving)
        }.sortedBy { it.displayName }
    }
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Search") }) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 12.dp, 20.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search names, places, notes") }) }
            item { FilterChip(selected = livingOnly, onClick = { livingOnly = !livingOnly }, label = { Text("Living only") }) }
            items(results, key = { it.id }) { member -> MemberRow(member, onClick = { onOpenMember(member.id) }) }
        }
    }
}
