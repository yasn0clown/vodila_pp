package com.example.pp_androidstudio_avto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSwitcher(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Сменить тему") })
        },
        content = { padding ->
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme() },
                modifier = Modifier.padding(padding)
            )
        }
    )
}

