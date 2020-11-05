package sensormodels.store.models;

import org.apache.lucene.document.Document;

public interface LuceneStoreModel {
  Document getDocument();
}
