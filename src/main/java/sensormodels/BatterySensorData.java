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
public class BatterySensorData extends DatabaseModel {


  @BsonIgnore
    public static final String MY_SQL_TABLE_NAME = "BatterySensorData";

  @BsonIgnore
    public static final String FILE_NAME = "BatterySensor";

    @SerializedName("sensor_name")
    @Expose
    private String sensorName;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("sensor_data")
  @Expose
  private SensorData sensorData;

    @Expose
    private String formatted_date;

  @BsonIgnore
    private File file;

  public String getFormatted_date() {
    return formatted_date;
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

    @Override

    @BsonIgnore
    public String getMongoCollectionName() {
    return "BatterySensorData";
  }

    @Override

    @BsonIgnore
    public Class<BatterySensorData> getClassObject() {
    return BatterySensorData.class;
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
        + " time_stamp VARCHAR(30) , "
        + " formatted_date VARCHAR(10) , "
        + " sensor_name CHAR (25), "
        + " percent INTEGER , "
        + " charging BIT ) ";
  }

    @Override

    @BsonIgnore
    public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (timestamp,time_stamp, formatted_date, sensor_name,percent,charging)"
        + " values (?, ?, ?, ?, ?,?)";
  }

    @Override

    @BsonIgnore
    public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
        preparedStmt.setString(1, this.getTimestamp());
        preparedStmt.setString(2, this.getTimestamp());
        preparedStmt.setString(3, this.getFormatted_date());
        preparedStmt.setString(4, this.getSensorName());
        preparedStmt.setInt(5, this.getSensorData().getPercent());
        preparedStmt.setBoolean(6, this.getSensorData().getCharging());
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

    @SerializedName("percent")
    @Expose
    private Integer percent;

    @SerializedName("charging")
    @Expose
    private Boolean charging;

    public Integer getPercent() {
      return percent;
    }

    public void setPercent(Integer percent) {
      this.percent = percent;
    }

    public Boolean getCharging() {
      return charging;
    }

    public void setCharging(Boolean charging) {
      this.charging = charging;
    }
  }
}
