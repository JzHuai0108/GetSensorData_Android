package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

abstract class DataSensor(private val context: Context, private val sensorType: Int, private val updateInterval: Double) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    val sensor: Sensor?
    var counter: Long = 0
    var epoch: Long = 0
        set(value) {
            field = value
            previousSensorTimestamp = 0.0
            previousUpdateTimestamp = 0.0
        }
    var previousSensorTimestamp: Double = 0.0
    var previousUpdateTimestamp: Double = 0.0
    var measurementFrequency: Float = 0.0f

    init {
        sensor = sensorManager?.getDefaultSensor(sensorType)
    }

    fun getDescription(): String =
        getSensorDescription()

    private fun getSensorDescription(): String =
        """
            | ${getPrefix()}: ${getName()}
        """.trimMargin()

    open fun getFeatures(): String =
        ""

    abstract fun getPrefix(): String

    abstract fun getName(): String

    fun registerListener(listener: SensorEventListener, samplingPeriodUs: Int) {
        sensorManager?.registerListener(listener, sensor, samplingPeriodUs)
    }

    abstract fun getStatus(event: SensorEvent): Pair<String, String>?

    fun getTimestamp(event: SensorEvent): Double =
        event.timestamp.toDouble() * 1e-9

    fun getTimestamp(): Double {
        val systemNanoTime = System.nanoTime()
        val timestampInNanoseconds: Long
        timestampInNanoseconds = if (systemNanoTime >= epoch) {
            systemNanoTime - epoch
        } else {
            systemNanoTime - epoch + Long.MAX_VALUE
        }
        return timestampInNanoseconds.toDouble() * 1e-9
    }
}
