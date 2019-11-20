package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import es.csic.getsensordata.R
import java.io.IOException
import java.util.*

class AccelerometerDataSensor(private val context: Context, private val updateInterval: Double): DataSensor(context, Sensor.TYPE_ACCELEROMETER, updateInterval) {
    override fun getSensorPrefix(): String = "ACCE"

    override fun getSensorName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.accelerometer_sensor_not_detected)
        }

    override fun getSensorFeatures(): String =
        if (sensor != null) {
            """
                | ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                | ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                | ${context.getString(R.string.resolution)}: ${sensor.resolution} m/s^2,
                | ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} m/s^2,
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
            measurementFrequency = (0.9 * measurementFrequency + 0.1 / (eventTimestamp - previousSensorTimestamp)).toFloat()
        } else {
            Log.e("${getSensorPrefix()} SENSOR", "timestamp < previousTimestamp")
        }
        previousSensorTimestamp = eventTimestamp

        if (timestamp - previousUpdateTimestamp > updateInterval) {
            val templateForScreen = """
                |   Acc(X): %10.5f  m/s^2
                |   Acc(Y): %10.5f  m/s^2
                |   Acc(Z): %10.5f  m/s^2
                |                               Freq: %5.0f Hz
            """.trimMargin()
            val statusForScreen = String.format(Locale.US, templateForScreen,
                event.values[0],
                event.values[1],
                event.values[2],
                measurementFrequency
            )
            val templateForLog = "\n${getSensorPrefix()};%.3f;%.3f;%.5f;%.5f;%.5f;%d"
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
