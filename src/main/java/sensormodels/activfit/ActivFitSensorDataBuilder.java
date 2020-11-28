package sensormodels.activfit;

public class ActivFitSensorDataBuilder implements ActivFitSensorBuilder {
  private final ActivFitSensorData activFitSensorData;

  public ActivFitSensorDataBuilder() {
    activFitSensorData = new ActivFitSensorData();
    activFitSensorData.setTimestamp(new ActivFitSensorData.Timestamp());
    activFitSensorData.setSensorData(new ActivFitSensorData.SensorData());
  }

  @Override
  public ActivFitSensorBuilder setSensorName(String sensorName) {
    this.activFitSensorData.setSensorName(sensorName);
    return this;
  }

  @Override
  public ActivFitSensorBuilder setTimeStamp(ActivFitSensorData.Timestamp timeStamp) {
    this.activFitSensorData.setTimestamp(timeStamp);
    return this;
  }

  @Override
  public ActivFitSensorBuilder setSensorData(ActivFitSensorData.SensorData sensorData) {
    this.activFitSensorData.setSensorData(sensorData);
    return this;
  }

  @Override
  public ActivFitSensorBuilder setStartTime(String startTime) {
    this.activFitSensorData.getTimestamp().setStartTime(startTime);
    this.activFitSensorData.setFormattedDate();
    return this;
  }

  @Override
  public ActivFitSensorBuilder setEndTime(String endTime) {
    this.activFitSensorData.getTimestamp().setEndTime(endTime);
    return this;
  }

  @Override
  public ActivFitSensorBuilder setActivity(String activity) {
    this.activFitSensorData.getSensorData().setActivity(activity);
    return this;
  }

  @Override
  public ActivFitSensorBuilder setDuration(Integer duration) {
    this.activFitSensorData.getSensorData().setDuration(duration);
    return this;
  }

  @Override
  public ActivFitSensorData build() {
    return this.activFitSensorData;
  }
}
