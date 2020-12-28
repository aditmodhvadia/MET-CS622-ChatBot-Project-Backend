package database

import sensormodels.BluetoothSensorData
import sensormodels.HeartRateSensorData
import sensormodels.LightSensorData
import sensormodels.ScreenUsageSensorData
import sensormodels.activfit.ActivFitSensorData
import sensormodels.activfit.ActivFitSensorDataBuilder
import sensormodels.activity.ActivitySensorData
import sensormodels.activity.ActivitySensorDataBuilder
import sensormodels.battery.BatterySensorData
import sensormodels.battery.BatterySensorDataBuilder
import sensormodels.store.models.MySqlStoreModel
import utils.WebAppConstants
import java.sql.*
import java.util.*
import java.util.Date
import java.util.function.Consumer
import javax.annotation.Nonnull
import javax.servlet.ServletContext

class MySqlManager private constructor() : DbManager<MySqlStoreModel?>, DatabaseQueryRunner {
    override fun init(servletContext: ServletContext?) {
        initSensorModels()
        var stmt: Statement? = null
        try {
            // STEP 2: Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver")

            // STEP 3: Open a connection
            println("Connecting to database...")

            // STEP 4: Execute a query
            println("Creating tables...")
            stmt = connection!!.createStatement()

            // executing statements to created SenorData database tables
            createAllTables(stmt)
            println("Created tables in given database...")
        } catch (se: Exception) {
            // Handle errors for JDBC
            se.printStackTrace()
            // Handle errors for Class.forName
        } finally {
            // finally block used to close resources
            try {
                stmt?.close()
            } catch (ignored: SQLException) {
                // nothing we can do
            }
            try {
                connection?.close()
            } catch (se: SQLException) {
                se.printStackTrace()
            }
        }
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<MySqlStoreModel?>) {
        //    sensorDataList.forEach(this::insertSensorData);
        try {
            sensorDataList.forEach { sensorData: MySqlStoreModel? ->
                try {
                    val preparedStmt = connection!!.prepareStatement(sensorData!!.insertIntoTableQuery)
                    sensorData.fillQueryData(preparedStmt)
                    preparedStmt.execute()
                } catch (exception: SQLException) {
                    exception.printStackTrace()
                }
            }
        } finally {
            println("MySQL Log: Data Inserted for " + sensorDataList[0]!!.tableName)
            try {
                connection?.close()
            } catch (exception: SQLException) {
                exception.printStackTrace()
            }
        }
    }

