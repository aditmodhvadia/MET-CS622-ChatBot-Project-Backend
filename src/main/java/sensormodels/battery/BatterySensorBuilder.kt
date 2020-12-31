package sensormodels.battery

interface BatterySensorBuilder {
    fun setSensorName(sensorName: String): BatterySensorBuilder
    fun setTimeStamp(timeStamp: String): BatterySensorBuilder
    fun setSensorData(sensorData: BatterySensorData.SensorData): BatterySensorBuilder
    fun setPercent(percent: Int): BatterySensorBuilder
    fun setCharging(charging: Boolean): BatterySensorBuilder
    fun build(): BatterySensorData
}