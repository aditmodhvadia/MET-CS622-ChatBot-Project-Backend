package database;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.operation.OrderBy;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sensormodels.*;
import utils.QueryUtils;
import utils.WebAppConstants;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static jdk.nashorn.internal.objects.Global.Infinity;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


/**
 * @author Adit Modhvadia
 */
public class MongoDBManager {
    public static final String DATABASE_NAME = "SensorData";
    public static MongoCollection<ActivitySensorData> activitySensorDataMongoCollection;
    public static MongoCollection<ActivFitSensorData> activFitSensorDataMongoCollection;
    public static MongoCollection<BatterySensorData> batterySensorDataMongoCollection;
    public static MongoCollection<BluetoothSensorData> bluetoothSensorDataMongoCollection;
    public static MongoCollection<HeartRateSensorData> heartRateSensorDataMongoCollection;
    public static MongoCollection<LightSensorData> lightSensorDataMongoCollection;
    public static MongoCollection<ScreenUsageSensorData> screenUsageSensorDataMongoCollection;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static boolean shouldInsert = false;

    /**
     * Use to initialise the MongoDB Database and Connections corresponding to the Sensors
     * Call only once per execution
     */
    public static void init() {
        shouldInsert = true;

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));    // custom codec required to store POJO in MongoDB

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .build();
//        create a connection to MongoDB Client
        mongoClient = MongoClients.create(settings);
        System.out.println("Connected to edu.bu.aditm.database successfully");

//        fetch the edu.bu.aditm.database for MongoDB
        database = mongoClient.getDatabase(DATABASE_NAME);

//        initialise all the mongoCollections for the Sensors
        activitySensorDataMongoCollection = database.getCollection("ActivitySensorData", ActivitySensorData.class);
        activFitSensorDataMongoCollection = database.getCollection("ActivFitSensorData", ActivFitSensorData.class);
        batterySensorDataMongoCollection = database.getCollection("BatterySensorData", BatterySensorData.class);
        bluetoothSensorDataMongoCollection = database.getCollection("BluetoothSensorData", BluetoothSensorData.class);
        heartRateSensorDataMongoCollection = database.getCollection("HeartRateSensorData", HeartRateSensorData.class);
        lightSensorDataMongoCollection = database.getCollection("LightSensorData", LightSensorData.class);
        screenUsageSensorDataMongoCollection = database.getCollection("ScreenUsageSensorData", ScreenUsageSensorData.class);
    }

    /**
     * Use to insert given document of the given type into the given target collection
     *
     * @param collection  given target collection
     * @param document    given document to be inserted
     * @param <TDocument> given type of document
     */
    public static <TDocument> void insertDocumentIntoCollection(MongoCollection<TDocument> collection, TDocument document) {
        if (shouldInsert) {
            collection.insertOne(document);
            System.out.println("MongoDB Log: Data Inserted");
        }
    }

    /**
     * Get the result from ActivFit Sensor Data for the given Date, if there is a running event for it
     *
     * @param userDate given Date
     * @return List of ActivFitSensorData having running activity for the given Date
     */
    public static ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
        MongoCursor<ActivFitSensorData> cursor = activFitSensorDataMongoCollection
                .find(and(eq("formatted_date", WebAppConstants.inputDateFormat.format(userDate)),
                        eq("sensorData.activity", "running"))).cursor();
        ArrayList<ActivFitSensorData> queryResult = new ArrayList<>();  // holds the result from the query
        while (cursor.hasNext()) {
            ActivFitSensorData nextData = cursor.next();
            queryResult.add(nextData);
        }
        return queryResult;
    }

    /**
     * Called to query the number of steps user takes for the given day
     *
     * @param userDate given day
     * @return step count for the given day
     */
    public static int queryForTotalStepsInDay(Date userDate) {
        MongoCursor<ActivitySensorData> cursor = activitySensorDataMongoCollection
                .find(eq("formatted_date", WebAppConstants.inputDateFormat.format(userDate)))
                .sort(orderBy(descending("step_counts")))
                .cursor();
        int maxStepCount = (int) -Infinity;    // Max value of step count for the day
        while (cursor.hasNext()) {
//            get the next Data
            ActivitySensorData sensorData = cursor.next();
            if (sensorData.getSensorData().getStepCounts() > maxStepCount) {
                maxStepCount = sensorData.getSensorData().getStepCounts();
            }
        }
        return maxStepCount;
    }

    /**
     * Called to query and fetch all days heart rate data to count the number of notifications user receives for the day
     *
     * @param date
     * @return int value of the number of notifications received by the user for heart rates
     */
    public static int queryHeartRatesForDay(Date date) {
        MongoCursor<HeartRateSensorData> cursor = heartRateSensorDataMongoCollection
                .find(eq("formatted_date", WebAppConstants.inputDateFormat.format(date))).cursor();
        int heartRateCounter = 0;
        while (cursor.hasNext()) {
            heartRateCounter++;
            cursor.next();
        }
        return heartRateCounter;
    }
}
