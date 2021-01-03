package sensormodels.battery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.SuperStoreModel
import utils.WebAppConstants.formatted
import java.io.File
import java.sql.PreparedStatement
import java.util.*

data class BatterySensorData(
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

    @Expose
    @SerializedName("formatted_date")
    var formattedDate: String? = null
        private set
    override val fileName: String = FILE_NAME
    override var file: File? = null

    override fun setFormattedDate() {
        formattedDate = Date(timestamp).formatted()
    }

    override val startTime: String? = timestamp

    data class SensorData(
        @SerializedName("percent")
        @Expose
        var percent: Int? = null,
        @SerializedName("charging")
        @Expose
        var charging: Boolean? = null
    )

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "BatterySensorData"

        @BsonIgnore
        val FILE_NAME = "BatterySensor"
    }

    override val document: Document by lazy { Document() }
    override val mongoCollectionName: String = "BatterySensorData"
    override val tableName: String = MY_SQL_TABLE_NAME
    override val createTableQuery: String =
        ("CREATE TABLE ${this.tableName}(timestamp VARCHAR(30) ,  time_stamp VARCHAR(30) ,  formatted_date VARCHAR(10) ,  sensor_name CHAR (25),  percent INTEGER ,  charging BIT ) ")
    override val insertIntoTableQuery: String =
        (" insert into ${this.tableName} (timestamp,time_stamp, formatted_date, sensor_name,percent,charging) values (?, ?, ?, ?, ?,?)")

    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.apply {
            setString(1, timestamp)
            setString(2, timestamp)
            setString(3, formattedDate)
            setString(4, sensorName)
            setInt(5, sensorData!!.percent!!)
            setBoolean(6, sensorData!!.charging!!)
        }
    }
}