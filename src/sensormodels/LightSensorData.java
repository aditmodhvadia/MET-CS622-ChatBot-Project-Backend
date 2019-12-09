package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class LightSensorData {

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
    @Expose
    private String formatted_date;

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

    public String getFormatted_date() {
        return formatted_date;
    }

    public class SensorData {
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
