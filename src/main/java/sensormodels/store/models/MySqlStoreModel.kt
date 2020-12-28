package sensormodels.store.models

import sensormodels.DatabaseModel
import kotlin.Throws
import java.sql.SQLException
import java.sql.PreparedStatement

interface MySqlStoreModel : DatabaseModel {
    val tableName: String?
    val createTableQuery: String?
    val insertIntoTableQuery: String?

    @Throws(SQLException::class)
    fun fillQueryData(preparedStmt: PreparedStatement?)
}