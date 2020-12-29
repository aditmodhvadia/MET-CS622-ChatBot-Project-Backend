package database

import sensormodels.DatabaseModel

interface DatabasePublisher {
    fun addDatabase(dbManager: DbManager<DatabaseModel>)
    fun removeDatabase(dbManager: DbManager<DatabaseModel>)
    fun hasDatabaseManager(dbManager: DbManager<DatabaseModel>): Boolean
}