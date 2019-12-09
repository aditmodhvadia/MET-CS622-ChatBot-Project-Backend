package database;


import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sensormodels.*;
import utils.QueryUtils;
import utils.WebAppConstants;

import java.util.*;

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

    /**
     * Use to initialise the MongoDB Database and Connections corresponding to the Sensors
     * Call only once per execution
     */
    public static void init() {
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
        collection.insertOne(document);
        System.out.println("MongoDB Log: Data Inserted");
    }

    /**
     * Get the result from ActivFit Sensor Data for the given Date, if there is a running event for it
     *
     * @param userDate given Date
     * @return List of ActivFitSensorData having running activity for the given Date
     */
    public static ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
//        get the next Day Date as well
        Date nextDate = QueryUtils.addDayToDate(userDate, 1);
//        fetch all record from the collection
        MongoCursor<ActivFitSensorData> cursor = activFitSensorDataMongoCollection.find().cursor();
        ArrayList<ActivFitSensorData> queryResult = new ArrayList<>();  // holds the result from the query
        while (cursor.hasNext()) {
            ActivFitSensorData nextData = cursor.next();
//            get startDate of the SensorData entry and then check if it lies between the user entered Date and the next day or not
            Date startDate = new Date(nextData.getTimestamp().getStartTime());
            if (startDate.after(userDate) && startDate.before(nextDate)) {  // check it lies within range
                if (!nextData.getSensorData().getActivity().equals("unknown") && Objects.equals(nextData.getSensorData().getActivity(), "running")) {
//                    SensorData entry lies between the dates and is for running, so add it to the result List
                    queryResult.add(nextData);
                }
            }
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
//        fetch all documents from the collection
        MongoCursor<ActivitySensorData> cursor = activitySensorDataMongoCollection.find().cursor();
        int maxStepCount = (int) -Infinity;    // Max value of step count for the day
        while (cursor.hasNext()) {
//            get the next Data
            ActivitySensorData sensorData = cursor.next();
            Date sensorDate = new Date(sensorData.getTimestamp());
            String sensorFormattedDate = WebAppConstants.inputDateFormat.format(sensorDate);
            String userFormattedDate = WebAppConstants.inputDateFormat.format(userDate);
//            format both the user input date and the sensor date to compare if they are equal
            if (sensorFormattedDate.equals(userFormattedDate)) {    // both dates are equal
                if (sensorData.getSensorData().getStepCounts() > maxStepCount) {
//                    found a step count larger than the maxStepCount, so update it
                    maxStepCount = sensorData.getSensorData().getStepCounts();
                }
            }
        }
        return maxStepCount;
    }

    /**
     * Called to query and fetch all days heart rate data to count the number of notifications user receives for the day
     *
     * @return HashMap containing key value pair of date and the corresponding count for the day
     */
    public static HashMap<String, Integer> queryHeartRatesForDay() {
        MongoCursor<HeartRateSensorData> cursor = heartRateSensorDataMongoCollection.find().cursor();
        HashMap<String, Integer> heartRateCounter = new HashMap<>();
        while (cursor.hasNext()) {
//            get the next in line Sensor Data object
            HeartRateSensorData sensorData = cursor.next();
//            get the date and format it accordingly
            Date sensorDate = new Date(sensorData.getTimestamp());
            String sensorFormattedDate = WebAppConstants.inputDateFormat.format(sensorDate);

            if (heartRateCounter.containsKey(sensorFormattedDate)) {
//                HashMap contains the count for the date
                int count = heartRateCounter.get(sensorFormattedDate);
//                increment the value of count for that day
                heartRateCounter.replace(sensorFormattedDate, count++, count);
            } else {
//                sensor data not present, so put it in hashmap with counter set to
                heartRateCounter.put(sensorFormattedDate, 1);
            }
        }
        return heartRateCounter;
    }
}
