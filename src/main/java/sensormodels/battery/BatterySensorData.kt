package sensormodels.battery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import sensormodels.battery.BatterySensorBuilder
import sensormodels.battery.BatterySensorData
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.MySqlStoreModel
import org.bson.codecs.pojo.annotations.BsonIgnore
import utils.WebAppConstants
import java.io.File
import kotlin.Throws
import java.sql.SQLException
import java.sql.PreparedStatement
import java.util.*

class BatterySensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
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

    @BsonIgnore
    private var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override fun getStartTime(): String {
        return timestamp!!
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return "BatterySensorData"
    }

    @BsonIgnore
    override fun getClassObject(): Class<BatterySensorData> {
        return BatterySensorData::class.java
    }

    @BsonIgnore
    override fun getTableName(): String {
        return MY_SQL_TABLE_NAME
    }

    @BsonIgnore
    override fun getCreateTableQuery(): String {
        return ("CREATE TABLE "
                + this.tableName
                + "(timestamp VARCHAR(30) , "
                + " time_stamp VARCHAR(30) , "
                + " formatted_date VARCHAR(10) , "
                + " sensor_name CHAR (25), "
                + " percent INTEGER , "
                + " charging BIT ) ")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (timestamp,time_stamp, formatted_date, sensor_name,percent,charging)"
                + " values (?, ?, ?, ?, ?,?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, timestamp)
        preparedStmt.setString(2, timestamp)
        preparedStmt.setString(3, formattedDate)
        preparedStmt.setString(4, sensorName)
        preparedStmt.setInt(5, sensorData!!.percent!!)
        preparedStmt.setBoolean(6, sensorData!!.charging!!)
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

    @BsonIgnore
    override fun getDocument(): Document {
        return Document()
    }

    class SensorData {
        @SerializedName("percent")
        @Expose
        var percent: Int? = null

        @SerializedName("charging")
        @Expose
        var charging: Boolean? = null
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "BatterySensorData"

        @BsonIgnore
        val FILE_NAME = "BatterySensor"
    }
}