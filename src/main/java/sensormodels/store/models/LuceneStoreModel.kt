package sensormodels.store.models

import org.apache.lucene.document.Document
import sensormodels.DatabaseModel

interface LuceneStoreModel : DatabaseModel {
    val document: Document?
}