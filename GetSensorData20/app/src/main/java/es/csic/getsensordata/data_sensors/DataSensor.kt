package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import es.csic.getsensordata.R

abstract class DataSensor(private val context: Context, private val sensorType: Int) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    val sensor: Sensor?

    init {
        sensor = sensorManager?.getDefaultSensor(sensorType)
    }

    fun getDescription(): String =
        getSensorDescription()

    fun getFeatures(): String =
        getSensorFeatures()

    private fun getSensorDescription(): String =
        "${getSensorPrefix()}: ${getSensorName()}"

    abstract fun getSensorPrefix(): String

    abstract fun getSensorName(): String

    abstract fun getSensorFeatures(): String

    fun registerListener(listener: SensorEventListener, samplingPeriodUs: Int) {
        sensorManager?.registerListener(listener, sensor, samplingPeriodUs)
    }
}
