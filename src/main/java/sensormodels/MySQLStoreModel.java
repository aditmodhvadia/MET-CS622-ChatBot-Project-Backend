package sensormodels;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface MySQLStoreModel {

    String getTableName();

    String getCreateTableQuery();

    String getInsertIntoTableQuery();

    void setQueryData(PreparedStatement preparedStmt) throws SQLException;
}
