package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.util.*

class LightSensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null
    var luxValue: String? = null

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
        private set
    override val fileName: String = FILE_NAME

    @BsonIgnore
    override var file: File? = null

    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override val startTime: String?
        get() = TODO("Not yet implemented")

    class SensorData {
        @SerializedName("lux")
        @Expose
        var lux: Int? = null
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "LightSensorData"

        @BsonIgnore
        val FILE_NAME = "LightSensor"
    }

    override val document: Document
        get() {
            return Document().apply {
                if (luxValue != null) {
                    add(
                        TextField(LuceneManager.LuceneConstants.LUX, luxValue, Field.Store.YES)
                    )
                }
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
        }
    override val mongoCollectionName: String = "LightSensorData"
    override val tableName: String = MY_SQL_TABLE_NAME
    override val createTableQuery: String = ("CREATE TABLE "
            + this.tableName
            + "(timestamp VARCHAR(30) , "
            + " formatted_date VARCHAR(10) , "
            + " sensor_name VARCHAR(30) , "
            + " lux INTEGER) ")
    override val insertIntoTableQuery: String = (" insert into "
            + this.tableName
            + " (timestamp, formatted_date, sensor_name,lux)"
            + " values (?, ?, ?, ?)")

    override fun fillQueryData(preparedStmt: PreparedStatement?) {
        preparedStmt?.apply {
            setString(1, timestamp)
            setString(2, formattedDate)
            setString(3, sensorName)
            setInt(4, sensorData!!.lux!!)
        }
    }
}