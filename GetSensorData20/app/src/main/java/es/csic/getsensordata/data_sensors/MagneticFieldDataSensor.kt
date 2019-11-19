package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import es.csic.getsensordata.R
import java.util.*

class MagneticFieldDataSensor(private val context: Context, private val updateInterval: Double): DataSensor(context, Sensor.TYPE_MAGNETIC_FIELD, updateInterval) {
    override fun getSensorPrefix(): String =
        context.getString(R.string.magnetic_field_sensor_prefix)

    override fun getSensorName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.magnetic_field_sensor_not_detected)
        }

    override fun getSensorFeatures(): String =
        if (sensor != null) {
            """
                | ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                | ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                | ${context.getString(R.string.resolution)}: ${sensor.resolution} uT,
                | ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} uT,
                | ${context.getString(R.string.power_consumption)}: ${sensor.power} mA,
                | ${context.getString(R.string.minimum_delay)}: ${sensor.minDelay}
            """.trimMargin()
        } else {
            context.getString(R.string.no_features)
        }

    override fun getSensorStatus(event: SensorEvent, epoch: Long): Pair<String, String>? {
        counter += 1

        val sensorTimestamp = getSensorTimestamp(event)
        val timestamp = getTimestamp(epoch)

        if (sensorTimestamp - previousSensorTimestamp > 0) {
            measurementFrequency = (0.9 * measurementFrequency + 0.1 / (sensorTimestamp - previousSensorTimestamp)).toFloat()
        } else {
            Log.e("${getSensorPrefix()} SENSOR", "timestamp < previousTimestamp")
        }
        previousSensorTimestamp = sensorTimestamp

        if (timestamp - previousUpdateTimestamp > updateInterval) {
            val templateForScreen = """
                |   Mag(X): %10.5f  uT
                |   Mag(Y): %10.5f  uT
                |   Mag(Z): %10.5f  uT
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
                    sensorTimestamp,
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