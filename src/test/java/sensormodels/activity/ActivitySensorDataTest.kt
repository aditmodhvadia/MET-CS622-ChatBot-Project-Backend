package sensormodels.activity

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ActivitySensorDataTest {
    private lateinit var activitySensorData: ActivitySensorData
    private val sensorName = "ActivitySensorData"
    private val startTime = "12/12/2020"
    private val stepCount = 50
    private val stepCountDelta = 5000

    @Before
    fun setUp() {
        activitySensorData = buildActivitySensor {
            setSensorName(sensorName)
            setStepCounts(stepCount)
            setStepDelta(stepCountDelta)
            setTimeStamp(startTime)
            setTimestamp(startTime)
        }
    }

    @Test
    fun getSensorName() {
        Assert.assertEquals(sensorName, activitySensorData.sensorName)
    }

    @Test
    fun timeStamp() {
        Assert.assertEquals(startTime, activitySensorData.timeStamp)
    }

    @Test
    fun timestamp() {
        Assert.assertEquals(startTime, activitySensorData.timeStamp)
    }

    @Test
    fun formattedDate() {
        Assert.assertEquals(startTime, activitySensorData.formattedDate)
    }

    @Test
    fun getStartTime() {
        print(activitySensorData)
        Assert.assertEquals(startTime, activitySensorData.startTime)
    }

    @Test
    fun getStepCount() {
        Assert.assertEquals(stepCount, activitySensorData.sensorData.stepCounts)
    }

    @Test
    fun stepDelta() {
        Assert.assertEquals(stepCountDelta, activitySensorData.sensorData.stepDelta)
    }
}