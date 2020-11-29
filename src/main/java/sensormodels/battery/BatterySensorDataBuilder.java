package sensormodels.battery;

public class BatterySensorDataBuilder implements BatterySensorBuilder {
  private final BatterySensorData batterySensorData;

  public BatterySensorDataBuilder() {
    this.batterySensorData = new BatterySensorData();
    this.batterySensorData.setSensorData(new BatterySensorData.SensorData());
  }

  @Override
  public BatterySensorBuilder setSensorName(String sensorName) {
    this.batterySensorData.setSensorName(sensorName);
    return this;
  }

  @Override
  public BatterySensorBuilder setTimeStamp(String timeStamp) {
    this.batterySensorData.setTimestamp(timeStamp);
    this.batterySensorData.setFormattedDate();
    return this;
  }

  @Override
  public BatterySensorBuilder setSensorData(BatterySensorData.SensorData sensorData) {
    this.batterySensorData.setSensorData(sensorData);
    return this;
  }

  @Override
  public BatterySensorBuilder setPercent(Integer percent) {
    this.batterySensorData.getSensorData().setPercent(percent);
    return this;
  }

  @Override
  public BatterySensorBuilder setCharging(Boolean charging) {
    this.batterySensorData.getSensorData().setCharging(charging);
    return this;
  }

  @Override
  public BatterySensorData build() {
    return this.batterySensorData;
  }
}
