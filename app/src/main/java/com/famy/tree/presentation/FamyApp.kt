package com.famy.tree.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.famy.tree.presentation.navigation.BottomDestinations
import com.famy.tree.presentation.navigation.Routes
import com.famy.tree.presentation.screens.GalleryScreen
import com.famy.tree.presentation.screens.HelpScreen
import com.famy.tree.presentation.screens.HomeScreen
import com.famy.tree.presentation.screens.ImportExportScreen
import com.famy.tree.presentation.screens.InsightsScreen
import com.famy.tree.presentation.screens.MemberProfileScreen
import com.famy.tree.presentation.screens.MembersScreen
import com.famy.tree.presentation.screens.OnboardingScreen
import com.famy.tree.presentation.screens.RelationshipsScreen
import com.famy.tree.presentation.screens.SearchScreen
import com.famy.tree.presentation.screens.SettingsScreen
import com.famy.tree.presentation.screens.TimelineScreen
import com.famy.tree.presentation.screens.TreeScreen
import com.famy.tree.presentation.theme.FamyTheme
import kotlinx.coroutines.launch

@Composable
fun FamyApp(viewModel: FamyViewModel) {
    val ui by viewModel.state.collectAsState()
    FamyTheme(preference = ui.familyState.settings.themePreference) {
        if (ui.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@FamyTheme
        }
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val start = if (ui.familyState.settings.onboardingComplete) Routes.HOME else Routes.ONBOARDING

        LaunchedEffect(ui.message) {
            val message = ui.message
            if (message != null) {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("Famy", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(24.dp))
                    DrawerItem("Relationships", Icons.Outlined.Link, Routes.RELATIONSHIPS, navController, drawerState, scope)
                    DrawerItem("Timeline", Icons.Outlined.Timeline, Routes.TIMELINE, navController, drawerState, scope)
                    DrawerItem("Insights", Icons.Outlined.Insights, Routes.INSIGHTS, navController, drawerState, scope)
                    DrawerItem("Media Gallery", Icons.Outlined.PhotoLibrary, Routes.GALLERY, navController, drawerState, scope)
                    DrawerItem("Import / Export", Icons.Outlined.FileUpload, Routes.IMPORT_EXPORT, navController, drawerState, scope)
                    DrawerItem("Settings", Icons.Outlined.Settings, Routes.SETTINGS, navController, drawerState, scope)
                    DrawerItem("Help", Icons.Outlined.HelpOutline, Routes.HELP, navController, drawerState, scope)
                }
            }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    val route = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (route != Routes.ONBOARDING) {
                        NavigationBar {
                            BottomDestinations.forEach { destination ->
                                NavigationBarItem(
                                    selected = route == destination.route,
                                    onClick = { navController.navigate(destination.route) { launchSingleTop = true } },
                                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(navController = navController, startDestination = start, modifier = Modifier.padding(innerPadding)) {
                    composable(Routes.ONBOARDING) { OnboardingScreen(onComplete = { viewModel.completeOnboarding(); navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }) }
                    composable(Routes.HOME) { HomeScreen(ui.familyState, onOpenDrawer = { scope.launch { drawerState.open() } }, onAddDemo = viewModel::addDemoTree, onAddMember = viewModel::saveMember, onOpenMember = { navController.navigate("${Routes.PROFILE}/$it") }, onOpenTree = { navController.navigate(Routes.TREE) }) }
                    composable(Routes.TREE) { TreeScreen(ui.familyState, onOpenDrawer = { scope.launch { drawerState.open() } }, onMemberClick = { navController.navigate("${Routes.PROFILE}/$it") }) }
                    composable(Routes.MEMBERS) { MembersScreen(ui.familyState, onAddMember = viewModel::saveMember, onOpenMember = { navController.navigate("${Routes.PROFILE}/$it") }) }
                    composable(Routes.SEARCH) { SearchScreen(ui.familyState, onOpenMember = { navController.navigate("${Routes.PROFILE}/$it") }) }
                    composable("${Routes.PROFILE}/{memberId}", arguments = listOf(navArgument("memberId") { type = NavType.StringType })) { entry ->
                        MemberProfileScreen(
                            state = ui.familyState,
                            memberId = entry.arguments?.getString("memberId").orEmpty(),
                            onBack = { navController.popBackStack() },
                            onSave = viewModel::saveMember,
                            onDelete = { viewModel.removeMember(it); navController.popBackStack() },
                            onAddEvent = viewModel::addEvent,
                            onAddRelationship = viewModel::addRelationship
                        )
                    }
                    composable(Routes.RELATIONSHIPS) { RelationshipsScreen(ui.familyState, viewModel::addRelationship, viewModel::removeRelationship) }
                    composable(Routes.TIMELINE) { TimelineScreen(ui.familyState) }
                    composable(Routes.INSIGHTS) { InsightsScreen(ui.familyState) }
                    composable(Routes.GALLERY) { GalleryScreen(ui.familyState, viewModel::addMedia) }
                    composable(Routes.IMPORT_EXPORT) { ImportExportScreen(ui.familyState, viewModel::exportJson, viewModel::importJson, viewModel::exportGedcom, viewModel::importGedcom) }
                    composable(Routes.SETTINGS) { SettingsScreen(ui.familyState.settings, viewModel::setTheme, viewModel::setCompactCards, viewModel::setDateFormat, viewModel::clearAll) }
                    composable(Routes.HELP) { HelpScreen() }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    route: String,
    navController: androidx.navigation.NavHostController,
    drawerState: androidx.compose.material3.DrawerState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, contentDescription = label) },
        selected = false,
        onClick = {
            navController.navigate(route) { launchSingleTop = true }
            scope.launch { drawerState.close() }
        },
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}
