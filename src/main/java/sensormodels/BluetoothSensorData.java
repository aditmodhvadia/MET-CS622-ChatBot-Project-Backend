package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.MongoStoreModel;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class BluetoothSensorData implements MongoStoreModel {
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

    @Override
    public String getMongoCollectionName() {
        return "BluetoothSensorData";
    }

    @Override
    public Class<BluetoothSensorData> getClassObject() {
        return BluetoothSensorData.class;
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

