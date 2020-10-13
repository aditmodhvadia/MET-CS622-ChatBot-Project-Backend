package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import sensormodels.store.models.FileStoreModel;
import sensormodels.store.models.MongoStoreModel;
import sensormodels.store.models.MySQLStoreModel;
import utils.WebAppConstants;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class ScreenUsageSensorData implements MongoStoreModel, MySQLStoreModel, FileStoreModel {

    public static final String MY_SQL_TABLE_NAME = "";
    public static final String FILE_NAME = "ScreenUsage";
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
    @Expose
    private String formatted_date;
    private File file;

    public String getFormatted_date() {
        return formatted_date;
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
        this.formatted_date = WebAppConstants.inputDateFormat.format(new Date(startTimestamp));
    }

    @Override
    public String getStartTime() {
        return this.getStartTimestamp();
    }

    @Override
    public String getMongoCollectionName() {
        return "ScreenUsageSensorData";
    }

    @Override
    public Class<ScreenUsageSensorData> getClassObject() {
        return ScreenUsageSensorData.class;
    }

    @Override
    public String getTableName() {
        return MY_SQL_TABLE_NAME;
    }

    @Override
    public String getCreateTableQuery() {
        return "CREATE TABLE " + this.getTableName() +
                "(start_hour VARCHAR(40) , " +
                " end_hour VARCHAR(40)," +
                " start_timestamp VARCHAR(30),  " +
                " end_timestamp VARCHAR(30),  " +
                " formatted_date VARCHAR(10),  " +
                " min_elapsed DOUBLE , " +
                " min_start_hour DOUBLE , " +
                " min_end_hour INTEGER ) ";
    }

    @Override
    public String getInsertIntoTableQuery() {
        return " insert into " + this.getTableName() + " (start_hour,end_hour,start_timestamp,end_timestamp, formatted_date, min_elapsed,min_start_hour,min_end_hour)"
                + " values (?, ?, ?, ?, ?,?,?,?)";
    }

    @Override
    public void setQueryData(PreparedStatement preparedStmt) throws SQLException {
        preparedStmt.setString(1, this.getStartHour());
        preparedStmt.setString(2, this.getEndHour());
        preparedStmt.setString(3, this.getStartTimestamp());
        preparedStmt.setString(4, this.getEndTimestamp());
        preparedStmt.setString(5, this.getFormatted_date());
        preparedStmt.setDouble(6, this.getMinElapsed());
        preparedStmt.setDouble(7, this.getMinStartHour());
        preparedStmt.setInt(8, this.getMinEndHour());
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
}

