package sensormodels.activity;

public interface ActivitySensorBuilder {

  ActivitySensorBuilder setSensorName(String sensorName);

  ActivitySensorBuilder setTimeStamp(String timeStamp);

  ActivitySensorBuilder setTimestamp(String timeStamp);

  ActivitySensorBuilder setSensorData(ActivitySensorData.SensorData sensorData);

  ActivitySensorBuilder setStepCounts(Integer stepCounts);

  ActivitySensorBuilder setStepDelta(Integer stepDelta);

  ActivitySensorData build();
}
