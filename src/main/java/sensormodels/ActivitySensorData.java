package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import utils.WebAppConstants;

import javax.persistence.Transient;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class ActivitySensorData extends DatabaseModel {

  @Transient
  @BsonIgnore
  public static final String MY_SQL_TABLE_NAME = "ActivitySensorData";
  @Transient
  @BsonIgnore
  public static final String MONGO_COLLECTION_NAME = "ActivitySensorData";
  @Transient
  @BsonIgnore
  public static final String FILE_NAME = "Activity";
  @Transient
  @BsonIgnore
  private File file;

  @SerializedName("sensor_name")
  @Expose
  private String sensorName;

  @SerializedName("timestamp")
  @Expose
  private String timestamp;

  @SerializedName("time_stamp")
  @Expose
  private String time_stamp;

  @SerializedName("formatted_date")
  @Expose
  private String formatted_date;

  @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

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
    this.formatted_date = WebAppConstants.inputDateFormat.format(new Date(timestamp));
  }

  public String getTime_stamp() {
    return time_stamp;
  }

  public String getFormatted_date() {
    return formatted_date;
  }

  public void setFormattedDate() {
    this.formatted_date = WebAppConstants.inputDateFormat.format(new Date(timestamp));
  }

  @Override
  public String getStartTime() {
    return this.getTime_stamp();
  }

  public void setTime_stamp(String time_stamp) {
    this.time_stamp = time_stamp;
    this.formatted_date = WebAppConstants.inputDateFormat.format(new Date(time_stamp));
  }

  public SensorData getSensorData() {
    return sensorData;
  }

  public void setSensorData(SensorData sensorData) {
    this.sensorData = sensorData;
  }

  @Override
  @Transient
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
  @Transient
  @BsonIgnore
  public String getMongoCollectionName() {
    return MONGO_COLLECTION_NAME;
  }

  @Override
  @Transient
  @BsonIgnore
  public Class<ActivitySensorData> getClassObject() {
    return ActivitySensorData.class;
  }

  @Override
  @Transient
  @BsonIgnore
  public String getTableName() {
    return MY_SQL_TABLE_NAME;
  }

  @Override
  @Transient
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
  @Transient
  @BsonIgnore
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (time_stamp,formatted_date, sensor_name, step_counts,step_delta)"
        + " values (?, ?, ?, ?, ?)";
  }

  @Override
  @Transient
  @BsonIgnore
  public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getTimestamp());
    preparedStmt.setString(2, this.getFormatted_date());
    preparedStmt.setString(3, this.getSensorName());
    preparedStmt.setInt(4, this.getSensorData().getStepCounts());
    preparedStmt.setInt(5, this.getSensorData().getStepDelta());
  }

  @Override
  @Transient
  @BsonIgnore
  public String getFileName() {
    return FILE_NAME;
  }

  @Override
  @Transient
  @BsonIgnore
  public void setFile(File file) {
    this.file = file;
  }

  @Transient
  @BsonIgnore
  public static String getMySqlTableName() {
    return MY_SQL_TABLE_NAME;
  }

  public void setFormatted_date(String formatted_date) {
    this.formatted_date = formatted_date;
  }

  @Override
  @Transient
  @BsonIgnore
  public File getFile() {
    return this.file;
  }

  public static class SensorData {

    public SensorData() {
    }

    @SerializedName("step_counts")
    @Expose
    private Integer stepCounts;

    @SerializedName("step_delta")
    @Expose
    private Integer stepDelta;

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
