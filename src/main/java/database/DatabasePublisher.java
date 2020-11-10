package database;

import sensormodels.DatabaseModel;

public interface DatabasePublisher<T extends DbManager<DatabaseModel>> {
  void addDatabase(T dbManager);

  void removeDatabase(T dbManager);

  boolean hasDatabaseManager(T dbManager);
}
