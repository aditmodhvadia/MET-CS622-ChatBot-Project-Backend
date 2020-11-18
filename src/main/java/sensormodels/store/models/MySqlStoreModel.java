package sensormodels.store.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface MySqlStoreModel {

  String getTableName();

  String getCreateTableQuery();

  String getInsertIntoTableQuery();

  void fillQueryData(PreparedStatement preparedStmt) throws SQLException;
}
