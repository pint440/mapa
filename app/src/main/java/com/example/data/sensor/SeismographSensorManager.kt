package com.example.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

class SeismographSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _vibrationData = MutableStateFlow(0f)
    val vibrationData: StateFlow<Float> = _vibrationData

    private val _isSensorAvailable = MutableStateFlow(accelerometer != null)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable

    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calculate current acceleration magnitude
            val magnitude = sqrt(x * x + y * y + z * z)
            // Subtract Earth's typical gravity
            val gravity = SensorManager.GRAVITY_EARTH
            val rawDelta = magnitude - gravity
            
            // Clean up noise
            val processedDelta = if (rawDelta < 0.08f && rawDelta > -0.08f) 0f else rawDelta
            _vibrationData.value = processedDelta
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
