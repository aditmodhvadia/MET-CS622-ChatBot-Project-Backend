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
import java.util.*

class ActivitySensorData(override var file: File? = null) : MongoStoreModel, LuceneStoreModel, FileStoreModel,
    MySqlStoreModel {
    override val fileName: String
        get() = FILE_NAME

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

    override val startTime: String?
        get() = TODO("Not yet implemented")

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

    override val document: Document
        get() {
            return Document().apply {
                add(
                    IntPoint(
                        LuceneManager.LuceneConstants.STEP_COUNT, sensorData!!.stepCounts!!
                    )
                )
                add(
                    IntPoint(
                        LuceneManager.LuceneConstants.STEP_DELTA, sensorData!!.stepDelta!!
                    )
                )
                add(
                    StringField(
                        LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
                    )
                )
                add(
                    StringField(
                        LuceneManager.LuceneConstants.FORMATTED_DATE, sensorName, Field.Store.YES
                    )
                )
                //         use a string field for timestamp because we don't want it tokenized
                add(
                    StringField(
                        LuceneManager.LuceneConstants.TIMESTAMP, getTimestamp(), Field.Store.YES
                    )
                )
            }
        }

    override fun fillQueryData(preparedStmt: PreparedStatement?) {
        preparedStmt?.apply {
            setString(1, getTimestamp())
            setString(2, formattedDate)
            setString(3, sensorName)
            setInt(4, sensorData!!.stepCounts!!)
            setInt(5, sensorData!!.stepDelta!!)
        }
    }

    override val mongoCollectionName: String
        get() = MONGO_COLLECTION_NAME
    override val tableName: String
        get() = mySqlTableName
    override val createTableQuery: String
        get() = ("CREATE TABLE "
                + this.tableName
                + "(time_stamp VARCHAR(30) , "
                + " sensor_name CHAR(25) , "
                + " formatted_date CHAR(10) , "
                + " step_counts INTEGER, "
                + " step_delta INTEGER)")

    override val insertIntoTableQuery: String?
        get() = (" insert into "
                + this.tableName
                + " (time_stamp,formatted_date, sensor_name, step_counts,step_delta)"
                + " values (?, ?, ?, ?, ?)")
}