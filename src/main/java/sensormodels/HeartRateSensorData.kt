package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.StringField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.SuperStoreModel
import utils.WebAppConstants.formatted
import java.io.File
import java.sql.PreparedStatement
import java.util.*

data class HeartRateSensorData(
    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null,
    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null,
    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null
) : SuperStoreModel {

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
        private set
    override val fileName: String
        get() = FILE_NAME

    override var file: File? = null

    override fun setFormattedDate() {
        formattedDate = Date(timestamp).formatted()
    }

    override val startTime = timestamp

    data class SensorData(
        @SerializedName("bpm")
        @Expose
        var bpm: Int? = null
    )

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "HeartRateSensorData"

        @BsonIgnore
        val MONGO_COLLECTION_NAME = "HeartRateSensorData"

        @BsonIgnore
        val FILE_NAME = "HeartRate"
    }

    override val document: Document
        get() = Document().apply {
            add(IntPoint(LuceneManager.LuceneConstants.BPM, sensorData!!.bpm!!))
            add(
                StringField(
                    LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
                )
            )
            add(
                StringField(
                    LuceneManager.LuceneConstants.FORMATTED_DATE,
                    formattedDate,
                    Field.Store.YES
                )
            )
            //         use a string field for timestamp because we don't want it tokenized
            add(
                StringField(
                    LuceneManager.LuceneConstants.TIMESTAMP, timestamp, Field.Store.YES
                )
            )
        }

    override val mongoCollectionName = MONGO_COLLECTION_NAME
    override val tableName = MY_SQL_TABLE_NAME
    override val createTableQuery =
        "CREATE TABLE ${this.tableName}(timestamp VARCHAR(30) ,  formatted_date VARCHAR(10) ,  sensor_name CHAR (25),  bpm INTEGER)"
    override val insertIntoTableQuery =
        " insert into ${this.tableName} (timestamp, formatted_date, sensor_name,bpm) values (?, ?, ?, ?)"

    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.apply {
            setString(1, timestamp)
            setString(2, formattedDate)
            setString(3, sensorName)
            setInt(4, sensorData!!.bpm!!)
        }
    }
}