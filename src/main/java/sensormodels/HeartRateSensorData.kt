package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.StringField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.HeartRateSensorData
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class HeartRateSensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
        private set

    @BsonIgnore
    private var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override fun getStartTime(): String {
        return timestamp!!
    }

    @BsonIgnore
    override fun getDocument(): Document {
        val doc = Document()
        //        doc.add(new TextField(LuceneConstants.BPM,
        // String.valueOf(sensorData.getSensorData().getBpm()), Field.Store.YES));
        doc.add(IntPoint(LuceneManager.LuceneConstants.BPM, sensorData!!.bpm!!))
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
            )
        )
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.FORMATTED_DATE,
                formattedDate,
                Field.Store.YES
            )
        )
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.TIMESTAMP, timestamp, Field.Store.YES
            )
        )
        return doc
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return MONGO_COLLECTION_NAME
    }

    @BsonIgnore
    override fun getClassObject(): Class<HeartRateSensorData> {
        return HeartRateSensorData::class.java
    }

    @BsonIgnore
    override fun getTableName(): String {
        return MY_SQL_TABLE_NAME
    }

    @BsonIgnore
    override fun getCreateTableQuery(): String {
        return ("CREATE TABLE ${this.tableName}(timestamp VARCHAR(30) ,  formatted_date VARCHAR(10) ,  sensor_name CHAR (25),  bpm INTEGER)")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into ${this.tableName} (timestamp, formatted_date, sensor_name,bpm) values (?, ?, ?, ?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.apply {
            setString(1, timestamp)
            setString(2, formattedDate)
            setString(3, sensorName)
            setInt(4, sensorData!!.bpm!!)
        }
    }

    @BsonIgnore
    override fun getFileName(): String {
        return FILE_NAME
    }

    @BsonIgnore
    override fun getFile(): File {
        return file!!
    }

    override fun setFile(file: File) {
        this.file = file
    }

    class SensorData {
        @SerializedName("bpm")
        @Expose
        var bpm: Int? = null
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "HeartRateSensorData"

        @BsonIgnore
        val MONGO_COLLECTION_NAME = "HeartRateSensorData"

        @BsonIgnore
        val FILE_NAME = "HeartRate"
    }
}