package sensormodels.store.models;

public interface MongoStoreModel {
  String getMongoCollectionName();

  Class getClassObject();
}
