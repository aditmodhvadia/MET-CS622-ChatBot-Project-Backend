package database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import sensormodels.HeartRateSensorData
import sensormodels.activfit.ActivFitSensorData
import sensormodels.activity.ActivitySensorData
import sensormodels.store.models.MongoStoreModel
import java.util.*
import javax.annotation.Nonnull
import javax.servlet.ServletContext
import kotlin.math.max

class MongoDbManager private constructor() : DbManager<MongoStoreModel?>, DatabaseQueryRunner {
    /**
     * Use to initialise the MongoDB Database and Connections corresponding to the Sensors Call only
     * once per execution.
     *
     * @param servletContext servlet context
     */
    override fun init(servletContext: ServletContext?) {
        //        create a connection to MongoDB Client
        println("Connected to edu.bu.aditm.database successfully")
        try {
            // Kotlin DSL
            database = mongoClient {
                applyConnectionString("mongodb://localhost:27017".connectionString())
                codecRegistry(codecRegistry)
            }.getDatabase(DATABASE_NAME)
            println("MongoDB initialised")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to initialise MongoDB")
        }
    }

    // Kotlin DSL
    private fun mongoClient(mongoClientInitializer: MongoClientSettings.Builder.() -> Unit): MongoClient {
        return MongoClients.create(MongoClientSettings.builder().apply(mongoClientInitializer).build())
    }

    @Nonnull
    private fun getMongoClientSettings(pojoCodecRegistry: CodecRegistry): MongoClientSettings {
        val connectionString = ConnectionString("mongodb://localhost:27017")
        return MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(pojoCodecRegistry)
            .build()
    }

    //                    .register(ActivFitSensorData.class)
    @get:Nonnull
    private val codecRegistry: CodecRegistry
        get() = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(
                PojoCodecProvider.builder() //                    .register(ActivFitSensorData.class)
                    .automatic(true)
                    .build()
            )
        )

    override fun insertSensorDataList(@Nonnull sensorDataList: List<MongoStoreModel?>) {
        try {
            database.getCollection(
                sensorDataList[0]!!.mongoCollectionName,
                sensorDataList[0]!!.javaClass
            ).apply {
                insertMany(sensorDataList)
            }
            println(
                "MongoDB Log: Data Inserted for ${sensorDataList[0]!!.mongoCollectionName}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun insertSensorData(sensorData: MongoStoreModel?) {
        database.getCollection(sensorData!!.mongoCollectionName, sensorData.javaClass).apply {
            insertOne(sensorData)
        }
        println("MongoDB Log: Data Inserted")
    }

    override fun queryForRunningEvent(date: String): List<ActivFitSensorData> {
        val cursor = database
            .getCollection(ActivFitSensorData.MONGO_COLLECTION_NAME, ActivFitSensorData::class.java)
            .find(
                Filters.and(
                    Filters.eq("formatted_date", date),
                    Filters.eq("sensorData.activity", "running")
                )
            )
            .cursor()
        val queryResult = ArrayList<ActivFitSensorData>() // holds the result from the query
        while (cursor.hasNext()) {
            val nextData = cursor.next()
            queryResult.add(nextData)
        }
        return queryResult
    }

    override fun queryForTotalStepsInDay(date: String): Int {
        val cursor = database
            .getCollection(ActivitySensorData.MONGO_COLLECTION_NAME, ActivitySensorData::class.java)
            .find(Filters.eq("formatted_date", date))
            .sort(Sorts.orderBy(Sorts.descending("step_counts")))
            .cursor()
        var maxStepCount = -1 // Max value of step count for the day
        while (cursor.hasNext()) {
            val sensorData = cursor.next()
            maxStepCount = max(maxStepCount, sensorData.sensorData.stepCounts)
        }
        return maxStepCount
    }

    override fun queryHeartRatesForDay(date: String): Int {
        val cursor = database
            .getCollection(HeartRateSensorData.MONGO_COLLECTION_NAME, HeartRateSensorData::class.java)
            .find(Filters.eq("formatted_date", date))
            .cursor()
        var heartRateCounter = 0
        while (cursor.hasNext()) {
            heartRateCounter++
            cursor.next()
        }
        return heartRateCounter
    }

    companion object {
        const val DATABASE_NAME = "SensorData"

        /**
         * Singleton method to get the instance of the class.
         *
         * @return singleton instance of the class
         */
        @JvmStatic
        var instance: MongoDbManager? = null
            get() {
                if (field == null) {
                    field = MongoDbManager()
                }
                return field
            }
            private set
        private lateinit var database: MongoDatabase
    }

    init {
        init(null)
    }
}

private fun String.connectionString(): ConnectionString = ConnectionString(this)
