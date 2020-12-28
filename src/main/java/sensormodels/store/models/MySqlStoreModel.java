package sensormodels.store.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import sensormodels.DatabaseModel;

public interface MySqlStoreModel extends DatabaseModel {

  String getTableName();

  String getCreateTableQuery();

  String getInsertIntoTableQuery();

  void fillQueryData(PreparedStatement preparedStmt) throws SQLException;
}
