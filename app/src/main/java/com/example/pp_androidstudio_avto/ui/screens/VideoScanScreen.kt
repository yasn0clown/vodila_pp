package com.example.pp_androidstudio_avto.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.nio.ByteBuffer

@Composable
fun VideoScanScreen(
    viewModel: VideoScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Launcher для запроса разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.onCameraPermissionResult(isGranted)
        if (isGranted) {
            Log.i("VideoScanScreen", "Camera permission GRANTED")
            // Разрешение получено, можно пробовать стартовать камеру, если монитор инициализирован
            if (uiState.isMonitorInitialized) {
                // viewModel.startCamera(...) // Эту функцию добавим в ViewModel
            }
        } else {
            Log.e("VideoScanScreen", "Camera permission DENIED")
            // Обработать отказ в разрешении (показать сообщение пользователю)
        }
    }

    // Проверка и запрос разрешений при первом входе на экран или если разрешение еще не дано
    LaunchedEffect(key1 = true) { // key1 = true означает, что эффект запустится один раз при композиции
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                Log.d("VideoScanScreen", "Camera permission already granted.")
                viewModel.onCameraPermissionResult(true)
            }
            else -> {
                Log.d("VideoScanScreen", "Requesting camera permission.")
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Чтобы камера была сверху, а кнопки внизу
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column( // Верхняя часть для информации и превью
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f) // Занимает доступное пространство
        ) {
            Text("Экран сканирования видео", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))

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
                Text("Система мониторинга инициализирована!", color = Color(0xFF008000))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Область для превью камеры
            if (uiState.cameraPermissionGranted && uiState.isMonitorInitialized) {
                CameraPreview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f) // Соотношение сторон, подбери под себя
                        .padding(vertical = 8.dp),
                    onFrameAnalyzed = {
                        // TODO: Сюда будем передавать кадры в ViewModel для JNI
                        // Log.d("VideoScanScreen", "Frame received: ${it.width}x${it.height}, ts: ${it.imageInfo.timestamp}")
                    },
                    viewModel = viewModel // Передаем ViewModel для управления камерой
                )
            } else if (!uiState.cameraPermissionGranted && uiState.isMonitorInitialized) {
                Text("Для отображения превью камеры необходимо разрешение.")
            } else {
                Text("Ожидание инициализации и разрешений для камеры...")
            }
        }


        // Нижняя часть для кнопок управления
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            // Кнопки управления (пока простые)
            if (uiState.isMonitorInitialized) {
                if (uiState.isMonitoringActive) {
                    Button(onClick = {
                        viewModel.stopMonitoring()
                        // Также нужно остановить камеру здесь или в ViewModel
                    }) {
                        Text("Остановить мониторинг")
                    }
                } else {
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                viewModel.startMonitoring()
                                // Также нужно запустить камеру здесь или в ViewModel
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA) // Запросить снова, если вдруг нет
                            }
                        },
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
        }
    }
}

