package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import es.csic.getsensordata.R

class GyroscopeDataSensor(private val context: Context): DataSensor(context, Sensor.TYPE_GYROSCOPE) {
    override fun getSensorPrefix(): String =
        context.getString(R.string.gyroscope_sensor_prefix)

    override fun getSensorName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.gyroscope_sensor_not_detected)
        }

    override fun getSensorFeatures(): String =
        if (sensor != null) {
            """
                ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                ${context.getString(R.string.resolution)}: ${sensor.resolution} rad/s,
                ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} rad/s,
                ${context.getString(R.string.power_consumption)}: ${sensor.power} mA,
                ${context.getString(R.string.minimum_delay)}: ${sensor.minDelay}
            """.trimIndent()
        } else {
            context.getString(R.string.no_features)
        }
}
