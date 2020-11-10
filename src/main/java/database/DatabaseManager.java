package database;

import sensormodels.DatabaseModel;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements DbManager<DatabaseModel> {

  private static DatabaseManager instance;
  private final ArrayList<DbManager> databases = new ArrayList<>();

  public DatabaseManager(ServletContext servletContext) {
    init(servletContext);
  }

  public static DatabaseManager getInstance(ServletContext servletContext) {
    if (instance == null) {
      instance = new DatabaseManager(servletContext);
    }
    return instance;
  }

  @Override
  public void init(ServletContext servletContext) {
    //    databases.add(MongoDBManager.getInstance());
    //    databases.add(MySqlManager.getInstance());
    databases.add(LuceneManager.getInstance());
    //    LuceneManager.getInstance().updateServletContext(servletContext);

    databases.forEach(dbManager -> dbManager.init(servletContext));
  }

  @Override
  public <V extends DatabaseModel> void insertSensorDataList(List<V> sensorDataList) {
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
}
