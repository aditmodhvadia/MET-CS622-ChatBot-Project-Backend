package sensormodels.activfit

interface ActivFitSensorBuilder {
    fun setSensorName(sensorName: String): ActivFitSensorBuilder
    fun setTimeStamp(timeStamp: ActivFitSensorData.Timestamp): ActivFitSensorBuilder
    fun setSensorData(sensorData: ActivFitSensorData.SensorData): ActivFitSensorBuilder
    fun setStartTime(startTime: String): ActivFitSensorBuilder
    fun setEndTime(endTime: String): ActivFitSensorBuilder
    fun setActivity(activity: String): ActivFitSensorBuilder
    fun setDuration(duration: Int): ActivFitSensorBuilder
    fun build(): ActivFitSensorData
}