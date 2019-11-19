package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import es.csic.getsensordata.R

abstract class DataSensor(private val context: Context, private val sensorType: Int, private val updateInterval: Double) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    val sensor: Sensor?
    var counter: Long = 0
    var previousSensorTimestamp: Double = 0.0
    var previousUpdateTimestamp: Double = 0.0
    var measurementFrequency: Float = 0.0f

    init {
        sensor = sensorManager?.getDefaultSensor(sensorType)
    }

    fun getDescription(): String =
        getSensorDescription()

    fun getFeatures(): String =
        getSensorFeatures()

    private fun getSensorDescription(): String =
        """
            | ${getSensorPrefix()}: ${getSensorName()}
        """.trimMargin()

    abstract fun getSensorPrefix(): String

    abstract fun getSensorName(): String

    abstract fun getSensorFeatures(): String

    fun registerListener(listener: SensorEventListener, samplingPeriodUs: Int) {
        sensorManager?.registerListener(listener, sensor, samplingPeriodUs)
    }

    abstract fun getSensorStatus(event: SensorEvent, epoch: Long): Pair<String, String>?

    fun getSensorTimestamp(event: SensorEvent): Double =
        event.timestamp.toDouble() * 1e-9

    fun getTimestamp(epoch: Long): Double {
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
