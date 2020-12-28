package sensormodels.store.models;

import java.io.File;
import sensormodels.DatabaseModel;

public interface FileStoreModel extends DatabaseModel {
  String getFileName();

  File getFile();

  void setFile(File file);

  void setFormattedDate();

  String getStartTime();

  Class getClassObject();
}
