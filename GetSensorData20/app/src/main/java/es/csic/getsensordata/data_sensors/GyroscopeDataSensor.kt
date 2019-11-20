package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import es.csic.getsensordata.R
import java.util.*

class GyroscopeDataSensor(private val context: Context, private val updateInterval: Double): DataSensor(context, Sensor.TYPE_GYROSCOPE, updateInterval) {
    override fun getPrefix(): String = "GYRO"

    override fun getSensorName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.gyroscope_sensor_not_detected)
        }

    override fun getSensorFeatures(): String =
        if (sensor != null) {
            """
                | ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                | ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                | ${context.getString(R.string.resolution)}: ${sensor.resolution} rad/s,
                | ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} rad/s,
                | ${context.getString(R.string.power_consumption)}: ${sensor.power} mA,
                | ${context.getString(R.string.minimum_delay)}: ${sensor.minDelay}
            """.trimMargin()
        } else {
            context.getString(R.string.no_features)
        }

    override fun getStatus(event: SensorEvent): Pair<String, String>? {
        counter += 1

        val eventTimestamp = getTimestamp(event)
        val timestamp = getTimestamp()

        if (eventTimestamp - previousSensorTimestamp > 0) {
            measurementFrequency = (0.99 * measurementFrequency + 0.01 / (eventTimestamp - previousSensorTimestamp)).toFloat()
        } else {
            Log.e("${getPrefix()} SENSOR", "timestamp < previousTimestamp")
        }
        previousSensorTimestamp = eventTimestamp

        if (timestamp - previousUpdateTimestamp > updateInterval) {
            val templateForScreen = """
                |   Gyr(X): %10.5f  rad/s
                |   Gyr(Y): %10.5f  rad/s
                |   Gyr(Z): %10.5f  rad/s
                |                               Freq: %5.0f Hz
            """.trimMargin()
            val statusForScreen = String.format(Locale.US, templateForScreen,
                    event.values[0],
                    event.values[1],
                    event.values[2],
                    measurementFrequency
            )
            val templateForLog = "\n${getPrefix()};%.3f;%.3f;%.5f;%.5f;%.5f;%d"
            val statusForLog = String.format(Locale.US, templateForLog,
                    timestamp,
                    eventTimestamp,
                    event.values[0],
                    event.values[1],
                    event.values[2],
                    event.accuracy
            )
            previousUpdateTimestamp = timestamp
            return Pair(statusForScreen, statusForLog)
        } else {
            return null
        }
    }
}
