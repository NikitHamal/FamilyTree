package com.famy.tree.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val TREE = "tree"
    const val MEMBERS = "members"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val RELATIONSHIPS = "relationships"
    const val TIMELINE = "timeline"
    const val INSIGHTS = "insights"
    const val GALLERY = "gallery"
    const val IMPORT_EXPORT = "import_export"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

val BottomDestinations = listOf(
    BottomDestination(Routes.HOME, "Home", Icons.Outlined.Home),
    BottomDestination(Routes.TREE, "Tree", Icons.Outlined.AccountTree),
    BottomDestination(Routes.MEMBERS, "Members", Icons.Outlined.Groups),
    BottomDestination(Routes.SEARCH, "Search", Icons.Outlined.Search)
)
