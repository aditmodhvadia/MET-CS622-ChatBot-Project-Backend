package sensormodels.store.models;

import java.io.File;

public interface FileStoreModel {
  String getFileName();

  File getFile();

  void setFile(File file);

  void setFormattedDate();

  String getStartTime();

  Class getClassObject();
}
