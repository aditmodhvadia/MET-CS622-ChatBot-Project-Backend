package sensormodels.battery

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BatterySensorDataTest {
    private val sensorName = "ActivFitSensorData"
    private val startTime = "12/12/2020"
    private val percent = 50
    private val charging = true
    private lateinit var batterySensorData: BatterySensorData

    @Before
    fun setUp() {
        batterySensorData = BatterySensorDataBuilder()
            .setSensorName(sensorName)
            .setCharging(charging)
            .setPercent(percent)
            .setTimeStamp(startTime)
            .build()
    }

    @Test
    fun getSensorName() {
        Assert.assertEquals(sensorName, batterySensorData.sensorName)
    }

    @Test
    fun getStartTime() {
        Assert.assertEquals(startTime, batterySensorData.timestamp)
    }

    @Test
    fun getCharging() {
        Assert.assertEquals(charging, batterySensorData.sensorData!!.charging)
    }

    @Test
    fun getPercent() {
        Assert.assertEquals(percent, batterySensorData.sensorData!!.percent)
    }

    @Test
    fun formattedDate() {
        Assert.assertEquals(startTime, batterySensorData.formattedDate)
    }
}