package com.example.pp_androidstudio_avto.ui.screens

import android.app.Application // Используем Application для доступа к Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pp_androidstudio_avto.drivermonitor.DriverMonitorBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Состояния для UI
data class VideoScanUiState(
    val isMonitorInitialized: Boolean = false,
    val monitorInitializationError: String? = null,
    val isMonitoringActive: Boolean = false, // Пользователь нажал "Старт"
    val lastDetections: List<String> = emptyList(), // Пока просто строки для примера
    val cameraPermissionGranted: Boolean = false
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
}