package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.apache.lucene.document.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import utils.WebAppConstants;

public class ScreenUsageSensorData extends DatabaseModel {

  @BsonIgnore public static final String MY_SQL_TABLE_NAME = "ScreenUsageSensorData";

  @BsonIgnore public static final String FILE_NAME = "ScreenUsage";

  @SerializedName("start_hour")
  @Expose
  private String startHour;

  @SerializedName("end_hour")
  @Expose
  private String endHour;

  @SerializedName("start_timestamp")
  @Expose
  private String startTimestamp;

  @SerializedName("end_timestamp")
  @Expose
  private String endTimestamp;

  @SerializedName("min_elapsed")
  @Expose
  private Double minElapsed;

  @SerializedName("min_start_hour")
  @Expose
  private Double minStartHour;

  @SerializedName("min_end_hour")
  @Expose
  private Integer minEndHour;

  @SerializedName("formatted_date")
  @Expose
  private String formattedDate;

  @BsonIgnore private File file;

  public String getFormattedDate() {
    return formattedDate;
  }

  public String getStartHour() {
    return startHour;
  }

  public void setStartHour(String startHour) {
    this.startHour = startHour;
  }

  public String getEndHour() {
    return endHour;
  }

  public void setEndHour(String endHour) {
    this.endHour = endHour;
  }

  public String getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(String startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public String getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(String endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public Double getMinElapsed() {
    return minElapsed;
  }

  public void setMinElapsed(Double minElapsed) {
    this.minElapsed = minElapsed;
  }

  public Double getMinStartHour() {
    return minStartHour;
  }

  public void setMinStartHour(Double minStartHour) {
    this.minStartHour = minStartHour;
  }

  public Integer getMinEndHour() {
    return minEndHour;
  }

  public void setMinEndHour(Integer minEndHour) {
    this.minEndHour = minEndHour;
  }

  public void setFormattedDate() {
    this.formattedDate = WebAppConstants.inputDateFormat.format(new Date(startTimestamp));
  }

  @Override
  public String getStartTime() {
    return this.getStartTimestamp();
  }

  @Override
  @BsonIgnore
  public String getMongoCollectionName() {
    return "ScreenUsageSensorData";
  }

  @Override
  @BsonIgnore
  public Class<ScreenUsageSensorData> getClassObject() {
    return ScreenUsageSensorData.class;
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
        + "(start_hour VARCHAR(40) , "
        + " end_hour VARCHAR(40),"
        + " start_timestamp VARCHAR(30),  "
        + " end_timestamp VARCHAR(30),  "
        + " formatted_date VARCHAR(10),  "
        + " min_elapsed DOUBLE , "
        + " min_start_hour DOUBLE , "
        + " min_end_hour INTEGER ) ";
  }

  @Override
  @BsonIgnore
  public String getInsertIntoTableQuery() {
    return " insert into "
        + this.getTableName()
        + " (start_hour,end_hour,start_timestamp,end_timestamp, "
        + "formatted_date, min_elapsed,min_start_hour,min_end_hour)"
        + " values (?, ?, ?, ?, ?,?,?,?)";
  }

  @Override
  @BsonIgnore
  public void fillQueryData(PreparedStatement preparedStmt) throws SQLException {
    preparedStmt.setString(1, this.getStartHour());
    preparedStmt.setString(2, this.getEndHour());
    preparedStmt.setString(3, this.getStartTimestamp());
    preparedStmt.setString(4, this.getEndTimestamp());
    preparedStmt.setString(5, this.getFormattedDate());
    preparedStmt.setDouble(6, this.getMinElapsed());
    preparedStmt.setDouble(7, this.getMinStartHour());
    preparedStmt.setInt(8, this.getMinEndHour());
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

  @Override
  @BsonIgnore
  public Document getDocument() {
    return new Document();
  }
}
