package com.example.pp_androidstudio_avto.ui.screens

import android.app.Application // Используем Application для доступа к Context
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pp_androidstudio_avto.drivermonitor.DriverMonitorBridge
import android.graphics.ImageFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.io.IOException
import java.nio.ByteBuffer

// Состояния для UI
data class VideoScanUiState(
    val isMonitorInitialized: Boolean = false,
    val monitorInitializationError: String? = null,
    val isMonitoringActive: Boolean = false,
    val lastDetections: List<String> = emptyList(), // Для текущих отображений на VideoScanScreen
    val cameraPermissionGranted: Boolean = false,
    val violationStats: Map<String, Int> = emptyMap() // Статистика нарушений (Тип -> Количество)
)

class VideoScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoScanUiState())
    val uiState: StateFlow<VideoScanUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "VideoScanViewModel"
        // Имена файлов графов, лучше вынести в константы или получать извне
        private const val FACE_MESH_GRAPH_FILE = "face_mesh_desktop_live.pbtxt"
        private const val HAND_TRACKING_GRAPH_FILE = "hand_tracking_desktop_live.pbtxt" // Если не используется, можно оставить null или пустой
    }

    // Вызывается при создании ViewModel
    init {
        Log.d(TAG, "ViewModel created. Initializing monitor...")
        initializeDriverMonitor()
    }

    /**
     * Конвертирует изображение в формате YUV_420_888 (из ImageProxy) в ByteArray формата RGBA.
     *
     * @param frameData Данные кадра, содержащие planes, width, height, и format.
     * @return ByteArray с пикселями в формате RGBA. Размер массива: width * height * 4.
     */
    fun convertYuv420ToRgba(frameData: VideoScanViewModel.FrameData): ByteArray {
        if (frameData.format != ImageFormat.YUV_420_888) {
            Log.e("YUV_TO_RGBA", "Invalid image format, expected YUV_420_888, got ${frameData.format}")
            return ByteArray(0) // Возвращаем пустой массив или выбрасываем исключение
        }

        val width = frameData.width
        val height = frameData.height
        val rgbaBytes = ByteArray(width * height * 4)

        val yPlane = frameData.planes[0]
        val uPlane = frameData.planes[1]
        val vPlane = frameData.planes[2]

        val yBuffer: ByteBuffer = yPlane.buffer
        val uBuffer: ByteBuffer = uPlane.buffer
        val vBuffer: ByteBuffer = vPlane.buffer

        val yRowStride: Int = yPlane.rowStride
        val yPixelStride: Int = yPlane.pixelStride // Обычно 1 для Y

        val uRowStride: Int = uPlane.rowStride
        val uPixelStride: Int = uPlane.pixelStride // Обычно 1 или 2 для U/V

        val vRowStride: Int = vPlane.rowStride
        val vPixelStride: Int = vPlane.pixelStride // Обычно 1 или 2 для U/V

        var yIndex: Int
        var uvIndexOffset: Int // Смещение для U и V плоскостей

        // Переменные для хранения значений Y, U, V
        var yValue: Int
        var uValue: Int
        var vValue: Int

        // Переменные для хранения RGB
        var r: Int
        var g: Int
        var b: Int

        var outputIndex = 0 // Индекс для записи в rgbaBytes

        for (j in 0 until height) { // j - строка (y-координата)
            for (i in 0 until width) { // i - столбец (x-координата)
                // Рассчитываем индекс для Y-плоскости
                // yBuffer может содержать padding, поэтому используем rowStride
                yIndex = j * yRowStride + i * yPixelStride
                yValue = yBuffer[yIndex].toInt() and 0xFF

                // Рассчитываем индексы для U и V плоскостей
                // U и V имеют вдвое меньшее разрешение.
                // Для YUV_420_888, U и V могут быть чередованы (NV21) или в отдельных плоскостях (I420).
                // ImageFormat.YUV_420_888 означает, что у нас 3 отдельные плоскости.
                // U и V плоскости имеют pixelStride и rowStride.
                // Один (U,V) пиксель соответствует блоку 2x2 Y пикселей.
                // Делим j и i на 2, чтобы получить соответствующий индекс в U/V плоскостях.
                val uvRow = j / 2
                val uvCol = i / 2

                // Индекс в U-плоскости
                // Важно: uPixelStride может быть 2, если U и V чередуются в одной плоскости (например, UVUV...).
                // Но для YUV_420_888 (3 plane) uPixelStride обычно 1 для U, и vPixelStride 1 для V.
                // Если uPixelStride = 2, то U и V находятся в одной плоскости, чередуясь.
                // Для YUV_420_888 (PLANAR или SEMI_PLANAR с 3 plane buffers), U и V в отдельных буферах.
                // planes[1] -> U, planes[2] -> V
                // Если uPixelStride (planes[1].pixelStride) == 1:
                uvIndexOffset = uvRow * uRowStride + uvCol * uPixelStride
                uValue = uBuffer[uvIndexOffset].toInt() and 0xFF

                // Индекс в V-плоскости
                // Если vPixelStride (planes[2].pixelStride) == 1:
                uvIndexOffset = uvRow * vRowStride + uvCol * vPixelStride
                vValue = vBuffer[uvIndexOffset].toInt() and 0xFF

                // Если uPixelStride == 2 (означает, что U и V чередуются в одной плоскости, planes[1] = UV plane)
                // Это не должно быть для YUV_420_888 с 3 planes, но на всякий случай:
                // if (uPixelStride == 2) {
                //     uvIndexOffset = uvRow * uRowStride + uvCol * uPixelStride
                //     uValue = uBuffer[uvIndexOffset].toInt() and 0xFF // U
                //     vValue = uBuffer[uvIndexOffset + 1].toInt() and 0xFF // V (следующий байт)
                // }

                // Конвертация YUV в RGB (стандартные коэффициенты)
                // Преобразуем V и U в диапазон [-128, 127] для формул
                val uNorm = uValue - 128
                val vNorm = vValue - 128

                r = (yValue + 1.370705 * vNorm).toInt()
                g = (yValue - 0.698001 * vNorm - 0.337633 * uNorm).toInt()
                b = (yValue + 1.732446 * uNorm).toInt()

                // Ограничиваем значения R, G, B диапазоном [0, 255]
                r = r.coerceIn(0, 255)
                g = g.coerceIn(0, 255)
                b = b.coerceIn(0, 255)

                // Записываем RGBA (A = 255, непрозрачный)
                rgbaBytes[outputIndex++] = r.toByte()
                rgbaBytes[outputIndex++] = g.toByte()
                rgbaBytes[outputIndex++] = b.toByte()
                rgbaBytes[outputIndex++] = 0xFF.toByte() // Alpha
            }
        }
        return rgbaBytes
    }

    fun initializeDriverMonitor() {
        if (_uiState.value.isMonitorInitialized) {
            Log.d(TAG, "Monitor already initialized.")
            return
        }
        _uiState.value = _uiState.value.copy(monitorInitializationError = null) // Сбрасываем предыдущую ошибку

        viewModelScope.launch {
            try {
                val appContext = getApplication<Application>().applicationContext
                val assetManager = appContext.assets
                val cacheDir = appContext.cacheDir.absolutePath
                val faceMeshGraphContent = readAssetFile(FACE_MESH_GRAPH_FILE)
                val handTrackingGraphContent = try {
                    readAssetFile(HAND_TRACKING_GRAPH_FILE)
                } catch (e: IOException) {
                    Log.w(TAG, "Hand tracking graph file not found or error reading, proceeding without it.", e)
                    null // Если файл опционален и его отсутствие не критично
                }

                if (faceMeshGraphContent == null) {
                    Log.e(TAG, "Face mesh graph content is null. Initialization failed.")
                    _uiState.value = _uiState.value.copy(
                        isMonitorInitialized = false,
                        monitorInitializationError = "Failed to read face mesh graph."
                    )
                    return@launch
                }

                Log.d(TAG, "Attempting to initialize native library...")
                val success = DriverMonitorBridge.nativeInitialize(
                    assetManager,
                    cacheDir,
                    faceMeshGraphContent,
                    handTrackingGraphContent
                )

                if (success) {
                    Log.i(TAG, "Driver Monitor JNI initialized successfully.")
                    _uiState.value = _uiState.value.copy(isMonitorInitialized = true)
                } else {
                    Log.e(TAG, "Failed to initialize Driver Monitor JNI.")
                    _uiState.value = _uiState.value.copy(
                        isMonitorInitialized = false,
                        monitorInitializationError = "JNI initialization returned false."
                    )
                }
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "UnsatisfiedLinkError during JNI initialization.", e)
                _uiState.value = _uiState.value.copy(
                    isMonitorInitialized = false,
                    monitorInitializationError = "JNI library not found: ${e.message}"
                )
            } catch (e: IOException) {
                Log.e(TAG, "IOException during asset reading.", e)
                _uiState.value = _uiState.value.copy(
                    isMonitorInitialized = false,
                    monitorInitializationError = "Error reading asset files: ${e.message}"
                )
            } catch (e: Exception) {
                Log.e(TAG, "General exception during JNI initialization.", e)
                _uiState.value = _uiState.value.copy(
                    isMonitorInitialized = false,
                    monitorInitializationError = "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    private fun readAssetFile(fileName: String): String? {
        return try {
            getApplication<Application>().applicationContext.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading asset file: $fileName", e)
            throw e // Перебрасываем, чтобы обработать выше
        }
    }

    fun onCameraPermissionResult(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(cameraPermissionGranted = isGranted)
        if (!isGranted) {
            // Обработка случая, когда разрешение не предоставлено
            Log.w(TAG, "Camera permission denied.")
        } else {
            Log.d(TAG, "Camera permission granted.")
            // Здесь можно начать настройку камеры, если монитор уже инициализирован
        }
    }

    fun startMonitoring() {
        if (_uiState.value.isMonitorInitialized && _uiState.value.cameraPermissionGranted) {
            _uiState.value = _uiState.value.copy(isMonitoringActive = true)
            Log.i(TAG, "Monitoring started.")
            // Здесь будет логика старта получения кадров с камеры
        } else {
            Log.w(TAG, "Cannot start monitoring: Monitor not initialized or camera permission denied.")
            // Можно показать сообщение пользователю
        }
    }

    fun stopMonitoring() {
        _uiState.value = _uiState.value.copy(isMonitoringActive = false)
        Log.i(TAG, "Monitoring stopped.")
        // Здесь будет логика остановки получения кадров
    }

    // Вызывается, когда ViewModel больше не используется и будет уничтожена
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared. Releasing monitor...")
        if (_uiState.value.isMonitorInitialized) { // Освобождаем ресурсы только если были инициализированы
            try {
                DriverMonitorBridge.nativeRelease()
                Log.i(TAG, "Driver Monitor JNI released successfully.")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "UnsatisfiedLinkError during JNI release.", e)
            } catch (e: Exception) {
                Log.e(TAG, "Exception during JNI release.", e)
            }
        }
    }

    fun handleCameraError(errorMessage: String?) {
        _uiState.value = _uiState.value.copy(
            isMonitoringActive = false, // Останавливаем мониторинг при ошибке камеры
            // Можно добавить специальное поле для ошибки камеры, если нужно
            // cameraErrorMessage = errorMessage
        )
        Log.e(TAG, "Camera Error: $errorMessage")
    }

    // Эта функция будет вызываться из ImageAnalyzer для обработки кадра
    data class FrameData(
        val planes: Array<ImageProxy.PlaneProxy>, // Или сразу ByteArray, если конвертация будет здесь
        val format: Int,
        val width: Int,
        val height: Int,
        val rotationDegrees: Int,
        val timestampNanos: Long // Наносекунды
    )

    @Serializable
    data class Violation(
        val type: String,
        val description: String,
        val timestamp: String,
        val capture_image: Boolean
    )

    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun parseAndHandleDetections(
        jsonString: String,
        rgbaFrameForCapture: ByteArray?, // RGBA данные кадра, если нужно фото
        frameWidth: Int,
        frameHeight: Int
    ) {
        if (jsonString.isBlank() || jsonString == "[]") {
            // Нет нарушений или пустой результат
            // Можно сбросить отображение старых нарушений, если нужно
            // _uiState.value = _uiState.value.copy(lastDetections = emptyList())
            return
        }

        try {
            val violations = jsonParser.decodeFromString<List<Violation>>(jsonString)

            if (violations.isNotEmpty()) {
                Log.i(TAG, "Detected violations: $violations")
                // Обновляем UI (пока просто списком описаний)
                val currentDetectionDescriptions = violations.map { "${it.type}: ${it.description}" }

                // Обновляем статистику нарушений
                val updatedStats = _uiState.value.violationStats.toMutableMap()
                violations.forEach { violation ->
                    updatedStats[violation.type] = (updatedStats[violation.type] ?: 0) + 1
                    if (violation.capture_image && rgbaFrameForCapture != null) {
                        Log.d(TAG, "Capture image requested for: ${violation.type}")
                        // TODO: Сохранение изображения (позже)
                    }
                    // TODO: Сохранение информации о нарушении в базу данных (позже)
                }
                Log.d(TAG, "Updated stats: $updatedStats")

                _uiState.update { currentState ->
                    currentState.copy(
                        lastDetections = currentDetectionDescriptions,
                        violationStats = updatedStats
                    )
                }

            } else {
                _uiState.update { it.copy(lastDetections = emptyList()) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON violations: $jsonString", e)
            _uiState.update { it.copy(lastDetections = listOf("Error parsing JNI result")) }
        }
    }

    fun resetStatistics() {
        _uiState.update {
            it.copy(
                violationStats = emptyMap(),
                lastDetections = emptyList() // Также сбрасываем текущие отображаемые
            )
        }
        Log.i(TAG, "Statistics reset.")
    }

    fun processFrameData(frameData: FrameData) {
        if (!_uiState.value.isMonitoringActive) {
            return // Мониторинг не активен
        }

//        Log.v(TAG, "Processing frame data: Format=${frameData.format}, ${frameData.width}x${frameData.height}, Rotation=${frameData.rotationDegrees}, ts: ${frameData.timestampNanos}")

        val rgbaByteArray = convertYuv420ToRgba(frameData) // Вызываем нашу функцию

        if (rgbaByteArray.isEmpty()) {
            Log.e(TAG, "RGBA conversion failed or returned empty array.")
            return
        }

        // Логируем размер полученного массива для проверки
//        Log.d(TAG, "Converted to RGBA. Array size: ${rgbaByteArray.size}. Expected: ${frameData.width * frameData.height * 4}")
        // TODO: Шаг 5 - Передача RGBA в JNI
        //    DriverMonitorBridge.nativeProcessFrameRgba(rgbaByteArray, frameData.width, frameData.height, frameData.timestampNanos / 1000)
        try {
            val jsonResult = DriverMonitorBridge.nativeProcessFrameRgba(
                rgbaByteArray,
                frameData.width,
                frameData.height,
                frameData.timestampNanos / 1000 // Конвертируем наносекунды в микросекунды, как ожидает JNI
            )

            // Логируем полученный JSON
//            Log.d(TAG, "JNI Result: $jsonResult")

            // TODO: Шаг 6 - Парсинг JSON и обновление UI/сохранение данных
            parseAndHandleDetections(jsonResult, rgbaByteArray, frameData.width, frameData.height)

        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "JNI call failed (UnsatisfiedLinkError): ${e.message}", e)
            // Возможно, стоит остановить мониторинг или показать ошибку пользователю
            // _uiState.value = _uiState.value.copy(isMonitoringActive = false, monitorInitializationError = "JNI call failed")
        } catch (e: Exception) {
            Log.e(TAG, "JNI call failed (Exception): ${e.message}", e)
            // Аналогично, обработка ошибки
        }
    }


}