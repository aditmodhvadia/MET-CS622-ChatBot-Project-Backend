package sensormodels.activfit

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.stream.Collectors

class ActivFitSensorDataTest {
    private val sensorName = "ActivFitSensorData"
    private val activity = "Activity"
    private val startTime = "12/12/2020"
    private val endTime = "endTime"
    private val duration = 100
    private lateinit var activFitSensorData: ActivFitSensorData

    @Before
    fun setUp() {
        activFitSensorData = ActivFitSensorDataBuilder()
            .setSensorName(sensorName)
            .setActivity(activity)
            .setDuration(duration)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .build()
    }

    @Test
    fun getSensorName() {
        Assert.assertEquals(sensorName, activFitSensorData.sensorName)
    }

    @Test
    fun getActivity() {
        Assert.assertEquals(activity, activFitSensorData.sensorData!!.activity)
    }

    @Test
    fun getStartTime() {
        Assert.assertEquals(startTime, activFitSensorData.timestamp?.startTime)
    }

    @Test
    fun getEndTime() {
        Assert.assertEquals(endTime, activFitSensorData.timestamp!!.endTime)
    }

    @Test
    fun getDuration() {
        Assert.assertEquals(duration, activFitSensorData.sensorData!!.duration)
    }

    @Test
    fun formattedDate() {
        Assert.assertEquals(startTime, activFitSensorData.formattedDate)
    }

    @Test
    fun filterSensorData() {
        val sensorDataList: MutableList<ActivFitSensorData?> = ArrayList()
        sensorDataList.add(activFitSensorData)
        sensorDataList.add(activFitSensorData)
        sensorDataList.add(activFitSensorData)
        Assert.assertEquals(
            sensorDataList,
            sensorDataList.stream()
                .filter { sensorData: ActivFitSensorData? -> sensorData!!.sensorName == sensorName }
                .collect(Collectors.toList()))
        Assert.assertEquals(
            ArrayList<Any>(),
            sensorDataList.stream()
                .filter { sensorData: ActivFitSensorData? -> sensorData!!.sensorName != sensorName }
                .collect(Collectors.toList()))
    }
}