    override fun insertSensorData(sensorData: MySqlStoreModel?) {
        // create the mysql insert prepared statement
        val preparedStmt: PreparedStatement
        try {
            preparedStmt = connection!!.prepareStatement(sensorData!!.insertIntoTableQuery)
            sensorData.fillQueryData(preparedStmt)
            // execute the prepared statement
            preparedStmt.execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                if (connection != null) {
                    connection!!.close()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    override fun queryForRunningEvent(date: Date?): ArrayList<ActivFitSensorData?>? {
        return getRunningEventFromActivFitSensorData(date)
    }

    override fun queryForTotalStepsInDay(date: Date?): Int {
        return try {
            val maxSensorData = getActivitySensorDataForGivenDate(date).stream()
                .max(
                    Comparator.comparingInt { sensorModel: ActivitySensorData -> sensorModel.sensorData.stepCounts })
                .get()
            maxSensorData.sensorData.stepCounts
        } catch (exception: NoSuchElementException) {
            0
        }
    }

    override fun queryHeartRatesForDay(date: Date?): Int {
        return getHeartRateSensorDataForGivenDate(date).size
    }

    private fun initSensorModels() {
        sensorModels = ArrayList()
        sensorModels!!.add(ActivFitSensorData())
        sensorModels!!.add(ActivitySensorData())
        sensorModels!!.add(BatterySensorData())
        sensorModels!!.add(BluetoothSensorData())
        sensorModels!!.add(HeartRateSensorData())
        sensorModels!!.add(LightSensorData())
        sensorModels!!.add(ScreenUsageSensorData())
    }

    @Throws(SQLException::class)
    private fun createAllTables(stmt: Statement?) {
        sensorModels!!.forEach(
            Consumer { sensorModel: MySqlStoreModel ->
                try {
                    stmt!!.executeUpdate(sensorModel.createTableQuery)
                } catch (exception: SQLException) {
                    println("Table " + sensorModel.tableName + " already exists")
                }
            })
    }

    /**
     * Call to get a reference to a connection with MySQL Server with the credentials.
     *
     * @return reference to connection
     */
    private val connection: Connection?
        get() {
            try {
                return DriverManager.getConnection(DB_URL, USER, PASS)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    private fun getActivitySensorDataForGivenDate(userDate: Date?): ArrayList<ActivitySensorData> {
        val query = ("SELECT * FROM "
                + ActivitySensorData.MY_SQL_TABLE_NAME
                + " WHERE formatted_date LIKE '"
                + WebAppConstants.inputDateFormat.format(userDate)
                + "' ORDER BY step_counts DESC LIMIT 1")
        val resultSet = ArrayList<ActivitySensorData>()
        // create the java statement
        val st: Statement
        try {
            st = connection!!.createStatement()
            val rs = st.executeQuery(query)
            while (rs.next()) {
                val data = ActivitySensorDataBuilder()
                    .setTimeStamp(rs.getString("time_stamp"))
                    .setTimestamp(rs.getString("time_stamp"))
                    .setSensorName(rs.getString("sensor_name"))
                    .setStepCounts(rs.getInt("step_counts"))
                    .setStepDelta(rs.getInt("step_delta"))
                    .build()
                resultSet.add(data)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            closeConnection(connection)
        }

        // execute the query, and get a java resultset
        return resultSet
    }

    /**
     * Call to search for running event for the given date in the activfit sensor data.
     *
     * @param date given date
     * @return all instances of running event recorded for the given date
     */
    private fun getRunningEventFromActivFitSensorData(date: Date?): ArrayList<ActivFitSensorData?> {
        // if you only need a few columns, specify them by name instead of using "*"
        val query = ("SELECT * FROM "
                + ActivFitSensorData.MY_SQL_TABLE_NAME
                + " WHERE formatted_date LIKE '"
                + WebAppConstants.inputDateFormat.format(date)
                + "' AND activity LIKE "
                + "'running'")
        val resultSet = ArrayList<ActivFitSensorData?>()
        // create the java statement
        val st: Statement
        try {
            st = connection!!.createStatement()
            val rs = st.executeQuery(query)
            while (rs.next()) {
                resultSet.add(getActivFitSensorDataFromResultSet(rs))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            closeConnection(connection)
        }

        // execute the query, and get a java result set
        return resultSet
    }

    @Throws(SQLException::class)
    private fun getActivFitSensorDataFromResultSet(rs: ResultSet): ActivFitSensorData {
        return ActivFitSensorDataBuilder()
            .setStartTime(rs.getString("start_time"))
            .setEndTime(rs.getString("end_time"))
            .setActivity(rs.getString("activity"))
            .setDuration(rs.getInt("duration"))
            .build()
    }

    /**
     * Close database connection
     *
     * @param connection database connection
     */
    private fun closeConnection(connection: Connection?) {
        try {
            connection!!.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun getHeartRateSensorDataForGivenDate(date: Date?): ArrayList<HeartRateSensorData> {
        val results = ArrayList<HeartRateSensorData>()
        val query = ("SELECT * FROM "
                + HeartRateSensorData.MY_SQL_TABLE_NAME
                + " WHERE formatted_date LIKE '"
                + WebAppConstants.inputDateFormat.format(date)
                + "'")

        // create the java statement
        val st: Statement
        try {
            st = connection!!.createStatement()
            val rs = st.executeQuery(query)
            while (rs.next()) {
                //                HeartRateSensorData data = new HeartRateSensorData();
                val heartRateSensorData = HeartRateSensorData()
                heartRateSensorData.timestamp = rs.getString("timestamp")
                heartRateSensorData.sensorName = rs.getString("sensor_name")
                val sensorData = HeartRateSensorData.SensorData()
                // sensorData.s(rs.getString("sensor_name"));
                sensorData.bpm = rs.getInt("bpm")
                heartRateSensorData.sensorData = sensorData
                heartRateSensorData.setFormattedDate()
                results.add(heartRateSensorData)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            closeConnection(connection)
        }

        // execute the query, and get a java resultset
        return results
    }// duration if charging doesn't work

    // execute the query, and get a java resultset
    // if you only need a few columns, specify them by name instead of using "*"
    private val batterySensorData:

    // create the java statement
            ArrayList<BatterySensorData>
        private get() {
            val results = ArrayList<BatterySensorData>()
            // if you only need a few columns, specify them by name instead of using "*"
            val query = "SELECT * FROM " + BatterySensorData.MY_SQL_TABLE_NAME

            // create the java statement
            var st: Statement? = null
            try {
                st = connection!!.createStatement()
                val rs = st.executeQuery(query)
                while (rs.next()) {
                    val batterySensor = BatterySensorDataBuilder()
                        .setSensorName(rs.getString("sensor_name"))
                        .setTimeStamp(rs.getString("timestamp"))
                        .setPercent(rs.getInt("percent"))
                        .setCharging(rs.getBoolean("charging")) // duration if charging doesn't work
                        .build()
                    results.add(batterySensor)
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                closeConnection(connection)
            }

            // execute the query, and get a java resultset
            return results
        }

    companion object {
        // JDBC driver name and database URL TODO: Not using this one in init()
        const val JDBC_DRIVER = "com.mysql.jdbc.Driver"
        const val DB_NAME = "sensordata"
        const val DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME

        //  Database credentials
        const val USER = "admin"
        const val PASS = "admin"

        /**
         * Singleton method to get the instance of the class.
         *
         * @return singleton instance of the class
         */
        @JvmStatic
        var instance: MySqlManager? = null
            get() {
                if (field == null) {
                    field = MySqlManager()
                }
                return field
            }
            private set
        private var sensorModels: ArrayList<MySqlStoreModel>? = null
    }

    init {
        init(null)
    }
}