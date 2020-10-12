package database;

import sensormodels.*;
import utils.WebAppConstants;

import java.sql.*;
import java.util.Date;
import java.util.*;


public class MySqlManager {
    private static MySqlManager instance;
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_NAME = "sensordata";
    static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;

    private static final String ACTIV_FIT_TABLE = "ActivFitSensorData";
    private static final String ACTIVITY_TABLE = "ActivitySensorData";
    private static final String BATTERY_TABLE = "BatterySensorData";
    private static final String BLUETOOTH_TABLE = "BluetoothSensorData";
    private static final String HEART_RATE_TABLE = "HeartRateSensorData";
    private static final String LIGHT_TABLE = "LightSensorData";
    private static final String SCREEN_USAGE_TABLE = "ScreenUsageSensorData";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root";
    private static Connection connection;


    private MySqlManager() {
        init();
    }

    /**
     * Singleton method to get the instance of the class
     *
     * @return singleton instance of the class
     */
    public static MySqlManager getInstance() {
        if (instance == null) {
            instance = new MySqlManager();
        }
        return instance;
    }

    public static void init() {
        connection = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            connection = getConnection();

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = connection.createStatement();

            // executing statements to created SenorData database tables
            stmt.executeUpdate(MySQLQueries.createActivityTable);
            stmt.executeUpdate(MySQLQueries.createActivFitTable);
            stmt.executeUpdate(MySQLQueries.createBatteryTable);
            stmt.executeUpdate(MySQLQueries.createBluetoothTable);
            stmt.executeUpdate(MySQLQueries.createHeartRateTable);
            stmt.executeUpdate(MySQLQueries.createLightTable);
            stmt.executeUpdate(MySQLQueries.createScreenUsageTable);
            System.out.println("Created tables in given database...");
        } catch (Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException ignored) {
            }// nothing we can do
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }//end main

    /**
     * Call to insert given list data into ActivitySensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoActivityTable(List<ActivitySensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            for (ActivitySensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertIntoActivityTable);
                preparedStmt.setString(1, sensorData.getTimestamp());
                preparedStmt.setString(2, sensorData.getFormatted_date());
                preparedStmt.setString(3, sensorData.getSensorName());
                preparedStmt.setInt(4, sensorData.getSensorData().getStepCounts());
                preparedStmt.setInt(5, sensorData.getSensorData().getStepDelta());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to insert given data list into ActivFitSensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoActivFitTable(List<ActivFitSensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            for (ActivFitSensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertInActivFitTable);
                preparedStmt.setString(1, sensorData.getTimestamp().getStartTime());
                preparedStmt.setString(2, sensorData.getTimestamp().getEndTime());
                preparedStmt.setString(3, sensorData.getFormatted_date());
                preparedStmt.setInt(4, sensorData.getSensorData().getDuration());
                preparedStmt.setString(5, sensorData.getSensorData().getActivity());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to insert given data list into BatterySensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoBatteryTable(List<BatterySensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement\
        PreparedStatement preparedStmt;
        try {
            for (BatterySensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertInBatteryTable);
                preparedStmt.setString(1, sensorData.getTimestamp());
                preparedStmt.setString(2, sensorData.getTimestamp());
                preparedStmt.setString(3, sensorData.getFormatted_date());
                preparedStmt.setString(4, sensorData.getSensorName());
                preparedStmt.setInt(5, sensorData.getSensorData().getPercent());
                preparedStmt.setBoolean(6, sensorData.getSensorData().getCharging());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to insert given data list into BluetoothSensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoBluetoothTable(List<BluetoothSensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt = null;
        try {
            for (BluetoothSensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertInBluetoothTable);
                preparedStmt.setString(1, sensorData.getTimestamp());
                preparedStmt.setString(2, sensorData.getFormatted_date());
                preparedStmt.setString(3, sensorData.getSensorName());
                preparedStmt.setString(4, sensorData.getSensorData().getState());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to insert given data list into HeartRateSensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoHeartRateTable(List<HeartRateSensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            for (HeartRateSensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertInHeartRateTable);
                preparedStmt.setString(1, sensorData.getTimestamp());
                preparedStmt.setString(2, sensorData.getFormatted_date());
                preparedStmt.setString(3, sensorData.getSensorName());
                preparedStmt.setInt(4, sensorData.getSensorData().getBpm());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to insert given data list into LightSensorData table in MySQL
     *
     * @param sensorDataList given data list
     */
    public void insertIntoLightTable(List<LightSensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt = null;
        try {
            for (LightSensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertIntoLightTable);
                preparedStmt.setString(1, sensorData.getTimestamp());
                preparedStmt.setString(2, sensorData.getFormatted_date());
                preparedStmt.setString(3, sensorData.getSensorName());
                preparedStmt.setInt(4, sensorData.getSensorData().getLux());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertIntoScreenUsageTable(List<ScreenUsageSensorData> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            for (ScreenUsageSensorData sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(MySQLQueries.insertInScreenUsageTable);
                preparedStmt.setString(1, sensorData.getStartHour());
                preparedStmt.setString(2, sensorData.getEndHour());
                preparedStmt.setString(3, sensorData.getStartTimestamp());
                preparedStmt.setString(4, sensorData.getEndTimestamp());
                preparedStmt.setString(5, sensorData.getFormatted_date());
                preparedStmt.setDouble(6, sensorData.getMinElapsed());
                preparedStmt.setDouble(7, sensorData.getMinStartHour());
                preparedStmt.setInt(8, sensorData.getMinEndHour());
                // execute the prepared statement
                preparedStmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call to get a reference to a connection with MySQL Server with the credentials
     *
     * @return reference to connection
     */
    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<ActivFitSensorData> queryForRunningEvent(Date date) {
        ArrayList<ActivFitSensorData> queryResult = getRunningEventFromActivFitSensorData(date);
        return queryResult;
    }

    /**
     * Called to query the number of steps user takes for the given day
     *
     * @param userDate given day
     * @return step count for the given day
     */
    public static int queryForTotalStepsInDay(Date userDate) {
//        fetch all documents from the collection
        ArrayList<ActivitySensorData> sensorDataList = getActivitySensorDataForGivenDate(userDate);
        int maxStepCount = -1;    // Max value of step count for the day
        for (ActivitySensorData sensorData : sensorDataList) {
            if (sensorData.getSensorData().getStepCounts() > maxStepCount) {
                maxStepCount = sensorData.getSensorData().getStepCounts();
            }
        }
        return maxStepCount;
    }

    private static ArrayList<ActivitySensorData> getActivitySensorDataForGivenDate(Date userDate) {
        connection = getConnection();
        String query = "SELECT * FROM " + ACTIVITY_TABLE + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(userDate)
                + "' ORDER BY step_counts DESC LIMIT 1";
        ArrayList<ActivitySensorData> resultSet = new ArrayList<>();
        // create the java statement
        Statement st;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                ActivitySensorData data = new ActivitySensorData();
                data.setTimestamp(rs.getString("time_stamp"));
                data.setTime_stamp(rs.getString("time_stamp"));
                data.setSensorName(rs.getString("sensor_name"));
                ActivitySensorData.SensorData sensorData = new ActivitySensorData.SensorData();
                sensorData.setStepCounts(rs.getInt("step_counts"));
                sensorData.setStepDelta(rs.getInt("step_delta"));
                data.setSensorData(sensorData);
                data.setFormattedDate();
                resultSet.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // execute the query, and get a java resultset
        return resultSet;
    }

    /**
     * Call to search for running event for the given date in the activ fit sensor data
     *
     * @param date given date
     * @return all instances of running event recorded for the given date
     */
    private static ArrayList<ActivFitSensorData> getRunningEventFromActivFitSensorData(Date date) {
        connection = getConnection();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + ACTIV_FIT_TABLE + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(date)
                + "' AND activity LIKE " + "'running'";
        ArrayList<ActivFitSensorData> resultSet = new ArrayList<>();
        // create the java statement
        Statement st;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                ActivFitSensorData data = new ActivFitSensorData();
                ActivFitSensorData.Timestamp timeStamp = new ActivFitSensorData.Timestamp();
                timeStamp.setStartTime(rs.getString("start_time"));
                timeStamp.setEndTime(rs.getString("end_time"));
                data.setTimestamp(timeStamp);
                ActivFitSensorData.SensorData sensorData = new ActivFitSensorData.SensorData();
                sensorData.setActivity(rs.getString("activity"));
                sensorData.setDuration(rs.getInt("duration"));
                data.setSensorData(sensorData);
                data.setFormattedDate();
                resultSet.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // execute the query, and get a java result set
        return resultSet;
    }

    private static ArrayList<HeartRateSensorData> getHeartRateSensorDataForGivenDate(Date date) {
        connection = getConnection();
        ArrayList<HeartRateSensorData> results = new ArrayList<>();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + HEART_RATE_TABLE + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(date)
                + "'";

        // create the java statement
        Statement st = null;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
//                HeartRateSensorData data = new HeartRateSensorData();
                HeartRateSensorData heartRateSensorData = new HeartRateSensorData();
                heartRateSensorData.setTimestamp(rs.getString("timestamp"));
                heartRateSensorData.setSensorName(rs.getString("sensor_name"));
                HeartRateSensorData.SensorData sensorData = new HeartRateSensorData.SensorData();
                //sensorData.s(rs.getString("sensor_name"));
                sensorData.setBpm(rs.getInt("bpm"));
                heartRateSensorData.setSensorData(sensorData);
                heartRateSensorData.setFormattedDate();
                results.add(heartRateSensorData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // execute the query, and get a java resultset
        return results;
    }

    private static ArrayList<BatterySensorData> getBatterySensorData() {
        connection = getConnection();
        ArrayList<BatterySensorData> results = new ArrayList<>();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + BATTERY_TABLE;

        // create the java statement
        Statement st = null;
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                BatterySensorData batterySensor = new BatterySensorData();
                batterySensor.setSensorName(rs.getString("sensor_name"));
                batterySensor.setTimestamp(rs.getString("timestamp"));
                batterySensor.setSensorName(rs.getString("sensor_data"));
                BatterySensorData.SensorData sensorData = new BatterySensorData.SensorData();
                sensorData.setPercent(rs.getInt("percent"));
                sensorData.setCharging(rs.getBoolean("duration"));
                batterySensor.setSensorData(sensorData);
                results.add(batterySensor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // execute the query, and get a java resultset
        return results;
    }

    /**
     * Use to add given number of days to the given Date
     *
     * @param userDate given Date
     * @param days     given number of days
     * @return Date after adding given number of days
     */
    private static Date addDayToDate(Date userDate, int days) {
        Calendar cal = Calendar.getInstance();  // get Calendar Instance
        cal.setTime(userDate);  // set Time to the given Date@param
        cal.add(Calendar.DATE, days);   // add given number of days@param to the given Date@param
        return cal.getTime();   // return the new Date
    }

    /**
     * Call to get the number of heart rate notifications received for the given date
     *
     * @param date given date
     * @return count of heart rate notifications received for the given date
     */
    public static int queryHeartRatesForDay(Date date) {
        ArrayList<HeartRateSensorData> sensorDataList = getHeartRateSensorDataForGivenDate(date);
        return sensorDataList.size();
    }

    private static class MySQLQueries {
        static final String createActivityTable = "CREATE TABLE " + ACTIVITY_TABLE +
                "(time_stamp VARCHAR(30) , " +
                " sensor_name CHAR(25) , " +
                " formatted_date CHAR(10) , " +
                " step_counts INTEGER, " +
                " step_delta INTEGER)";

        static final String createActivFitTable = "CREATE TABLE " + ACTIV_FIT_TABLE +
                " (start_time VARCHAR(30) , " +
                " formatted_date VARCHAR(10) , " +
                " end_time VARCHAR(30) , " +
                " duration INTEGER , " +
                " activity VARCHAR(55) ) ";

        static final String createBatteryTable = "CREATE TABLE " + BATTERY_TABLE +
                "(timestamp VARCHAR(30) , " +
                " time_stamp VARCHAR(30) , " +
                " formatted_date VARCHAR(10) , " +
                " sensor_name CHAR (25), " +
                " percent INTEGER , " +
                " charging BIT ) ";

        static final String createBluetoothTable = "CREATE TABLE " + BLUETOOTH_TABLE +
                "(timestamp VARCHAR(30) , " +
                " formatted_date VARCHAR(10) , " +
                " sensor_name VARCHAR(30) , " +
                " state CHAR (225)) ";

        static final String createHeartRateTable = "CREATE TABLE " + HEART_RATE_TABLE +
                "(timestamp VARCHAR(30) , " +
                " formatted_date VARCHAR(10) , " +
                " sensor_name CHAR (25), " +
                " bpm INTEGER)";

        static final String createLightTable = "CREATE TABLE " + LIGHT_TABLE +
                "(timestamp VARCHAR(30) , " +
                " formatted_date VARCHAR(10) , " +
                " sensor_name VARCHAR(30) , " +
                " lux INTEGER) ";

        static final String createScreenUsageTable = "CREATE TABLE " + SCREEN_USAGE_TABLE +
                "(start_hour VARCHAR(40) , " +
                " end_hour VARCHAR(40)," +
                " start_timestamp VARCHAR(30),  " +
                " end_timestamp VARCHAR(30),  " +
                " formatted_date VARCHAR(10),  " +
                " min_elapsed DOUBLE , " +
                " min_start_hour DOUBLE , " +
                " min_end_hour INTEGER ) ";

        static final String insertIntoActivityTable = " insert into " + ACTIVITY_TABLE + " (time_stamp,formatted_date, sensor_name, step_counts,step_delta)"
                + " values (?, ?, ?, ?, ?)";
        static final String insertInActivFitTable = " insert into " + ACTIV_FIT_TABLE + " (start_time, end_time, formatted_date, duration, activity)"
                + " values (?, ?, ?, ?, ?)";
        static final String insertInBatteryTable = " insert into " + BATTERY_TABLE + " (timestamp,time_stamp, formatted_date, sensor_name,percent,charging)"
                + " values (?, ?, ?, ?, ?,?)";
        static final String insertInBluetoothTable = " insert into " + BLUETOOTH_TABLE + " (timestamp,formatted_date,sensor_name,state)"
                + " values (?, ?, ?, ?)";
        static final String insertInHeartRateTable = " insert into " + HEART_RATE_TABLE + " (timestamp, formatted_date, sensor_name,bpm)"
                + " values (?, ?, ?, ?)";
        static final String insertIntoLightTable = " insert into " + LIGHT_TABLE + " (timestamp, formatted_date, sensor_name,lux)"
                + " values (?, ?, ?, ?)";
        static final String insertInScreenUsageTable = " insert into " + SCREEN_USAGE_TABLE + " (start_hour,end_hour,start_timestamp,end_timestamp, formatted_date, min_elapsed,min_start_hour,min_end_hour)"
                + " values (?, ?, ?, ?, ?,?,?,?)";
    }
}
