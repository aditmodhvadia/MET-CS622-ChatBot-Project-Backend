package sensormodels.store.models

import sensormodels.DatabaseModel

interface MongoStoreModel : DatabaseModel {
    val mongoCollectionName: String?
}