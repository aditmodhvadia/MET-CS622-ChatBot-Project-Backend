package sensormodels.activfit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public class ActivFitSensorDataTest {
  private final String sensorName = "ActivFitSensorData";
  private final String activity = "Activity";
  private final String startTime = "12/12/2020";
  private final String endTime = "endTime";
  private final Integer duration = 100;
  private ActivFitSensorData activFitSensorData;

  @Before
  public void setUp() {
    activFitSensorData =
        new ActivFitSensorDataBuilder()
            .setSensorName(sensorName)
            .setActivity(activity)
            .setDuration(duration)
            .setStartTime(startTime)
            .setEndTime(endTime)
            .build();
  }

  @Test
  public void getSensorName() {
    assertEquals(sensorName, activFitSensorData.getSensorName());
  }

  @Test
  public void getActivity() {
    assertEquals(activity, activFitSensorData.getSensorData().getActivity());
  }

  @Test
  public void getStartTime() {
    assertEquals(startTime, activFitSensorData.getStartTime());
  }

  @Test
  public void getEndTime() {
    assertEquals(endTime, activFitSensorData.getTimestamp().getEndTime());
  }

  @Test
  public void getDuration() {
    assertEquals(duration, activFitSensorData.getSensorData().getDuration());
  }

  @Test
  public void getFormattedDate() {
    assertEquals(startTime, activFitSensorData.getFormattedDate());
  }

  @Test
  public void filterSensorData() {
    List<ActivFitSensorData> sensorDataList = new ArrayList<>();
    sensorDataList.add(activFitSensorData);
    sensorDataList.add(activFitSensorData);
    sensorDataList.add(activFitSensorData);

    assertEquals(
        sensorDataList,
        sensorDataList.stream()
            .filter(sensorData -> sensorData.getSensorName().equals(sensorName))
            .collect(Collectors.toList()));

    assertEquals(
        new ArrayList<>(),
        sensorDataList.stream()
            .filter(sensorData -> !sensorData.getSensorName().equals(sensorName))
            .collect(Collectors.toList()));
  }
}
