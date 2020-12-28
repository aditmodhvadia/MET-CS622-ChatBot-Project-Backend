package sensormodels.activity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.StringField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class ActivitySensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
    @BsonIgnore
    private var file: File? = null

    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null

    @SerializedName("timestamp")
    @Expose
    var time_stamp: String? = null

    @SerializedName("time_stamp")
    @Expose
    var timeStamp: String? = null
        set(timeStamp) {
            field = timeStamp
            formattedDate = WebAppConstants.inputDateFormat.format(Date(timeStamp))
        }

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null
    private fun getTimestamp(): String? {
        return time_stamp
    }

    fun setTimestamp(timestamp: String?) {
        this.time_stamp = timestamp
        setFormattedDate()
    }

    override fun setFormattedDate() {
        if (time_stamp != null) {
            formattedDate = WebAppConstants.inputDateFormat.format(Date(time_stamp))
        }
    }

    override fun getStartTime(): String {
        return timeStamp!!
    }

    @BsonIgnore
    override fun getDocument(): Document {
        val doc = Document()
        doc.add(
            IntPoint(
                LuceneManager.LuceneConstants.STEP_COUNT, sensorData!!.stepCounts!!
            )
        )
        doc.add(
            IntPoint(
                LuceneManager.LuceneConstants.STEP_DELTA, sensorData!!.stepDelta!!
            )
        )
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
            )
        )
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.FORMATTED_DATE, sensorName, Field.Store.YES
            )
        )
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.TIMESTAMP, getTimestamp(), Field.Store.YES
            )
        )
        return doc
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return MONGO_COLLECTION_NAME
    }

    @BsonIgnore
    override fun getClassObject(): Class<ActivitySensorData> {
        return ActivitySensorData::class.java
    }

    @BsonIgnore
    override fun getTableName(): String {
        return mySqlTableName
    }

    @BsonIgnore
    override fun getCreateTableQuery(): String {
        return ("CREATE TABLE "
                + this.tableName
                + "(time_stamp VARCHAR(30) , "
                + " sensor_name CHAR(25) , "
                + " formatted_date CHAR(10) , "
                + " step_counts INTEGER, "
                + " step_delta INTEGER)")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (time_stamp,formatted_date, sensor_name, step_counts,step_delta)"
                + " values (?, ?, ?, ?, ?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, getTimestamp())
        preparedStmt.setString(2, formattedDate)
        preparedStmt.setString(3, sensorName)
        preparedStmt.setInt(4, sensorData!!.stepCounts!!)
        preparedStmt.setInt(5, sensorData!!.stepDelta!!)
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

    class SensorData {
        @SerializedName("step_counts")
        @Expose
        var stepCounts: Int? = null

        @SerializedName("step_delta")
        @Expose
        var stepDelta: Int? = null
    }

    companion object {
        @get:BsonIgnore
        @BsonIgnore
        val mySqlTableName = "ActivitySensorData"

        @BsonIgnore
        val MONGO_COLLECTION_NAME = "ActivitySensorData"

        @BsonIgnore
        val FILE_NAME = "Activity"
    }
}