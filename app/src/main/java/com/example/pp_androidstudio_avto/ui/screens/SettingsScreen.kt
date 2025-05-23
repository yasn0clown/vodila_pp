package com.example.pp_androidstudio_avto.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pp_androidstudio_avto.ui.ThemeSwitcher
import androidx.compose.material3.Text

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Используем твой существующий ThemeSwitcher, но без Scaffold внутри него
        // ThemeSwitcher(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
        // Давай немного адаптируем ThemeSwitcher или создадим новый для настроек
        SimpleThemeSwitcher(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
    }
}

// Добавь эту упрощенную версию или модифицируй свой ThemeSwitcher
// чтобы он не создавал собственный Scaffold, если он будет внутри другого Scaffold
@Composable
fun SimpleThemeSwitcher(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text("Тёмная тема")
        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        androidx.compose.material3.Switch(
            checked = isDarkTheme,
            onCheckedChange = { onToggleTheme() }
        )
    }
}