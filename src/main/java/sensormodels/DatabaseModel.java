package sensormodels;

import sensormodels.store.models.FileStoreModel;
import sensormodels.store.models.LuceneStoreModel;
import sensormodels.store.models.MongoStoreModel;
import sensormodels.store.models.MySQLStoreModel;

public abstract class DatabaseModel
    implements FileStoreModel, MongoStoreModel, MySQLStoreModel, LuceneStoreModel {}
