package com.example.pp_androidstudio_avto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.pp_androidstudio_avto.navigation.Screen
import com.example.pp_androidstudio_avto.navigation.drawerScreens
import com.example.pp_androidstudio_avto.ui.screens.*
import com.example.pp_androidstudio_avto.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            // Для отслеживания текущего экрана для заголовка TopAppBar
            val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route?.let { route ->
                drawerScreens.find { it.route == route }
            } ?: Screen.VideoScan // Экран по умолчанию

            MyAppTheme(darkTheme = isDarkTheme) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerContent(
                            navController = navController,
                            currentRoute = currentScreen.route,
                            onCloseDrawer = {
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(currentScreen.title) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                        }
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Открыть меню")
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(paddingValues),
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppDrawerContent(
    navController: NavHostController,
    currentRoute: String,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Column {
            // Можно добавить заголовок для Drawer
            Text(
                "Меню",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Divider()
            drawerScreens.forEach { screen ->
                NavigationDrawerItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                // Очищаем бэкстек до начального экрана, чтобы избежать накопления
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true // Избегаем нескольких копий одного экрана
                            }
                        }
                        onCloseDrawer()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.VideoScan.route, // Начальный экран
        modifier = modifier
    ) {
        composable(Screen.VideoScan.route) {
            VideoScanScreen()
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
        }
        composable(Screen.About.route) {
            AboutScreen()
        }
    }
}

// Твоя функция Greeting остается, если она нужна, но пока не используется в основном UI
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}