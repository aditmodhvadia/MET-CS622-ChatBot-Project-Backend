package sensormodels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.MongoStoreModel;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class BatterySensorData implements MongoStoreModel {

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
    public String getMongoCollectionName() {
        return "BatterySensorData";
    }

    @Override
    public Class<BatterySensorData> getClassObject() {
        return BatterySensorData.class;
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
