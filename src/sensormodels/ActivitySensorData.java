package sensormodels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class ActivitySensorData {

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

    public static class SensorData {

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
