package sensormodels.activfit

class ActivFitSensorDataBuilder : ActivFitSensorBuilder {
    private val activFitSensorData: ActivFitSensorData = ActivFitSensorData()

    override fun setSensorName(sensorName: String): ActivFitSensorBuilder {
        activFitSensorData.sensorName = sensorName
        return this
    }

    override fun setTimeStamp(timeStamp: ActivFitSensorData.Timestamp): ActivFitSensorBuilder {
        activFitSensorData.timestamp = timeStamp
        return this
    }

    override fun setSensorData(sensorData: ActivFitSensorData.SensorData): ActivFitSensorBuilder {
        activFitSensorData.sensorData = sensorData
        return this
    }

    override fun setStartTime(startTime: String): ActivFitSensorBuilder {
        activFitSensorData.timestamp!!.startTime = startTime
        activFitSensorData.setFormattedDate()
        return this
    }

    override fun setEndTime(endTime: String): ActivFitSensorBuilder {
        activFitSensorData.timestamp!!.endTime = endTime
        return this
    }

    override fun setActivity(activity: String): ActivFitSensorBuilder {
        activFitSensorData.sensorData!!.activity = activity
        return this
    }

    override fun setDuration(duration: Int): ActivFitSensorBuilder {
        activFitSensorData.sensorData!!.duration = duration
        return this
    }

    override fun build(): ActivFitSensorData {
        return activFitSensorData
    }

    init {
        activFitSensorData.timestamp = ActivFitSensorData.Timestamp()
        activFitSensorData.sensorData = ActivFitSensorData.SensorData()
    }
}