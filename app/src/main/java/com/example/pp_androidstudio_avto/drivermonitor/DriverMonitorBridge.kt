package com.example.pp_androidstudio_avto.drivermonitor

object DriverMonitorBridge {
    init {
        try {
            System.loadLibrary("driver_monitor_jni_lib") // Убедись, что это имя вашей библиотеки
        } catch (e: UnsatisfiedLinkError) {
            // Обработка ошибки, если библиотека не найдена.
            // Можно залогировать или выбросить исключение, чтобы приложение упало с понятной ошибкой.
            // Например, Log.e("DriverMonitorBridge", "Failed to load JNI library", e)
            // Для разработки это важно, чтобы сразу видеть проблему.
            throw RuntimeException("Failed to load JNI library 'driver_monitor_jni_lib'", e)
        }
    }

    /**
     * Инициализирует C++ библиотеку и графы MediaPipe.
     * Должна вызываться один раз при старте функционала мониторинга.
     * @param faceMeshGraphContent Содержимое .pbtxt файла для графа Face Mesh.
     * @param handTrackingGraphContent Содержимое .pbtxt файла для графа Hand Tracking.
     *                                 Может быть null или пустой строкой, если граф рук не используется.
     * @return true в случае успеха, false в случае ошибки.
     */
    external fun nativeInitialize(
        faceMeshGraphContent: String,
        handTrackingGraphContent: String?
    ): Boolean

    /**
     * Обрабатывает один кадр с камеры.
     * @param frameDataRgba Массив байт изображения в формате RGBA (8 бит на канал).
     * @param width Ширина изображения в пикселях.
     * @param height Высота изображения в пикселях.
     * @param timestampUs Временная метка кадра в микросекундах.
     * @return Строка JSON, содержащая массив объектов нарушений.
     *         Пример: "[{\"type\":\"DROWSINESS\",\"description\":\"Глаза закрыты\",\"timestamp\":\"20231217_103000\",\"capture_image\":true}]"
     *         Или пустой массив "[]", если нарушений нет.
     */
    external fun nativeProcessFrameRgba(
        frameDataRgba: ByteArray,
        width: Int,
        height: Int,
        timestampUs: Long
    ): String

    /**
     * Освобождает ресурсы, используемые C++ библиотекой.
     * Должна вызываться при завершении работы функционала мониторинга.
     */
    external fun nativeRelease()
}