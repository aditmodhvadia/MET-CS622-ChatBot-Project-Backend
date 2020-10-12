package sensormodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import database.LuceneManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import utils.WebAppConstants;

import java.util.Date;

/**
 * @author Adit Modhvadia
 */
public class HeartRateSensorData implements LuceneStoreModel {

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
    public Document getDocument() {
        Document doc = new Document();
//        doc.add(new TextField(LuceneConstants.BPM, String.valueOf(sensorData.getSensorData().getBpm()), Field.Store.YES));
        doc.add(new IntPoint(LuceneManager.LuceneConstants.BPM, this.getSensorData().getBpm()));
        doc.add(new StringField(LuceneManager.LuceneConstants.SENSOR_NAME, this.getSensorName(), Field.Store.YES));
        doc.add(new StringField(LuceneManager.LuceneConstants.FORMATTED_DATE, this.getFormatted_date(), Field.Store.YES));
//         use a string field for timestamp because we don't want it tokenized
        doc.add(new StringField(LuceneManager.LuceneConstants.TIMESTAMP, this.getTimestamp(), Field.Store.YES));
        return doc;
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


