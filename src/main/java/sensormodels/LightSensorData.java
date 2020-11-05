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
public class LightSensorData extends DatabaseModel {

  public static final String MY_SQL_TABLE_NAME = "LightSensorData";
  public static final String FILE_NAME = "LightSensor";

  @SerializedName("sensor_name")
  @Expose
  private String sensorName;

  @SerializedName("timestamp")
  @Expose
  private String timestamp;

  @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

  private String luxValue;
  @Expose private String formatted_date;
  private File file;

  public String getLuxValue() {
    return luxValue;
  }

  public void setLuxValue(String luxValue) {
    this.luxValue = luxValue;
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
  }

  public SensorData getSensorData() {
    return sensorData;
  }

  public void setSensorData(SensorData sensorData) {
    this.sensorData = sensorData;
  }

  public void setFormattedDate() {
    this.formatted_date = WebAppConstants.inputDateFormat.format(new Date(timestamp));
  }

  @Override
  public String getStartTime() {
    return this.getTimestamp();
  }

  public String getFormatted_date() {
    return formatted_date;
  }

  @Override
  public Document getDocument() {
    Document doc = new Document();
    doc.add(new TextField(LuceneManager.LuceneConstants.LUX, this.getLuxValue(), Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.FORMATTED_DATE,
            this.getFormatted_date(),
            Field.Store.YES));
    //         use a string field for timestamp because we don't want it tokenized
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.TIMESTAMP, this.getTimestamp(), Field.Store.YES));
    return doc;
  }

  @Override
  public String getMongoCollectionName() {
    return "LightSensorData";
  }

  @Override
  public Class<LightSensorData> getClassObject() {
    return LightSensorData.class;
  }

  @Override
  public String getTableName() {
    return MY_SQL_TABLE_NAME;
  }

  @Override
  public String getCreateTableQuery() {
    return "CREATE TABLE "
        + this.getTableName()
        + "(timestamp VARCHAR(30) , "
        + " formatted_date VARCHAR(10) , "
        + " sensor_name VARCHAR(30) , "
        + " lux INTEGER) ";
  }

  @Override
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (timestamp, formatted_date, sensor_name,lux)"
        + " values (?, ?, ?, ?)";
  }

  @Override
  public void setQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getTimestamp());
    preparedStmt.setString(2, this.getFormatted_date());
    preparedStmt.setString(3, this.getSensorName());
    preparedStmt.setInt(4, this.getSensorData().getLux());
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

  public static class SensorData {
    @SerializedName("lux")
    @Expose
    private Integer lux;

    public Integer getLux() {
      return lux;
    }

    public void setLux(Integer lux) {
      this.lux = lux;
    }
  }
}
