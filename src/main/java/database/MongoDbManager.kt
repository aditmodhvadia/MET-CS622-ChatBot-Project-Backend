package database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
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
import utils.WebAppConstants
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
        val pojoCodecRegistry = codecRegistry
        val settings = getMongoClientSettings(pojoCodecRegistry)
        //        create a connection to MongoDB Client
        val mongoClient = MongoClients.create(settings)
        println("Connected to edu.bu.aditm.database successfully")
        try {
            //        fetch the edu.bu.aditm.database for MongoDB
            database = mongoClient.getDatabase(DATABASE_NAME)
            println("MongoDB initialised")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to initialise MongoDB")
        }
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
            val collection: MongoCollection<Any> = database.getCollection(
                sensorDataList[0]!!.mongoCollectionName,
                sensorDataList[0]!!.javaClass
            )
            collection.insertMany(sensorDataList)
            println(
                "MongoDB Log: Data Inserted for " + sensorDataList[0]!!.mongoCollectionName
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun insertSensorData(sensorData: MongoStoreModel?) {
        val collection: MongoCollection<Any> =
            database.getCollection(sensorData!!.mongoCollectionName, sensorData.javaClass)
        collection.insertOne(sensorData)
        println("MongoDB Log: Data Inserted")
    }

    override fun queryForRunningEvent(date: Date?): ArrayList<ActivFitSensorData?> {
        val cursor = database
            .getCollection(ActivFitSensorData.MONGO_COLLECTION_NAME, ActivFitSensorData::class.java)
            .find(
                Filters.and(
                    Filters.eq("formatted_date", WebAppConstants.inputDateFormat.format(date)),
                    Filters.eq("sensorData.activity", "running")
                )
            )
            .cursor()
        val queryResult = ArrayList<ActivFitSensorData?>() // holds the result from the query
        while (cursor.hasNext()) {
            val nextData = cursor.next()
            queryResult.add(nextData)
        }
        return queryResult
    }

    override fun queryForTotalStepsInDay(date: Date?): Int {
        val cursor = database
            .getCollection(ActivitySensorData.MONGO_COLLECTION_NAME, ActivitySensorData::class.java)
            .find(Filters.eq("formatted_date", WebAppConstants.inputDateFormat.format(date)))
            .sort(Sorts.orderBy(Sorts.descending("step_counts")))
            .cursor()
        var maxStepCount = -1 // Max value of step count for the day
        while (cursor.hasNext()) {
            //            get the next Data
            val sensorData = cursor.next()
            maxStepCount = max(maxStepCount, sensorData.sensorData?.stepCounts ?: 0)
        }
        return maxStepCount
    }

    override fun queryHeartRatesForDay(date: Date?): Int {
        val cursor = database
            .getCollection(HeartRateSensorData.MONGO_COLLECTION_NAME, HeartRateSensorData::class.java)
            .find(Filters.eq("formatted_date", WebAppConstants.inputDateFormat.format(date)))
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