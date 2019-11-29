package database;

import sensormodels.ActivFitSensorData;
import sensormodels.ActivitySensorData;

import java.sql.*;

public class MySqlManager {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/sensordata";

    private static final String ACTIV_FIT_TABLE = "ActivFitSensorData";
    private static final String ACTIVITY_TABLE = "ActivitySensorData";
    private static final String BATTERY_TABLE = "BatterySensorData";
    private static final String BLUETOOTH_TABLE = "BluetoothSensorData";
    private static final String HEART_RATE_TABLE = "HeartRateSensorData";
    private static final String LIGHT_TABLE = "LightSensorData";
    private static final String SCREEN_USAGE_TABLE = "ScreenUsageSensorData";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "Superj35@";
    private static Connection connection;

    public static void init() {
        connection = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = connection.createStatement();

            String sql = "CREATE TABLE " + ACTIVITY_TABLE +
                    "(time_stamp TIMESTAMP , " +
                    " sensor_name CHAR(25) , " +
                    " step_counts INTEGER, " +
                    " step_delta INTEGER, " +
                    " PRIMARY KEY ( time_stamp))";
            // creating ActivFitSensorData table
//            deleted timestamp column as it is not required
            String sql2 = "CREATE TABLE " + ACTIV_FIT_TABLE +
                    " start_time TIMESTAMP , " +
                    " end_time TIMESTAMP , " +
                    " duration INTEGER, " +
                    " activity VARCHAR (255)) ";

            //" PRIMARY KEY ( timestamp))";
            // creating BatterySensorData table
            String sql3 = "CREATE TABLE " + BATTERY_TABLE +
                    "(timestamp TIME , " +
                    " time_stamp TIMESTAMP , " +
                    " sensor_data CHAR (5), " +
                    " percent INTEGER , " +
                    " charging BIT ) ";
            //" PRIMARY KEY ( timestamp))";

            // creating BluetoothSensorData table
            String sql4 = "CREATE TABLE " + BLUETOOTH_TABLE +
                    "(timestamp TIME , " +
                    " time_stamp TIME , " +
                    " sensor_data CHAR (5), " +
                    " state CHAR (225)) ";

            // creating HeartRateSensorData table
            String sql5 = "CREATE TABLE " + HEART_RATE_TABLE +
                    "(timestamp TIME , " +
                    " sensor_data CHAR (5), " +
                    " bpm INTEGER)";
            // creating LightSensorData table
            String sql6 = "CREATE TABLE " + LIGHT_TABLE +
                    "(timestamp TIME , " +
                    " time_stamp TIME , " +
                    " sensor_data CHAR (5), " +
                    " lux INTEGER) ";
            // creating ScreenUsageSensorData
            String sql7 = "CREATE TABLE " + SCREEN_USAGE_TABLE +
                    "(start_hour INTEGER , " +
                    " end_hour INTEGER," +
                    " start_timestamp TIME,  " +
                    " end_timestamp TIME,  " +
                    " min_elapsed DOUBLE , " +
                    " min_start_hour DOUBLE , " +
                    " min_end_hour DOUBLE ) ";

            // executing statements to created SenorData database tables
            stmt.executeUpdate(sql);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
            stmt.executeUpdate(sql4);
            stmt.executeUpdate(sql5);
            stmt.executeUpdate(sql6);
            stmt.executeUpdate(sql7);
            System.out.println("Created tables in given database...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
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
        System.out.println("Goodbye!");
    }//end main

    /**
     * Call to insert given data into ActivitySensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoActivityTable(ActivitySensorData sensorData) {
        // the mysql insert statement
        String sql1 = " insert into " + ACTIV_FIT_TABLE + " (time_stamp, sensor_data, step_counts,step_delta,)"
                + " values (?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql1);
            preparedStmt.setTimestamp(1, Timestamp.valueOf(sensorData.getTimestamp()));
            preparedStmt.setString(2, sensorData.getSensorName());
            preparedStmt.setInt(3, sensorData.getSensorData().getStepCounts());
            preparedStmt.setInt(4, sensorData.getSensorData().getStepDelta());

            // execute the preparedstatement
            preparedStmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
