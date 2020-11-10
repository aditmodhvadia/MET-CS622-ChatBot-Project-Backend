package sensormodels.store.models;

import java.io.File;

public interface FileStoreModel {
  String getFileName();

  void setFile(File file);

  File getFile();

  void setFormattedDate();

  String getStartTime();

  Class getClassObject();
}
