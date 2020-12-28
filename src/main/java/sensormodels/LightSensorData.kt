package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.MySqlStoreModel
import org.bson.codecs.pojo.annotations.BsonIgnore
import utils.WebAppConstants
import database.LuceneManager
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import sensormodels.LightSensorData
import java.io.File
import kotlin.Throws
import java.sql.SQLException
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

    @BsonIgnore
    private var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(timestamp))
    }

    override fun getStartTime(): String {
        return timestamp!!
    }

    @BsonIgnore
    override fun getDocument(): Document {
        val doc = Document()
        if (luxValue != null) {
            doc.add(
                TextField(LuceneManager.LuceneConstants.LUX, luxValue, Field.Store.YES)
            )
        }
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.SENSOR_NAME, sensorName, Field.Store.YES
            )
        )
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.FORMATTED_DATE,
                formattedDate,
                Field.Store.YES
            )
        )
        //         use a string field for timestamp because we don't want it tokenized
        doc.add(
            StringField(
                LuceneManager.LuceneConstants.TIMESTAMP, timestamp, Field.Store.YES
            )
        )
        return doc
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return "LightSensorData"
    }

    @BsonIgnore
    override fun getClassObject(): Class<LightSensorData> {
        return LightSensorData::class.java
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
                + " lux INTEGER) ")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (timestamp, formatted_date, sensor_name,lux)"
                + " values (?, ?, ?, ?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, timestamp)
        preparedStmt.setString(2, formattedDate)
        preparedStmt.setString(3, sensorName)
        preparedStmt.setInt(4, sensorData!!.lux!!)
    }

    @BsonIgnore
    override fun getFileName(): String {
        return FILE_NAME
    }

    @BsonIgnore
    override fun getFile(): File {
        return file!!
    }

    override fun setFile(file: File) {
        this.file = file
    }

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
}