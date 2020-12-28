package sensormodels.activity

interface ActivitySensorBuilder {
    fun setSensorName(sensorName: String?): ActivitySensorBuilder
    fun setTimeStamp(timeStamp: String?): ActivitySensorBuilder
    fun setTimestamp(timeStamp: String?): ActivitySensorBuilder
    fun setSensorData(sensorData: ActivitySensorData.SensorData?): ActivitySensorBuilder
    fun setStepCounts(stepCounts: Int?): ActivitySensorBuilder
    fun setStepDelta(stepDelta: Int?): ActivitySensorBuilder
    fun build(): ActivitySensorData
}