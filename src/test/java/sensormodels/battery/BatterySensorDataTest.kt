package sensormodels.battery;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BatterySensorDataTest {
  private final String sensorName = "ActivFitSensorData";
  private final String startTime = "12/12/2020";
  private final Integer percent = 50;
  private final boolean charging = true;
  private BatterySensorData batterySensorData;

  @Before
  public void setUp() {
    batterySensorData =
        new BatterySensorDataBuilder()
            .setSensorName(sensorName)
            .setCharging(charging)
            .setPercent(percent)
            .setTimeStamp(startTime)
            .build();
  }

  @Test
  public void getSensorName() {
    assertEquals(sensorName, batterySensorData.getSensorName());
  }

  @Test
  public void getStartTime() {
    assertEquals(startTime, batterySensorData.getTimestamp());
  }

  @Test
  public void getCharging() {
    assertEquals(charging, batterySensorData.getSensorData().getCharging());
  }

  @Test
  public void getPercent() {
    assertEquals(percent, batterySensorData.getSensorData().getPercent());
  }

  @Test
  public void getFormattedDate() {
    assertEquals(startTime, batterySensorData.getFormattedDate());
  }

  @After
  public void tearDown() throws Exception {
    batterySensorData = null;
  }
}
