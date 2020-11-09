package database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import sensormodels.ActivFitSensorData;
import sensormodels.ActivitySensorData;
import sensormodels.HeartRateSensorData;
import sensormodels.store.models.MongoStoreModel;
import utils.WebAppConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/** @author Adit Modhvadia */
public class MongoDBManager implements DbManager<MongoStoreModel>, DatabaseQueryRunner {
  private static MongoDBManager instance;
  public static final String DATABASE_NAME = "SensorData";
  private static MongoDatabase database;

  private MongoDBManager() {
    init();
  }

  /**
   * Singleton method to get the instance of the class
   *
   * @return singleton instance of the class
   */
  public static MongoDBManager getInstance() {
    if (instance == null) {
      instance = new MongoDBManager();
    }
    return instance;
  }

  /**
   * Use to initialise the MongoDB Database and Connections corresponding to the Sensors Call only
   * once per execution
   */
  public void init() {
    CodecRegistry pojoCodecRegistry =
        fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(
                PojoCodecProvider.builder()
                    //                    .register(ActivFitSensorData.class)
                    .automatic(true)
                    .build())); // custom codec required to store POJO in MongoDB

    ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
    MongoClientSettings settings =
        MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(pojoCodecRegistry)
            .build();

    //        create a connection to MongoDB Client
    MongoClient mongoClient = MongoClients.create(settings);
    System.out.println("Connected to edu.bu.aditm.database successfully");

    try {
      //        fetch the edu.bu.aditm.database for MongoDB
      database = mongoClient.getDatabase(DATABASE_NAME);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("MongoDB initialised");

    //        TODO: Maybe have to initialise the collections
    //        initialise all the mongoCollections for the Sensors
  }

  @Override
  public <V extends MongoStoreModel> void insertSensorDataList(List<V> sensorDataList) {
    try {
      MongoCollection<V> collection =
          database.getCollection(
              sensorDataList.get(0).getMongoCollectionName(),
              sensorDataList.get(0).getClassObject());
      collection.insertMany(sensorDataList);
      System.out.println(
          "MongoDB Log: Data Inserted for " + sensorDataList.get(0).getMongoCollectionName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public <V extends MongoStoreModel> void insertSensorData(V sensorData) {
    MongoCollection<V> collection =
        database.getCollection(sensorData.getMongoCollectionName(), sensorData.getClassObject());
    collection.insertOne(sensorData);
    System.out.println("MongoDB Log: Data Inserted");
  }

  @Override
  public ArrayList<ActivFitSensorData> queryForRunningEvent(Date userDate) {
    MongoCursor<ActivFitSensorData> cursor =
        database
            .getCollection(ActivFitSensorData.MONGO_COLLECTION_NAME, ActivFitSensorData.class)
            .find(
                and(
                    eq("formatted_date", WebAppConstants.inputDateFormat.format(userDate)),
                    eq("sensorData.activity", "running")))
            .cursor();
    ArrayList<ActivFitSensorData> queryResult =
        new ArrayList<>(); // holds the result from the query
    while (cursor.hasNext()) {
      ActivFitSensorData nextData = cursor.next();
      queryResult.add(nextData);
    }
    return queryResult;
  }

  @Override
  public int queryForTotalStepsInDay(Date userDate) {
    MongoCursor<ActivitySensorData> cursor =
        database
            .getCollection(ActivitySensorData.MONGO_COLLECTION_NAME, ActivitySensorData.class)
            .find(eq("formatted_date", WebAppConstants.inputDateFormat.format(userDate)))
            .sort(orderBy(descending("step_counts")))
            .cursor();
    int maxStepCount = -1; // Max value of step count for the day
    while (cursor.hasNext()) {
      //            get the next Data
      ActivitySensorData sensorData = cursor.next();
      if (sensorData.getSensorData().getStepCounts() > maxStepCount) {
        maxStepCount = sensorData.getSensorData().getStepCounts();
      }
    }
    return maxStepCount;
  }

  @Override
  public int queryHeartRatesForDay(Date date) {
    MongoCursor<HeartRateSensorData> cursor =
        database
            .getCollection(HeartRateSensorData.MONGO_COLLECTION_NAME, HeartRateSensorData.class)
            .find(eq("formatted_date", WebAppConstants.inputDateFormat.format(date)))
            .cursor();
    int heartRateCounter = 0;
    while (cursor.hasNext()) {
      heartRateCounter++;
      cursor.next();
    }
    return heartRateCounter;
  }
}
