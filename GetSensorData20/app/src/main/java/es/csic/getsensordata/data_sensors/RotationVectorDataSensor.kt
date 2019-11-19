package es.csic.getsensordata.data_sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import es.csic.getsensordata.R
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.PI

class RotationVectorDataSensor(private val context: Context, private val updateInterval: Double): DataSensor(context, Sensor.TYPE_ROTATION_VECTOR, updateInterval) {
    override fun getSensorPrefix(): String = "AHRS"

    override fun getSensorName(): String =
        if (sensor != null) {
            sensor.name
        } else {
            context.getString(R.string.rotation_vector_sensor_not_detected)
        }

    override fun getSensorFeatures(): String =
        if (sensor != null) {
            """
                | ${context.getString(R.string.manufacturer)}: ${sensor.vendor},
                | ${context.getString(R.string.version)}: ${sensor.version}, Type: ${sensor.type},
                | ${context.getString(R.string.resolution)}: ${sensor.resolution} a.u.,
                | ${context.getString(R.string.maximum_range)}: ${sensor.maximumRange} a.u.,
                | ${context.getString(R.string.power_consumption)}: ${sensor.power} mA,
                | ${context.getString(R.string.minimum_delay)}: ${sensor.minDelay}
            """.trimMargin()
        } else {
            context.getString(R.string.no_features)
        }

    override fun getSensorStatus(event: SensorEvent): Pair<String, String>? {
        counter += 1

        val sensorTimestamp = getSensorTimestamp(event)
        val timestamp = getTimestamp()

        measurementFrequency = (0.9 * measurementFrequency + 0.1 / (sensorTimestamp - previousSensorTimestamp)).toFloat()
        previousSensorTimestamp = sensorTimestamp

        val eventValues = event.values
        val rotationMatrix = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        try {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, eventValues)
        } catch(e: IllegalArgumentException) {
            // Galaxy Note 3 bug
            val eventValuesFixed = floatArrayOf(eventValues[0], eventValues[1], eventValues[2])
            SensorManager.getRotationMatrixFromVector(rotationMatrix, eventValuesFixed)
        }

        var orientation = floatArrayOf(0f, 0f, 0f)
        orientation = SensorManager.getOrientation(rotationMatrix, orientation)
        orientation[0] = -orientation[0]
        orientation[1] = -orientation[1]
        val yaw = orientation[0] * 180 / PI
        val pitch = orientation[1] * 180 / PI
        val roll = orientation[2] * 180 / PI

        if (timestamp - previousUpdateTimestamp > updateInterval) {
            val templateForScreen = """
                |   Pitch(X):   %10.3f  degrees
                |   Roll(Y):    %10.3f  degrees
                |   Yaw(Z):     %10.3f  degrees
                |                               Freq: %5.0f Hz
            """.trimMargin()
            val statusForScreen = String.format(Locale.US, templateForScreen,
                    pitch,
                    roll,
                    yaw,
                    measurementFrequency
            )
            val templateForLog = "\n${getSensorPrefix()};%.3f;%.3f;%.6f;%.6f;%.6f;%.8f;%.8f;%.8f;%d"
            val statusForLog = String.format(Locale.US, templateForLog,
                    timestamp,
                    sensorTimestamp,
                    pitch,
                    roll,
                    yaw,
                    eventValues[0],
                    eventValues[1],
                    eventValues[2],
                    event.accuracy
            )
            previousUpdateTimestamp = timestamp
            return Pair(statusForScreen, statusForLog)
        } else {
            return null
        }
    }
}
