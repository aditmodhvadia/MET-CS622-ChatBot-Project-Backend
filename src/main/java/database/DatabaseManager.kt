package database

import sensormodels.DatabaseModel
import sensormodels.store.models.SuperStoreModel
import java.util.*
import javax.annotation.Nonnull
import javax.servlet.ServletContext

class DatabaseManager(servletContext: ServletContext?) : DbManager<DatabaseModel>,
    DatabasePublisher {
    private val databases = ArrayList<DbManager<DatabaseModel>>()
    override fun init(servletContext: ServletContext?) {
        databases.apply {
            add(MongoDbManager.instance as DbManager<DatabaseModel>)
            add(LuceneManager.getInstance(servletContext)!! as DbManager<DatabaseModel>)
            add(MySqlManager.instance!! as DbManager<DatabaseModel>)
            forEach { database -> database.init(servletContext) }
        }
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<DatabaseModel>) {
        databases.forEach {
            try {
                it.insertSensorDataList(sensorDataList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun insertSensorData(sensorData: DatabaseModel) {
        databases.forEach {
            try {
                it.insertSensorData(sensorData)
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
        return dbManager in databases
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
        fun getInstance(servletContext: ServletContext?): DbManager<SuperStoreModel> {
            if (instance == null) {
                instance = DatabaseManager(servletContext)
            }
            return instance as DbManager<SuperStoreModel>
        }
    }

    init {
        init(servletContext)
    }
}