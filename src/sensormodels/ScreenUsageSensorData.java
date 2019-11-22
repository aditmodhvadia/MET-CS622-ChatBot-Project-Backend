package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adit Modhvadia
 */
public class ScreenUsageSensorData {

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
}

