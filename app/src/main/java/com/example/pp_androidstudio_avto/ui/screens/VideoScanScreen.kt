package com.example.pp_androidstudio_avto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Важный импорт

@Composable
fun VideoScanScreen(
    // Если ViewModel создается с Application, то фабрика по умолчанию справится
    viewModel: VideoScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current // Может понадобиться для запроса разрешений

    // LaunchedEffect для одноразовых действий при входе на экран, например, запрос разрешений.
    // Пока оставим пустым, разрешениями займемся на следующем шаге.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Экран сканирования видео", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Статус инициализации монитора
        if (uiState.monitorInitializationError != null) {
            Text(
                "Ошибка инициализации: ${uiState.monitorInitializationError}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (!uiState.isMonitorInitialized) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Инициализация системы мониторинга...")
            }
        } else {
            Text("Система мониторинга инициализирована успешно!", color = Color(0xFF008000)) // Зеленый цвет
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопки управления (пока простые)
        if (uiState.isMonitorInitialized) {
            if (uiState.isMonitoringActive) {
                Button(onClick = { viewModel.stopMonitoring() }) {
                    Text("Остановить мониторинг")
                }
            } else {
                Button(
                    onClick = { viewModel.startMonitoring() },
                    enabled = uiState.cameraPermissionGranted // Активируем, только если есть разрешение
                ) {
                    Text("Начать мониторинг")
                }
                if (!uiState.cameraPermissionGranted) {
                    Text(
                        "Для начала мониторинга необходимо разрешение на использование камеры.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Отображение последних обнаружений (заглушка)
        if (uiState.lastDetections.isNotEmpty()) {
            Text("Последние обнаружения:", style = MaterialTheme.typography.titleMedium)
            uiState.lastDetections.forEach { detection ->
                Text("- $detection")
            }
        }

        // TODO: Здесь будет PreviewView для камеры
        // TODO: Здесь будет запрос разрешений на камеру
    }
}