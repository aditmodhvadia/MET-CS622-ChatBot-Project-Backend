package database;

import javax.servlet.ServletContext;
import java.util.List;

public interface DbManager<T> {

  void init(ServletContext servletContext);

  /**
   * Use to insert given documents of the given type into the given target collection
   *
   * @param sensorDataList given list of sensor data to be inserted
   */
  <V extends T> void insertSensorDataList(List<V> sensorDataList) throws Exception;

  /**
   * Use to insert given document of the given type into the given target collection
   *
   * @param sensorData given sensor data to be inserted
   */
  <V extends T> void insertSensorData(V sensorData) throws Exception;
}
