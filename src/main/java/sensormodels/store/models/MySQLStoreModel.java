package sensormodels.store.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface MySQLStoreModel {

  String getTableName();

  String getCreateTableQuery();

  String getInsertIntoTableQuery();

  void fillQueryData(PreparedStatement preparedStmt) throws SQLException;
}
