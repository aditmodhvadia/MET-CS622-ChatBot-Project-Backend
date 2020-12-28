package sensormodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.lucene.document.Document
import org.bson.codecs.pojo.annotations.BsonIgnore
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.LuceneStoreModel
import sensormodels.store.models.MongoStoreModel
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.io.File
import java.sql.PreparedStatement
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
    override val fileName: String = FILE_NAME

    @BsonIgnore
    override var file: File? = null
    override fun setFormattedDate() {
        formattedDate = WebAppConstants.inputDateFormat.format(Date(startTimestamp))
    }

    override val startTime: String? = startTimestamp

    companion object {
        @BsonIgnore
        val MY_SQL_TABLE_NAME = "ScreenUsageSensorData"

        @BsonIgnore
        val FILE_NAME = "ScreenUsage"
    }

    override val document: Document = Document()
    override val mongoCollectionName: String = "ScreenUsageSensorData"
    override val tableName: String = MY_SQL_TABLE_NAME
    override val createTableQuery: String = ("CREATE TABLE "
            + this.tableName
            + "(start_hour VARCHAR(40) , "
            + " end_hour VARCHAR(40),"
            + " start_timestamp VARCHAR(30),  "
            + " end_timestamp VARCHAR(30),  "
            + " formatted_date VARCHAR(10),  "
            + " min_elapsed DOUBLE , "
            + " min_start_hour DOUBLE , "
            + " min_end_hour INTEGER ) ")
    override val insertIntoTableQuery: String = (" insert into "
            + this.tableName
            + " (start_hour,end_hour,start_timestamp,end_timestamp, "
            + "formatted_date, min_elapsed,min_start_hour,min_end_hour)"
            + " values (?, ?, ?, ?, ?,?,?,?)")

    override fun fillQueryData(preparedStmt: PreparedStatement?) {
        preparedStmt?.apply {
            setString(1, startHour)
            setString(2, endHour)
            setString(3, startTimestamp)
            setString(4, endTimestamp)
            setString(5, formattedDate)
            setDouble(6, minElapsed!!)
            setDouble(7, minStartHour!!)
            setInt(8, minEndHour!!)
        }
    }
}