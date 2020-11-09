package database;

import java.util.List;

public interface DbManager<T> {

  void init();

  /**
   * Use to insert given documents of the given type into the given target collection
   *
   * @param sensorDataList given list of sensor data to be inserted
   */
  <V extends T> void insertSensorDataList(List<V> sensorDataList);

  /**
   * Use to insert given document of the given type into the given target collection
   *
   * @param sensorData given sensor data to be inserted
   */
  <V extends T> void insertSensorData(V sensorData);
}
