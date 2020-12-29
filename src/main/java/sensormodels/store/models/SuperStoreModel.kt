package sensormodels.store.models

interface SuperStoreModel : FileStoreModel, MySqlStoreModel, MongoStoreModel, LuceneStoreModel