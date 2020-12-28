package sensormodels.battery;

public interface BatterySensorBuilder {

  BatterySensorBuilder setSensorName(String sensorName);

  BatterySensorBuilder setTimeStamp(String timeStamp);

  BatterySensorBuilder setSensorData(BatterySensorData.SensorData sensorData);

  BatterySensorBuilder setPercent(Integer percent);

  BatterySensorBuilder setCharging(Boolean charging);

  BatterySensorData build();
}
