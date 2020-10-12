package database;

public interface MongoStoreModel {
    String getMongoCollectionName();

    Class getClassObject();
}
