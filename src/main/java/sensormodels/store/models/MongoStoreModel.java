package sensormodels.store.models;

import sensormodels.DatabaseModel;

public interface MongoStoreModel extends DatabaseModel {
  String getMongoCollectionName();

  Class getClassObject();
}
