package database;

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

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/** @author Adit Modhvadia */
public class MongoDBManager implements DbManager, DatabaseQueryRunner {
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
                    .automatic(true)
                    .build())); // custom codec required to store POJO in MongoDB

    MongoClientSettings settings =
        MongoClientSettings.builder().codecRegistry(pojoCodecRegistry).build();

    //        create a connection to MongoDB Client
    MongoClient mongoClient = MongoClients.create(settings);
    System.out.println("Connected to edu.bu.aditm.database successfully");

    //        fetch the edu.bu.aditm.database for MongoDB
    database = mongoClient.getDatabase(DATABASE_NAME);

    //        TODO: Maybe have to initialise the collections
    //        initialise all the mongoCollections for the Sensors
  }

  /**
   * Use to insert given document of the given type into the given target collection
   *
   * @param document given document to be inserted
   */
  public <T extends MongoStoreModel> void insertDocumentIntoCollection(T document) {
    MongoCollection<T> collection =
        database.getCollection(document.getMongoCollectionName(), document.getClassObject());
    collection.insertOne(document);
    System.out.println("MongoDB Log: Data Inserted");
  }

  /**
   * Use to insert given documents of the given type into the given target collection
   *
   * @param documents given documents to be inserted
   */
  public <T extends MongoStoreModel> void insertDocumentsIntoCollection(ArrayList<T> documents) {
    MongoCollection<T> collection =
        database.getCollection(
            documents.get(0).getMongoCollectionName(), documents.get(0).getClassObject());
    collection.insertMany(documents);
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
