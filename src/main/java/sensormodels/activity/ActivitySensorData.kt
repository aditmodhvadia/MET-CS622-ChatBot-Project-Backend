package sensormodels.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import sensormodels.DatabaseModel;
import sensormodels.store.models.FileStoreModel;
import sensormodels.store.models.LuceneStoreModel;
import sensormodels.store.models.MongoStoreModel;
import sensormodels.store.models.MySqlStoreModel;
import utils.WebAppConstants;

public class ActivitySensorData implements MongoStoreModel, LuceneStoreModel, FileStoreModel,
    MySqlStoreModel {

  @BsonIgnore public static final String MY_SQL_TABLE_NAME = "ActivitySensorData";

  @BsonIgnore public static final String MONGO_COLLECTION_NAME = "ActivitySensorData";

  @BsonIgnore public static final String FILE_NAME = "Activity";

  @BsonIgnore private File file;

  @SerializedName("sensor_name")
  @Expose
  private String sensorName;

  @SerializedName("timestamp")
  @Expose
  private String timestamp;

  @SerializedName("time_stamp")
  @Expose
  private String timeStamp;

  @SerializedName("formatted_date")
  @Expose
  private String formattedDate;

  @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

  @BsonIgnore
  public static String getMySqlTableName() {
    return MY_SQL_TABLE_NAME;
  }

  public String getSensorName() {
    return sensorName;
  }

  public void setSensorName(String sensorName) {
    this.sensorName = sensorName;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
    setFormattedDate();
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
    this.formattedDate = WebAppConstants.inputDateFormat.format(new Date(timeStamp));
  }

  public String getFormattedDate() {
    return formattedDate;
  }

  public void setFormattedDate(String formattedDate) {
    this.formattedDate = formattedDate;
  }

  public void setFormattedDate() {
    if (timestamp != null) {
      this.formattedDate = WebAppConstants.inputDateFormat.format(new Date(timestamp));
    }
  }

  @Override
  public String getStartTime() {
    return this.getTimeStamp();
  }

  public SensorData getSensorData() {
    return sensorData;
  }

  public void setSensorData(SensorData sensorData) {
    this.sensorData = sensorData;
  }

  @Override
  @BsonIgnore
  public Document getDocument() {
    Document doc = new Document();
    doc.add(
        new IntPoint(
            LuceneManager.LuceneConstants.STEP_COUNT, this.getSensorData().getStepCounts()));
    doc.add(
        new IntPoint(
            LuceneManager.LuceneConstants.STEP_DELTA, this.getSensorData().getStepDelta()));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.FORMATTED_DATE, this.getSensorName(), Field.Store.YES));
    //         use a string field for timestamp because we don't want it tokenized
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.TIMESTAMP, this.getTimestamp(), Field.Store.YES));
    return doc;
  }

  @Override
  @BsonIgnore
  public String getMongoCollectionName() {
    return MONGO_COLLECTION_NAME;
  }

  @Override
  @BsonIgnore
  public Class<ActivitySensorData> getClassObject() {
    return ActivitySensorData.class;
  }

  @Override
  @BsonIgnore
  public String getTableName() {
    return MY_SQL_TABLE_NAME;
  }

  @Override
  @BsonIgnore
  public String getCreateTableQuery() {
    return "CREATE TABLE "
        + this.getTableName()
        + "(time_stamp VARCHAR(30) , "
        + " sensor_name CHAR(25) , "
        + " formatted_date CHAR(10) , "
        + " step_counts INTEGER, "
        + " step_delta INTEGER)";
  }

  @Override
  @BsonIgnore
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (time_stamp,formatted_date, sensor_name, step_counts,step_delta)"
        + " values (?, ?, ?, ?, ?)";
  }

  @Override
  @BsonIgnore
  public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getTimestamp());
    preparedStmt.setString(2, this.getFormattedDate());
    preparedStmt.setString(3, this.getSensorName());
    preparedStmt.setInt(4, this.getSensorData().getStepCounts());
    preparedStmt.setInt(5, this.getSensorData().getStepDelta());
  }

  @Override
  @BsonIgnore
  public String getFileName() {
    return FILE_NAME;
  }

  @Override
  @BsonIgnore
  public File getFile() {
    return this.file;
  }

  @Override
  @BsonIgnore
  public void setFile(File file) {
    this.file = file;
  }

  public static class SensorData {

    @SerializedName("step_counts")
    @Expose
    private Integer stepCounts;
    @SerializedName("step_delta")
    @Expose
    private Integer stepDelta;

    public SensorData() {}

    public Integer getStepCounts() {
      return stepCounts;
    }

    public void setStepCounts(Integer stepCounts) {
      this.stepCounts = stepCounts;
    }

    public Integer getStepDelta() {
      return stepDelta;
    }

    public void setStepDelta(Integer stepDelta) {
      this.stepDelta = stepDelta;
    }
  }
}
