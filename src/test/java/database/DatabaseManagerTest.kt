package database

import database.DatabaseManager.Companion.getInstance
import database.MongoDbManager.Companion.instance
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import sensormodels.DatabaseModel

class DatabaseManagerTest {
    private var dbManager: DatabaseManager? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        dbManager = getInstance(null) as DatabaseManager
    }

    @Test
    fun testAddDatabase() {
        dbManager!!.addDatabase(instance as DbManager<DatabaseModel>)
        Assert.assertTrue(dbManager!!.hasDatabaseManager(instance as DbManager<DatabaseModel>))
    }

    @Test
    fun testRemoveDatabase() {
        dbManager!!.addDatabase(instance as DbManager<DatabaseModel>)
        Assert.assertTrue(dbManager!!.hasDatabaseManager(instance as DbManager<DatabaseModel>))
        dbManager!!.removeDatabase(instance as DbManager<DatabaseModel>)
    }
}