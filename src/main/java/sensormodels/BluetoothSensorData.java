package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.lucene.document.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import utils.WebAppConstants;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class BluetoothSensorData extends DatabaseModel {

  @BsonIgnore
    public static final String MY_SQL_TABLE_NAME = "BluetoothSensorData";

  @BsonIgnore
    public static final String FILE_NAME = "Bluetooth";

    @SerializedName("sensor_name")
    @Expose
    private String sensorName;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

  @Expose private String formatted_date;
  private File file;

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

    @BsonIgnore
    public String getMongoCollectionName() {
    return "BluetoothSensorData";
  }

    @Override

    @BsonIgnore
    public Class<BluetoothSensorData> getClassObject() {
    return BluetoothSensorData.class;
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
        + " state CHAR (225)) ";
  }

    @Override

    @BsonIgnore
    public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (timestamp,formatted_date,sensor_name,state)"
        + " values (?, ?, ?, ?)";
  }

    @Override

    @BsonIgnore
    public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
        preparedStmt.setString(1, this.getTimestamp());
        preparedStmt.setString(2, this.getFormatted_date());
        preparedStmt.setString(3, this.getSensorName());
        preparedStmt.setString(4, this.getSensorData().getState());
  }

    @Override

    @BsonIgnore
    public String getFileName() {
    return FILE_NAME;
  }

    @Override

    @BsonIgnore
    public void setFile(File file) {
    this.file = file;
  }

    @Override

    @BsonIgnore
    public File getFile() {
    return this.file;
  }

    @Override

    @BsonIgnore
    public Document getDocument() {
    return new Document();
  }

  public static class SensorData {
    @SerializedName("state")
    @Expose
    private String state;

    public String getState() {
      return state;
    }

    public void setState(String state) {
      this.state = state;
    }
  }
}
