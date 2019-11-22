package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adit Modhvadia
 */
public class HeartRateSensorData {

    @SerializedName("sensor_name")
    @Expose
    private String sensorName;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
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
    }

    public SensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(SensorData sensorData) {
        this.sensorData = sensorData;
    }

    public static class SensorData {

        public SensorData() {
        }

        @SerializedName("bpm")
        @Expose
        private Integer bpm;

        public Integer getBpm() {
            return bpm;
        }

        public void setBpm(Integer bpm) {
            this.bpm = bpm;
        }

    }
}


