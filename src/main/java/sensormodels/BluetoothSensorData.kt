package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.BluetoothSensorData
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class BluetoothSensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
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
    private var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override fun getStartTime(): String {
        return timestamp!!
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return "BluetoothSensorData"
    }

    @BsonIgnore
    override fun getClassObject(): Class<BluetoothSensorData> {
        return BluetoothSensorData::class.java
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
                + " formatted_date VARCHAR(10) , "
                + " sensor_name VARCHAR(30) , "
                + " state CHAR (225)) ")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (timestamp,formatted_date,sensor_name,state)"
                + " values (?, ?, ?, ?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, timestamp)
        preparedStmt.setString(2, formattedDate)
        preparedStmt.setString(3, sensorName)
        preparedStmt.setString(4, sensorData!!.state)
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
}