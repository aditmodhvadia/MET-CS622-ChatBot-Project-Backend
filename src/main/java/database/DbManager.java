package database;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;

public interface DbManager<T> {

  /**
   * Initialize the database.
   *
   * @param servletContext servlet context
   */
  void init(@Nullable ServletContext servletContext);

  /**
   * Use to insert given documents of the given type into the given target collection.
   *
   * @param sensorDataList given list of sensor data to be inserted
   */
  <V extends T> void insertSensorDataList(@Nonnull List<V> sensorDataList);

  /**
   * Use to insert given document of the given type into the given target collection.
   *
   * @param sensorData given sensor data to be inserted
   */
  <V extends T> void insertSensorData(V sensorData);
}
