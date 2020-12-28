package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.ScreenUsageSensorData
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class ScreenUsageSensorData : MongoStoreModel, LuceneStoreModel, FileStoreModel, MySqlStoreModel {
    @SerializedName("start_hour")
    @Expose
    var startHour: String? = null

    @SerializedName("end_hour")
    @Expose
    var endHour: String? = null

    @SerializedName("start_timestamp")
    @Expose
    var startTimestamp: String? = null

    @SerializedName("end_timestamp")
    @Expose
    var endTimestamp: String? = null

    @SerializedName("min_elapsed")
    @Expose
    var minElapsed: Double? = null

    @SerializedName("min_start_hour")
    @Expose
    var minStartHour: Double? = null

    @SerializedName("min_end_hour")
    @Expose
    var minEndHour: Int? = null

    @SerializedName("formatted_date")
    @Expose
    var formattedDate: String? = null
        private set

    @BsonIgnore
    private var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(startTimestamp))
    }

    override fun getStartTime(): String {
        return startTimestamp!!
    }

    @BsonIgnore
    override fun getMongoCollectionName(): String {
        return "ScreenUsageSensorData"
    }

    @BsonIgnore
    override fun getClassObject(): Class<ScreenUsageSensorData> {
        return ScreenUsageSensorData::class.java
    }

    @BsonIgnore
    override fun getTableName(): String {
        return MY_SQL_TABLE_NAME
    }

    @BsonIgnore
    override fun getCreateTableQuery(): String {
        return ("CREATE TABLE "
                + this.tableName
                + "(start_hour VARCHAR(40) , "
                + " end_hour VARCHAR(40),"
                + " start_timestamp VARCHAR(30),  "
                + " end_timestamp VARCHAR(30),  "
                + " formatted_date VARCHAR(10),  "
                + " min_elapsed DOUBLE , "
                + " min_start_hour DOUBLE , "
                + " min_end_hour INTEGER ) ")
    }

    @BsonIgnore
    override fun getInsertIntoTableQuery(): String {
        return (" insert into "
                + this.tableName
                + " (start_hour,end_hour,start_timestamp,end_timestamp, "
                + "formatted_date, min_elapsed,min_start_hour,min_end_hour)"
                + " values (?, ?, ?, ?, ?,?,?,?)")
    }

    @BsonIgnore
    @Throws(SQLException::class)
    override fun fillQueryData(preparedStmt: PreparedStatement) {
        preparedStmt.setString(1, startHour)
        preparedStmt.setString(2, endHour)
        preparedStmt.setString(3, startTimestamp)
        preparedStmt.setString(4, endTimestamp)
        preparedStmt.setString(5, formattedDate)
        preparedStmt.setDouble(6, minElapsed!!)
        preparedStmt.setDouble(7, minStartHour!!)
        preparedStmt.setInt(8, minEndHour!!)
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

    @BsonIgnore
    override fun getDocument(): Document {
        return Document()
    }

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "ScreenUsageSensorData"

        @BsonIgnore
        val FILE_NAME = "ScreenUsage"
    }
}