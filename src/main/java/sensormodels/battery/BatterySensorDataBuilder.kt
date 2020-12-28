package sensormodels.battery

class BatterySensorDataBuilder : BatterySensorBuilder {
    private val batterySensorData: BatterySensorData = BatterySensorData()
    override fun setSensorName(sensorName: String?): BatterySensorBuilder {
        batterySensorData.sensorName = sensorName
        return this
    }

    override fun setTimeStamp(timeStamp: String?): BatterySensorBuilder {
        batterySensorData.timestamp = timeStamp
        batterySensorData.setFormattedDate()
        return this
    }

    override fun setSensorData(sensorData: BatterySensorData.SensorData?): BatterySensorBuilder {
        batterySensorData.sensorData = sensorData
        return this
    }

    override fun setPercent(percent: Int?): BatterySensorBuilder {
        batterySensorData.sensorData?.percent = percent
        return this
    }

    override fun setCharging(charging: Boolean?): BatterySensorBuilder {
        batterySensorData.sensorData?.charging = charging
        return this
    }

    override fun build(): BatterySensorData {
        return batterySensorData
    }

    init {
        batterySensorData.sensorData = BatterySensorData.SensorData()
    }
}