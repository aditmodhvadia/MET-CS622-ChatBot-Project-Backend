package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.*
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.util.*

class BluetoothSensorData : SuperStoreModel {
    @SerializedName("sensor_name")
    @Expose
    var sensorName: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("sensor_data")
    @Expose
    var sensorData: SensorData? = null

    @Expose
    @SerializedName("formatted_date")
    var formattedDate: String? = null
        private set
    override val fileName: String = FILE_NAME
    override var file: File? = null

    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override val startTime: String?
        get() = TODO("Not yet implemented")

    class SensorData {
        @SerializedName("state")
        @Expose
        var state: String? = null
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "BluetoothSensorData"

        @BsonIgnore
        val FILE_NAME = "Bluetooth"
    }

    override val document: Document = Document()
    override val mongoCollectionName: String = "BluetoothSensorData"
    override val tableName: String = MY_SQL_TABLE_NAME
    override val createTableQuery: String
        get() = ("CREATE TABLE "
                + this.tableName
                + "(timestamp VARCHAR(30) , "
                + " formatted_date VARCHAR(10) , "
                + " sensor_name VARCHAR(30) , "
                + " state CHAR (225)) ")
    override val insertIntoTableQuery: String
        get() = (" insert into "
                + this.tableName
                + " (timestamp,formatted_date,sensor_name,state)"
                + " values (?, ?, ?, ?)")

    override fun fillQueryData(preparedStmt: PreparedStatement?) {
        preparedStmt?.apply {
            setString(1, timestamp)
            setString(2, formattedDate)
            setString(3, sensorName)
            setString(4, sensorData!!.state)
        }
    }
}