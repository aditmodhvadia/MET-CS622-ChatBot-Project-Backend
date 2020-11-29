package database;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import sensormodels.DatabaseModel;

public final class DatabaseManager implements DbManager<DatabaseModel>, DatabasePublisher {

  private static DatabaseManager instance;
  private final ArrayList<DbManager> databases = new ArrayList<>();

  public DatabaseManager(ServletContext servletContext) {
    init(servletContext);
  }

  /**
   * Get singleton instance of DatabaseManager.
   *
   * @param servletContext servlet context
   * @return singleton instance
   */
  public static DatabaseManager getInstance(ServletContext servletContext) {
    if (instance == null) {
      instance = new DatabaseManager(servletContext);
    }
    return instance;
  }

  @Override
  public void init(@Nullable ServletContext servletContext) {
    databases.forEach(database -> database.init(servletContext));
  }

  @Override
  public <V extends DatabaseModel> void insertSensorDataList(@Nonnull List<V> sensorDataList) {
    for (DbManager dbManager : databases) {
      try {
        dbManager.insertSensorDataList(sensorDataList);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public <V extends DatabaseModel> void insertSensorData(V sensorData) {
    for (DbManager dbManager : databases) {
      try {
        dbManager.insertSensorData(sensorData);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void addDatabase(DbManager dbManager) {
    this.databases.add(dbManager);
  }

  @Override
  public void removeDatabase(DbManager dbManager) {
    this.databases.remove(dbManager);
  }

  @Override
  public boolean hasDatabaseManager(DbManager dbManager) {
    return this.databases.contains(dbManager);
  }
}
