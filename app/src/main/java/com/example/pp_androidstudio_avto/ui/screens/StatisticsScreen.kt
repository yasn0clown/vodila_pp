package com.example.pp_androidstudio_avto.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticsScreen(viewModel: VideoScanViewModel) { // Принимаем ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val violationStats = uiState.violationStats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Статистика нарушений",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (violationStats.isEmpty()) {
            Text(
                "Пока нет данных о нарушениях.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f), // Занимает доступное место
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(violationStats.toList()) { (type, count) -> // Конвертируем Map в List для LazyColumn
                    StatisticItem(violationType = type, count = count)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.resetStatistics() }) {
            Text("Сбросить статистику")
        }
    }
}

@Composable
fun StatisticItem(violationType: String, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatViolationType(violationType), // Красивое отображение типа
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Вспомогательная функция для "красивого" отображения типа нарушения
fun formatViolationType(type: String): String {
    return when (type.uppercase()) { // Приводим к верхнему регистру для надежности
        "DROWSINESS" -> "Сонливость"
        "DISTRACTION" -> "Отвлечение"
        "PHONE_CALL" -> "Разговор по телефону"
        // Добавь другие типы, если они есть
        else -> type // Если тип неизвестен, отображаем как есть
    }
}