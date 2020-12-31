package sensormodels.activity


class ActivitySensorDataBuilder : ActivitySensorBuilder {
    private val activitySensorData: ActivitySensorData = ActivitySensorData()
    override fun setSensorName(sensorName: String): ActivitySensorBuilder {
        activitySensorData.sensorName = sensorName
        return this
    }

    override fun setTimeStamp(timeStamp: String): ActivitySensorBuilder {
        activitySensorData.timeStamp = timeStamp
        activitySensorData.setFormattedDate()
        return this
    }

    override fun setTimestamp(timeStamp: String): ActivitySensorBuilder {
        activitySensorData.time_stamp = timeStamp
        activitySensorData.setFormattedDate()
        return this
    }

    override fun setSensorData(sensorData: ActivitySensorData.SensorData): ActivitySensorBuilder {
        activitySensorData.sensorData = sensorData
        return this
    }

    override fun setStepCounts(stepCounts: Int): ActivitySensorBuilder {
        val sensorData = activitySensorData.sensorData
        sensorData.stepCounts = stepCounts
        activitySensorData.sensorData = sensorData
        return this
    }

    override fun setStepDelta(stepDelta: Int): ActivitySensorBuilder {
        val sensorData = activitySensorData.sensorData
        sensorData.stepDelta = stepDelta
        activitySensorData.sensorData = sensorData
        return this
    }

    override fun build(): ActivitySensorData {
        return activitySensorData
    }

    init {
        activitySensorData.sensorData = ActivitySensorData.SensorData()
    }
}