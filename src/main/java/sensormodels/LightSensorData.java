package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import utils.WebAppConstants;

public class LightSensorData extends DatabaseModel {

  @BsonIgnore public static final String MY_SQL_TABLE_NAME = "LightSensorData";

  @BsonIgnore public static final String FILE_NAME = "LightSensor";

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

  @SerializedName("formatted_date")
  @Expose
  private String formattedDate;

  @BsonIgnore private File file;

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
    this.formattedDate = WebAppConstants.inputDateFormat.format(new Date(timestamp));
  }

  @Override
  public String getStartTime() {
    return this.getTimestamp();
  }

  public String getFormattedDate() {
    return formattedDate;
  }

  @Override
  @BsonIgnore
  public Document getDocument() {
    Document doc = new Document();
    if (this.getLuxValue() != null) {
      doc.add(
          new TextField(LuceneManager.LuceneConstants.LUX, this.getLuxValue(), Field.Store.YES));
    }
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.FORMATTED_DATE,
            this.getFormattedDate(),
            Field.Store.YES));
    //         use a string field for timestamp because we don't want it tokenized
    doc.add(
        new StringField(
            LuceneManager.LuceneConstants.TIMESTAMP, this.getTimestamp(), Field.Store.YES));
    return doc;
  }

  @Override
  @BsonIgnore
  public String getMongoCollectionName() {
    return "LightSensorData";
  }

  @Override
  @BsonIgnore
  public Class<LightSensorData> getClassObject() {
    return LightSensorData.class;
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
        + "(timestamp VARCHAR(30) , "
        + " formatted_date VARCHAR(10) , "
        + " sensor_name VARCHAR(30) , "
        + " lux INTEGER) ";
  }

  @Override
  @BsonIgnore
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (timestamp, formatted_date, sensor_name,lux)"
        + " values (?, ?, ?, ?)";
  }

  @Override
  @BsonIgnore
  public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getTimestamp());
    preparedStmt.setString(2, this.getFormattedDate());
    preparedStmt.setString(3, this.getSensorName());
    preparedStmt.setInt(4, this.getSensorData().getLux());
  }

  @Override
  @BsonIgnore
  public String getFileName() {
    return FILE_NAME;
  }

  @Override
  public void setFile(File file) {
    this.file = file;
  }

  @Override
  @BsonIgnore
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
