package database

import sensormodels.DatabaseModel

interface DatabasePublisher<T : DbManager<DatabaseModel?>?> {
    fun addDatabase(dbManager: T)
    fun removeDatabase(dbManager: T)
    fun hasDatabaseManager(dbManager: T): Boolean
}