// Новый Composable для превью камеры
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA, // Используем фронтальную камеру
    onFrameAnalyzed: (androidx.camera.core.ImageProxy) -> Unit,
    viewModel: VideoScanViewModel // Для управления жизненным циклом камеры из ViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Используем State из ViewModel для управления запуском/остановкой камеры
    val uiState by viewModel.uiState.collectAsState()
    val isMonitoringActive = uiState.isMonitoringActive

    // Поздняя инициализация для PreviewView, чтобы не создавать его без необходимости
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }


    LaunchedEffect(key1 = isMonitoringActive, key2 = previewView) {
        if (previewView == null) return@LaunchedEffect

        if (isMonitoringActive) {
            Log.d("CameraPreview", "Attempting to start camera.")
            cameraExecutor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView!!.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    // Если известно, что JNI будет принимать только определенный размер, можно его задать:
                    // .setTargetResolution(Size(640, 480)) // Убедитесь, что камера поддерживает
                    .build()
                    .also { analyzer -> // переименовал 'it' в 'analyzer' для ясности
                        analyzer.setAnalyzer(cameraExecutor!!) { imageProxy ->
                            try {
                                if (viewModel.uiState.value.isMonitoringActive) {
                                    val originalPlanes = imageProxy.planes
                                    val format = imageProxy.format
                                    val width = imageProxy.width
                                    val height = imageProxy.height
                                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                    val timestampNanos = imageProxy.imageInfo.timestamp

                                    // Создаем копии PlaneProxy с скопированными данными буфера
                                    val copiedPlanes = originalPlanes.mapNotNull { originalPlane ->
                                        // Проверяем, есть ли данные в буфере
                                        if (originalPlane.buffer == null || originalPlane.buffer.remaining() == 0) {
                                            Log.w("CameraPreviewAnalyzer", "Original plane buffer is null or empty, skipping this plane.")
                                            return@mapNotNull null // Пропускаем этот plane, если буфер пуст
                                        }

                                        val bufferData = ByteArray(originalPlane.buffer.remaining())
                                        originalPlane.buffer.get(bufferData) // Копируем данные в ByteArray

                                        // Создаем объект, реализующий ImageProxy.PlaneProxy
                                        object : ImageProxy.PlaneProxy {
                                            private val copiedBuffer: ByteBuffer = ByteBuffer.wrap(bufferData) // Оборачиваем ByteArray в ByteBuffer
                                            private val pixelStrideValue: Int = originalPlane.pixelStride
                                            private val rowStrideValue: Int = originalPlane.rowStride

                                            override fun getBuffer(): ByteBuffer = copiedBuffer.asReadOnlyBuffer() // Возвращаем read-only для безопасности
                                            override fun getPixelStride(): Int = pixelStrideValue
                                            override fun getRowStride(): Int = rowStrideValue
                                        }
                                    }.toTypedArray<ImageProxy.PlaneProxy>() // Явно указываем тип

                                    // Если после фильтрации не осталось валидных planes, возможно, не стоит создавать FrameData
                                    if (copiedPlanes.size != originalPlanes.size && originalPlanes.isNotEmpty()) {
                                        Log.w("CameraPreviewAnalyzer", "Some planes were skipped due to empty buffers. Original: ${originalPlanes.size}, Copied: ${copiedPlanes.size}")
                                    }
                                    // Если все planes были пустыми или произошла ошибка, copiedPlanes может быть пустым.
                                    // Решите, как обрабатывать этот случай. Для простоты пока оставим так.


                                    val frameData = VideoScanViewModel.FrameData(
                                        planes = copiedPlanes,
                                        format = format,
                                        width = width,
                                        height = height,
                                        rotationDegrees = rotationDegrees,
                                        timestampNanos = timestampNanos
                                    )
                                    viewModel.processFrameData(frameData)
                                }
                            } catch (e: Exception) {
                                Log.e("CameraPreviewAnalyzer", "Error processing image proxy: ${e.message}", e)
                            } finally {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider?.unbindAll() // Отвязываем предыдущие биндинги
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer // Добавляем ImageAnalysis
                    )
                    Log.i("CameraPreview", "Camera bound to lifecycle.")
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                    viewModel.handleCameraError("Use case binding failed: ${exc.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        } else {
            Log.d("CameraPreview", "Attempting to stop camera.")
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
            cameraExecutor = null
            cameraProvider = null // Сбрасываем, чтобы при следующем старте получить новый
            Log.i("CameraPreview", "Camera unbound and executor shutdown.")
        }
    }

    // AndroidView для интеграции PreviewView из CameraX
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER // или FIT_CENTER
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }.also {
                previewView = it
            }
        },
        modifier = modifier
    )

    // Освобождение ресурсов при выходе с экрана
    DisposableEffect(Unit) {
        onDispose {
            Log.d("CameraPreview", "Disposing CameraPreview. Shutting down executor.")
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
        }
    }
}