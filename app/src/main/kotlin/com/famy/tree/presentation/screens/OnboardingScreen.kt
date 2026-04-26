package com.famy.tree.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.OfflineBolt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPage(val eyebrow: String, val title: String, val body: String, val icon: ImageVector)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            eyebrow = "Premium family workspace",
            title = "A calmer, more intentional family tree app",
            body = "Famy now feels closer to a modern productivity tool: cleaner hierarchy, better spacing, and a more editorial sense of polish.",
            icon = Icons.Outlined.AutoAwesome
        ),
        OnboardingPage(
            eyebrow = "Tree experience",
            title = "Explore family structure without visual chaos",
            body = "Switch layouts, focus on a specific person, and navigate a more refined tree canvas built for clarity instead of clutter.",
            icon = Icons.Outlined.AccountTree
        ),
        OnboardingPage(
            eyebrow = "Rich profiles",
            title = "Turn every person into a meaningful record",
            body = "Profiles now make room for events, notes, and relationships in a way that feels thoughtful, minimal, and easier to extend.",
            icon = Icons.Outlined.Favorite
        ),
        OnboardingPage(
            eyebrow = "Private and portable",
            title = "Offline by default, export when you need it",
            body = "Your data stays on-device with JSON backup and GEDCOM portability so the app remains practical as well as elegant.",
            icon = Icons.Outlined.OfflineBolt
        )
    )
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text("Welcome to Famy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("A better foundation for family history.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        }

        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { index ->
            val page = pages[index]
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(page.icon, contentDescription = null, modifier = Modifier.size(40.dp))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(page.eyebrow, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(page.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                            Text(page.body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    InsightRail(index + 1, pages.size)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pager.currentPage == index) 24.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pager.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (pager.currentPage < pages.lastIndex) {
                OutlinedButton(onClick = onComplete, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Backup, contentDescription = null)
                    Text(" Skip", modifier = Modifier.padding(start = 4.dp))
                }
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (pager.currentPage == pages.lastIndex) onComplete()
                    else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                }
            ) {
                Text(if (pager.currentPage == pages.lastIndex) "Start building" else "Continue")
            }
        }
    }
}

@Composable
private fun InsightRail(current: Int, total: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Step $current of $total", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "Designed to feel more mature, calm, and high-end from the first screen onward.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }
}
