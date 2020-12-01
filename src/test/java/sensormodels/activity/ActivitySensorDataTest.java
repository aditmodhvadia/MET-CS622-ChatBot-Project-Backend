package sensormodels.activity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ActivitySensorDataTest {
  private ActivitySensorData activitySensorData;
  private final String sensorName = "ActivitySensorData";
  private final String startTime = "12/12/2020";
  private final Integer stepCount = 50;
  private final Integer stepCountDelta = 5000;

  @Before
  public void setUp() {
    activitySensorData =
        new ActivitySensorDataBuilder()
            .setSensorName(sensorName)
            .setStepCounts(stepCount)
            .setStepDelta(stepCountDelta)
            .setTimeStamp(startTime)
            .build();
  }

  @Test
  public void getSensorName() {
    assertEquals(sensorName, this.activitySensorData.getSensorName());
  }

  @Test
  public void getTimeStamp() {
    assertEquals(startTime, this.activitySensorData.getTimeStamp());
  }

  @Test
  public void getTimestamp() {
    assertEquals(startTime, this.activitySensorData.getTimeStamp());
  }

  @Test
  public void getFormattedDate() {
    assertEquals(startTime, this.activitySensorData.getFormattedDate());
  }

  @Test
  public void getStartTime() {
    assertEquals(startTime, this.activitySensorData.getStartTime());
  }

  @Test
  public void getStepCount() {
    assertEquals(stepCount, this.activitySensorData.getSensorData().getStepCounts());
  }

  @Test
  public void getStepDelta() {
    assertEquals(stepCountDelta, this.activitySensorData.getSensorData().getStepDelta());
  }
}
