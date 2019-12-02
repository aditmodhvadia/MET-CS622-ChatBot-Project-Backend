package database;

import sensormodels.*;

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
            connection = getConnection();

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = connection.createStatement();

            String sql = "CREATE TABLE " + ACTIVITY_TABLE +
                    "(time_stamp VARCHAR(30) , " +
                    " sensor_name CHAR(25) , " +
                    " step_counts INTEGER, " +
                    " step_delta INTEGER)";

            // creating ActivFitSensorData table
//            deleted timestamp column as it is not required
            String sql2 = "CREATE TABLE " + ACTIV_FIT_TABLE +
                    " (start_time VARCHAR(30) , " +    //  replace all TIMESTAMP with VARCHAR and store as string
                    " end_time VARCHAR(30) , " +
                    " duration INTEGER , " +
                    " activity VARCHAR(55) ) ";

            //" PRIMARY KEY ( timestamp))";
            // creating BatterySensorData table
            String sql3 = "CREATE TABLE " + BATTERY_TABLE +
                    "(timestamp VARCHAR(30) , " +
                    " time_stamp VARCHAR(30) , " +
                    " sensor_name CHAR (25), " +
                    " percent INTEGER , " +
                    " charging BIT ) ";
            //" PRIMARY KEY ( timestamp))";

            // creating BluetoothSensorData table
            String sql4 = "CREATE TABLE " + BLUETOOTH_TABLE +
                    "(timestamp VARCHAR(30) , " +
                    " sensor_name VARCHAR(30) , " +
                    " state CHAR (225)) ";

            // creating HeartRateSensorData table
            String sql5 = "CREATE TABLE " + HEART_RATE_TABLE +
                    "(timestamp VARCHAR(30) , " +
                    " sensor_name CHAR (25), " +
                    " bpm INTEGER)";
            // creating LightSensorData table
            String sql6 = "CREATE TABLE " + LIGHT_TABLE +
                    "(timestamp VARCHAR(30) , " +
                    " sensor_name VARCHAR(30) , " +
                    " lux INTEGER) ";
            // creating ScreenUsageSensorData
            String sql7 = "CREATE TABLE " + SCREEN_USAGE_TABLE +
                    "(start_hour VARCHAR(40) , " +
                    " end_hour VARCHAR(40)," +
                    " start_timestamp VARCHAR(30),  " +
                    " end_timestamp VARCHAR(30),  " +
                    " min_elapsed DOUBLE , " +
                    " min_start_hour DOUBLE , " +
                    " min_end_hour INTEGER ) ";

            // executing statements to created SenorData database tables
//            stmt.executeUpdate(sql);
//            stmt.executeUpdate(sql2);
//            stmt.executeUpdate(sql3);
//            stmt.executeUpdate(sql4);
//            stmt.executeUpdate(sql5);
//            stmt.executeUpdate(sql6);
//            stmt.executeUpdate(sql7);
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
        connection = getConnection();
        // the mysql insert statement
        String sql1 = " insert into " + ACTIVITY_TABLE + " (time_stamp, sensor_name, step_counts,step_delta)"
                + " values (?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql1);
            preparedStmt.setString(1, sensorData.getTimestamp());
            preparedStmt.setString(2, sensorData.getSensorName());
            preparedStmt.setInt(3, sensorData.getSensorData().getStepCounts());
            preparedStmt.setInt(4, sensorData.getSensorData().getStepDelta());

            // execute the preparedstatement
            preparedStmt.execute();

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
     * Call to insert given data into ActivFitSensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoActivFitTable(ActivFitSensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql2 = " insert into " + ACTIV_FIT_TABLE + " (start_time, end_time, duration, activity)"
                + " values (?, ?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql2);
            preparedStmt.setString(1, sensorData.getTimestamp().getStartTime());
            preparedStmt.setString(2, sensorData.getTimestamp().getEndTime());
            preparedStmt.setInt(3, sensorData.getSensorData().getDuration());
            preparedStmt.setString(4, sensorData.getSensorData().getActivity());

            // execute the preparedstatement
            preparedStmt.execute();

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
     * Call to insert given data into BatterySensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoBatteryTable(BatterySensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql3 = " insert into " + BATTERY_TABLE + " (timestamp,time_stamp,sensor_name,percent,charging)"
                + " values (?, ?, ?, ?,?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql3);
            preparedStmt.setString(1, sensorData.getTimestamp());
            preparedStmt.setString(2, sensorData.getTimestamp());
            preparedStmt.setString(3, sensorData.getSensorName());
            preparedStmt.setInt(4, sensorData.getSensorData().getPercent());
            preparedStmt.setBoolean(5, sensorData.getSensorData().getCharging());

            // execute the preparedstatement
            preparedStmt.execute();

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
     * Call to insert given data into BluetoothSensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoBluetoothTable(BluetoothSensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql4 = " insert into " + BLUETOOTH_TABLE + " (timestamp,sensor_name,state)"
                + " values (?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql4);
            preparedStmt.setString(1, sensorData.getTimestamp());
            preparedStmt.setString(2, sensorData.getSensorName());
            preparedStmt.setString(3, sensorData.getSensorData().getState());

            // execute the preparedstatement
            preparedStmt.execute();

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
     * Call to insert given data into HeartRateSensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoHeartRateTable(HeartRateSensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql5 = " insert into " + HEART_RATE_TABLE + " (timestamp,sensor_name,bpm)"
                + " values (?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql5);
            preparedStmt.setString(1, sensorData.getTimestamp());
            preparedStmt.setString(2, sensorData.getSensorName());
            preparedStmt.setInt(3, sensorData.getSensorData().getBpm());

            // execute the preparedstatement
            preparedStmt.execute();

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
     * Call to insert given data into LightSensorData table in MySQL
     *
     * @param sensorData given data
     */
    public static void insertIntoLightTable(LightSensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql6 = " insert into " + LIGHT_TABLE + " (timestamp,sensor_name,lux)"
                + " values (?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql6);
            preparedStmt.setString(1, sensorData.getTimestamp());
            preparedStmt.setString(2, sensorData.getSensorName());
            preparedStmt.setInt(3, sensorData.getSensorData().getLux());

            // execute the preparedstatement
            preparedStmt.execute();

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

    public static void insertIntoScreenUsageTable(ScreenUsageSensorData sensorData) {
        connection = getConnection();
        // the mysql insert statement
        String sql7 = " insert into " + SCREEN_USAGE_TABLE + " (start_hour,end_hour,start_timestamp,end_timestamp,min_elapsed,min_start_hour,min_end_hour)"
                + " values (?, ?, ?, ?,?,?,?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = connection.prepareStatement(sql7);
            preparedStmt.setString(1, sensorData.getStartHour());
            preparedStmt.setString(2, sensorData.getEndHour());
            preparedStmt.setString(3, sensorData.getStartTimestamp());
            preparedStmt.setString(4, sensorData.getEndTimestamp());
            preparedStmt.setDouble(5, sensorData.getMinElapsed());
            preparedStmt.setDouble(6, sensorData.getMinStartHour());
            preparedStmt.setInt(7, sensorData.getMinEndHour());

            // execute the preparedstatement
            preparedStmt.execute();

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
}
