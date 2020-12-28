package sensormodels.store.models;

import org.apache.lucene.document.Document;
import sensormodels.DatabaseModel;

public interface LuceneStoreModel extends DatabaseModel {
  Document getDocument();
}
