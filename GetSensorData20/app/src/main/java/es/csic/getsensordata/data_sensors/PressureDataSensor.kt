package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import es.csic.getsensordata.R
import java.util.*

class PressureDataSensor(private val context: Context, private val updateInterval: Double): DataSensor(context, Sensor.TYPE_PRESSURE, updateInterval) {
    override fun getPrefix(): String = "PRES"

    override fun getName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.pressure_sensor_not_detected)
        }

    override fun getFeatures(): String =
        if (sensor != null) {
            """
                | ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                | ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                | ${context.getString(R.string.resolution)}: ${sensor.resolution} mbar,
                | ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} mbar,
                | ${context.getString(R.string.power_consumption)}: ${sensor.power} mA,
                | ${context.getString(R.string.minimum_delay)}: ${sensor.minDelay}
            """.trimMargin()
        } else {
            context.getString(R.string.no_features)
        }

    override fun getStatusForScreen(event: SensorEvent): String {
        counter += 1

        val eventTimestamp = getTimestamp(event)
        val timestamp = getTimestamp()

        if (eventTimestamp - previousSensorTimestamp > 0) {
            measurementFrequency = (0.9 * measurementFrequency + 0.1 / (eventTimestamp - previousSensorTimestamp)).toFloat()
        } else {
            Log.e("${getPrefix()} SENSOR", "timestamp < previousTimestamp")
        }
        previousSensorTimestamp = eventTimestamp

        return if (timestamp - previousUpdateTimestamp > updateInterval) {
            previousUpdateTimestamp = timestamp
            val templateForScreen = """
                |   Pressure: %8.2f  mbar
                |                               Freq: %5.0f Hz
            """.trimMargin()
            String.format(Locale.US, templateForScreen,
                    event.values[0],
                    measurementFrequency
            )
        } else {
            ""
        }
    }

    override fun getStatusForLog(event: SensorEvent): String {
        val eventTimestamp = getTimestamp(event)
        val timestamp = getTimestamp()

        val templateForLog = "\n${getPrefix()};%.3f;%.3f;%.4f;%d"
        return String.format(Locale.US, templateForLog,
                timestamp,
                eventTimestamp,
                event.values[0],
                event.accuracy
        )
    }
}
