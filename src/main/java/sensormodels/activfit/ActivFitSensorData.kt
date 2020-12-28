package sensormodels.activfit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.activfit.ActivFitSensorData
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class ActivFitSensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
    @BsonIgnore
    private var file: File? = null

    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp? = null

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null

    @Expose
    @SerializedName("formatted_date")
    var formattedDate: String? = null
        private set

    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp!!.startTime))
    }

    override fun getStartTime(): String {
        return timestamp!!.startTime!!
    }

    @BsonIgnore
    override fun getDocument(): Document {
        val doc = Document()
        doc.add(
            TextField(
                LuceneManager.LuceneConstants.ACTIVITY,
                sensorData!!.activity,
                Field.Store.YES
            )
        )
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
            )
        )
        if (formattedDate != null) {
            doc.add(
                StringField(
                    LuceneManager.LuceneConstants.FORMATTED_DATE,
                    formattedDate,
                    Field.Store.YES
                )
            )
        }
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.START_TIME,
                timestamp!!.startTime,
                Field.Store.YES
            )
        )
        if (timestamp!!.endTime != null) {
            doc.add(
                StringField(
                    LuceneManager.LuceneConstants.END_TIME,
                    timestamp!!.endTime,
                    Field.Store.YES
                )
            )
        }
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.TIMESTAMP,
                timestamp!!.startTime,
                Field.Store.YES
            )
        )
        return doc
    }

    @BsonIgnore
    override fun getMongoCollectionName() = MONGO_COLLECTION_NAME

    @BsonIgnore
    override fun getClassObject(): Class<ActivFitSensorData> {
        return ActivFitSensorData::class.java
    }

    @BsonIgnore
    override fun getTableName(): String {
        return MY_SQL_TABLE_NAME
    }

    @BsonIgnore
    override fun getCreateTableQuery(): String {
        return ("CREATE TABLE "
                + this.tableName
                + " (start_time VARCHAR(30) , "
                + " formatted_date VARCHAR(10) , "
                + " end_time VARCHAR(30) , "
                + " duration INTEGER , "
                + " activity VARCHAR(55) ) ")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (start_time, end_time, formatted_date, duration, activity)"
                + " values (?, ?, ?, ?, ?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, timestamp!!.startTime)
        preparedStmt.setString(2, timestamp!!.endTime)
        preparedStmt.setString(3, formattedDate)
        preparedStmt.setInt(4, sensorData!!.duration!!)
        preparedStmt.setString(5, sensorData!!.activity)
    }

    @BsonIgnore
    override fun getFileName(): String {
        return FILE_NAME
    }

    @BsonIgnore
    override fun getFile(): File {
        return file!!
    }

    @BsonIgnore
    override fun setFile(file: File) {
        this.file = file
    }

    class Timestamp {
        @SerializedName("start_time")
        @Expose
        var startTime: String? = null

        @SerializedName("end_time")
        @Expose
        var endTime: String? = null

        constructor()
        constructor(startTime: String?, endTime: String?) {
            this.startTime = startTime
            this.endTime = endTime
        }
    }

    class SensorData {
        @SerializedName("activity")
        @Expose
        var activity: String? = null

        @SerializedName("duration")
        @Expose
        var duration: Int? = null
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "ActivFitSensorData"

        @BsonIgnore
        val MONGO_COLLECTION_NAME = "ActivFitSensorData"

        @BsonIgnore
        val FILE_NAME = "ActivFit"
    }
}