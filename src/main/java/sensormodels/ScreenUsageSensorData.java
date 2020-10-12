package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.MongoStoreModel;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class ScreenUsageSensorData implements MongoStoreModel {

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
    public String getMongoCollectionName() {
        return "ScreenUsageSensorData";
    }

    @Override
    public Class<ScreenUsageSensorData> getClassObject() {
        return ScreenUsageSensorData.class;
    }
}

