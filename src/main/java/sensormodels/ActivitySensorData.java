package sensormodels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import database.MongoStoreModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class ActivitySensorData implements LuceneStoreModel, MongoStoreModel {

    public static final String MONGO_COLLECTION_NAME = "ActivitySensorData";
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

    @Override
    public Document getDocument() {
        Document doc = new Document();
        doc.add(new IntPoint(LuceneManager.LuceneConstants.STEP_COUNT, this.getSensorData().getStepCounts()));
        doc.add(new IntPoint(LuceneManager.LuceneConstants.STEP_DELTA, this.getSensorData().getStepDelta()));
        doc.add(new StringField(LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneManager.LuceneConstants.FORMATTED_DATE, this.getSensorName(), Field.Store.YES));
//         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneManager.LuceneConstants.TIMESTAMP, this.getTimestamp(), Field.Store.YES));
        return doc;
    }

    @Override
    public String getMongoCollectionName() {
        return MONGO_COLLECTION_NAME;
    }

    @Override
    public Class<ActivitySensorData> getClassObject() {
        return ActivitySensorData.class;
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
