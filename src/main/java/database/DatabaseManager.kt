package database

import sensormodels.DatabaseModel
import java.util.*
import java.util.function.Consumer
import javax.annotation.Nonnull
import javax.servlet.ServletContext

class DatabaseManager(servletContext: ServletContext?) : DbManager<DatabaseModel>,
    DatabasePublisher {
    private val databases = ArrayList<DbManager<DatabaseModel>>()
    override fun init(servletContext: ServletContext?) {
        databases.add(MongoDbManager.instance as DbManager<DatabaseModel>)
        databases.add(LuceneManager.getInstance(servletContext!!)!! as DbManager<DatabaseModel>)
        databases.add(MySqlManager.instance!! as DbManager<DatabaseModel>)
        databases.forEach(Consumer { database: DbManager<DatabaseModel> -> database.init(servletContext) })
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<DatabaseModel>) {
        for (dbManager in databases) {
            try {
                dbManager.insertSensorDataList(sensorDataList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun insertSensorData(sensorData: DatabaseModel) {
        for (dbManager in databases) {
            try {
                dbManager.insertSensorData(sensorData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun addDatabase(dbManager: DbManager<DatabaseModel>) {
        databases.add(dbManager)
    }

    override fun removeDatabase(dbManager: DbManager<DatabaseModel>) {
        databases.remove(dbManager)
    }

    override fun hasDatabaseManager(dbManager: DbManager<DatabaseModel>): Boolean {
        return databases.contains(dbManager)
    }

    companion object {
        private var instance: DatabaseManager? = null

        /**
         * Get singleton instance of DatabaseManager.
         *
         * @param servletContext servlet context
         * @return singleton instance
         */
        @JvmStatic
        fun getInstance(servletContext: ServletContext?): DatabaseManager? {
            if (instance == null) {
                instance = DatabaseManager(servletContext)
            }
            return instance
        }
    }

    init {
        init(servletContext)
    }
}