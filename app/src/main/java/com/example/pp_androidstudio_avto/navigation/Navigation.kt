package com.example.pp_androidstudio_avto.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object VideoScan : Screen("video_scan", "Сканирование видео", Icons.Filled.Videocam)
    object Statistics : Screen("statistics", "Статистика", Icons.Filled.BarChart)
    object Settings : Screen("settings", "Настройки", Icons.Filled.Settings)
    object About : Screen("about", "О разработчиках", Icons.Filled.Info)
}

val drawerScreens = listOf(
    Screen.VideoScan,
    Screen.Statistics,
    Screen.Settings,
    Screen.About
)