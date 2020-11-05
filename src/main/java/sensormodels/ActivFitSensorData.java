package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import utils.WebAppConstants;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/** @author Adit Modhvadia */
public class ActivFitSensorData extends DatabaseModel {

  public static final String MY_SQL_TABLE_NAME = "ActivFitSensorData";
  public static final String MONGO_COLLECTION_NAME = "ActivFitSensorData";
  public static final String FILE_NAME = "ActivFit";
  private File file;

  @SerializedName("sensor_name")
  @Expose
  private String sensorName;

  @SerializedName("timestamp")
  @Expose
  private Timestamp timestamp;

  @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

  @Expose private String formatted_date;

  public String getFormatted_date() {
    return formatted_date;
  }

  public String getSensorName() {
    return sensorName;
  }

  public void setSensorName(String sensorName) {
    this.sensorName = sensorName;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public SensorData getSensorData() {
    return sensorData;
  }

  public void setSensorData(SensorData sensorData) {
    this.sensorData = sensorData;
  }

  @Override
  public void setFormattedDate() {
    this.formatted_date =
        WebAppConstants.inputDateFormat.format(new Date(timestamp.getStartTime()));
  }

  @Override
  public String getStartTime() {
    return this.getTimestamp().getStartTime();
  }

  @Override
  public Document getDocument() {
    Document doc = new Document();
    doc.add(
        new TextField(
            LuceneManager.LuceneConstants.ACTIVITY,
            this.getSensorData().getActivity(),
            Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.FORMATTED_DATE,
            this.getFormatted_date(),
            Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.START_TIME,
            this.getTimestamp().getStartTime(),
            Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.END_TIME,
            this.getTimestamp().getEndTime(),
            Field.Store.YES));
    //         use a string field for timestamp because we don't want it tokenized
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.TIMESTAMP,
            this.getTimestamp().getStartTime(),
            Field.Store.YES));
    return doc;
  }

  @Override
  public String getMongoCollectionName() {
    return MONGO_COLLECTION_NAME;
  }

  @Override
  public Class<ActivFitSensorData> getClassObject() {
    return ActivFitSensorData.class;
  }

  @Override
  public String getTableName() {
    return MY_SQL_TABLE_NAME;
  }

  @Override
  public String getCreateTableQuery() {
    return "CREATE TABLE "
        + this.getTableName()
        + " (start_time VARCHAR(30) , "
        + " formatted_date VARCHAR(10) , "
        + " end_time VARCHAR(30) , "
        + " duration INTEGER , "
        + " activity VARCHAR(55) ) ";
  }

  @Override
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (start_time, end_time, formatted_date, duration, activity)"
        + " values (?, ?, ?, ?, ?)";
  }

  @Override
  public void setQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getTimestamp().getStartTime());
    preparedStmt.setString(2, this.getTimestamp().getEndTime());
    preparedStmt.setString(3, this.getFormatted_date());
    preparedStmt.setInt(4, this.getSensorData().getDuration());
    preparedStmt.setString(5, this.getSensorData().getActivity());
  }

  @Override
  public String getFileName() {
    return FILE_NAME;
  }

  @Override
  public void setFile(File file) {
    this.file = file;
  }

  @Override
  public File getFile() {
    return this.file;
  }

  public static class Timestamp {
    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("end_time")
    @Expose
    private String endTime;

    public Timestamp() {}

    public Timestamp(String startTime, String endTime) {
      this.startTime = startTime;
      this.endTime = endTime;
    }

    public String getStartTime() {
      return startTime;
    }

    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    public String getEndTime() {
      return endTime;
    }

    public void setEndTime(String endTime) {
      this.endTime = endTime;
    }
  }

  public static class SensorData {
    @SerializedName("activity")
    @Expose
    private String activity;

    @SerializedName("duration")
    @Expose
    private Integer duration;

    public String getActivity() {
      return activity;
    }

    public void setActivity(String activity) {
      this.activity = activity;
    }

    public Integer getDuration() {
      return duration;
    }

    public void setDuration(Integer duration) {
      this.duration = duration;
    }
  }
}
