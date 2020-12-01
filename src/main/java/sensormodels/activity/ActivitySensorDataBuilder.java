package sensormodels.activity;

public class ActivitySensorDataBuilder implements ActivitySensorBuilder {
  private final ActivitySensorData activitySensorData;

  public ActivitySensorDataBuilder() {
    this.activitySensorData = new ActivitySensorData();
    this.activitySensorData.setSensorData(new ActivitySensorData.SensorData());
  }

  @Override
  public ActivitySensorBuilder setSensorName(String sensorName) {
    this.activitySensorData.setSensorName(sensorName);
    return this;
  }

  @Override
  public ActivitySensorBuilder setTimeStamp(String timeStamp) {
    this.activitySensorData.setTimeStamp(timeStamp);
    return this;
  }

  @Override
  public ActivitySensorBuilder setTimestamp(String timeStamp) {
    this.activitySensorData.setTimestamp(timeStamp);
    this.activitySensorData.setFormattedDate();
    return this;
  }

  @Override
  public ActivitySensorBuilder setSensorData(ActivitySensorData.SensorData sensorData) {
    this.activitySensorData.setSensorData(sensorData);
    return this;
  }

  @Override
  public ActivitySensorBuilder setStepCounts(Integer stepCounts) {
    ActivitySensorData.SensorData sensorData = this.activitySensorData.getSensorData();
    sensorData.setStepCounts(stepCounts);
    this.activitySensorData.setSensorData(sensorData);
    return this;
  }

  @Override
  public ActivitySensorBuilder setStepDelta(Integer stepDelta) {
    ActivitySensorData.SensorData sensorData = this.activitySensorData.getSensorData();
    sensorData.setStepDelta(stepDelta);
    this.activitySensorData.setSensorData(sensorData);
    return this;
  }

  @Override
  public ActivitySensorData build() {
    return this.activitySensorData;
  }
}
