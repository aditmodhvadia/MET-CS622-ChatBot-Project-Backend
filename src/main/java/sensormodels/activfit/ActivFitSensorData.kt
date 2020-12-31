package sensormodels.activfit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.SuperStoreModel
import utils.WebAppConstants
import utils.WebAppConstants.formatted
import java.io.File
import java.sql.PreparedStatement
import java.util.*

data class ActivFitSensorData(
    override var file: File? = null, @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp? = null,

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null
) : SuperStoreModel {

    @Expose
    @SerializedName("formatted_date")
    var formattedDate: String? = null
        private set
    override val fileName: String
        get() = FILE_NAME

    override fun setFormattedDate() {
        timestamp?.startTime?.let {
            formattedDate = Date(it).formatted()
        }
    }

    override val startTime: String? = timestamp?.startTime

    data class Timestamp(
        @SerializedName("start_time")
        @Expose
        var startTime: String? = null,
        @SerializedName("end_time")
        @Expose
        var endTime: String? = null
    )

    data class SensorData(
        @SerializedName("activity")
        @Expose
        var activity: String? = null,
        @SerializedName("duration")
        @Expose
        var duration: Int? = null
    )

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "ActivFitSensorData"

        @BsonIgnore
        val MONGO_COLLECTION_NAME = "ActivFitSensorData"

        @BsonIgnore
        val FILE_NAME = "ActivFit"
    }

    override val document: Document
        get() {
            return Document().apply {
                add(
                    TextField(
                        LuceneManager.LuceneConstants.ACTIVITY,
                        sensorData!!.activity,
                        Field.Store.YES
                    )
                )
                add(
                    StringField(
                        LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
                    )
                )
                if (formattedDate != null) {
                    add(
                        StringField(
                            LuceneManager.LuceneConstants.FORMATTED_DATE,
                            formattedDate,
                            Field.Store.YES
                        )
                    )
                }
                add(
                    StringField(
                        LuceneManager.LuceneConstants.START_TIME,
                        timestamp!!.startTime,
                        Field.Store.YES
                    )
                )
                if (timestamp!!.endTime != null) {
                    add(
                        StringField(
                            LuceneManager.LuceneConstants.END_TIME,
                            timestamp!!.endTime,
                            Field.Store.YES
                        )
                    )
                }
                //         use a string field for timestamp because we don't want it tokenized
                add(
                    StringField(
                        LuceneManager.LuceneConstants.TIMESTAMP,
                        timestamp!!.startTime,
                        Field.Store.YES
                    )
                )
            }
        }
    override val mongoCollectionName: String = MONGO_COLLECTION_NAME
    override val tableName: String = MY_SQL_TABLE_NAME
    override val createTableQuery: String =
        "CREATE TABLE ${this.tableName} (start_time VARCHAR(30) ,  formatted_date VARCHAR(10) ,  end_time VARCHAR(30)" +
                " ,  duration INTEGER ,  activity VARCHAR(55) ) "
    override val insertIntoTableQuery: String =
        "INSERT into ${this.tableName} (start_time, end_time, formatted_date, duration, activity) values (?, ?, ?, ?, ?)"

    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.apply {
            setString(1, timestamp!!.startTime)
            setString(2, timestamp!!.endTime)
            setString(3, formattedDate)
            setInt(4, sensorData!!.duration!!)
            setString(5, sensorData!!.activity)
        }
    }
}