package sensormodels.activfit;

public interface ActivFitSensorBuilder {

  ActivFitSensorBuilder setSensorName(String sensorName);

  ActivFitSensorBuilder setTimeStamp(ActivFitSensorData.Timestamp timeStamp);

  ActivFitSensorBuilder setSensorData(ActivFitSensorData.SensorData sensorData);

  ActivFitSensorBuilder setStartTime(String startTime);

  ActivFitSensorBuilder setEndTime(String endTime);

  ActivFitSensorBuilder setActivity(String activity);

  ActivFitSensorBuilder setDuration(Integer duration);

  ActivFitSensorData build();
}
