package database;

import sensormodels.*;
import sensormodels.store.models.MySQLStoreModel;
import utils.WebAppConstants;

import java.sql.*;
import java.util.Date;
import java.util.*;


public class MySqlManager implements DbManager, DatabaseQueryRunner {
    private static MySqlManager instance;
    // JDBC driver name and database URL TODO: Not using this one in init()
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_NAME = "sensordata";
    static final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;

    private static final String SCREEN_USAGE_TABLE = "ScreenUsageSensorData";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "root";
    private static Connection connection;
    private static ArrayList<MySQLStoreModel> sensorModels;


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

    @Override
    public void init() {
        initSensorModels();
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
            createAllTables(stmt);
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
            }
        }
    }

    public <T extends MySQLStoreModel> void storeSensorDataList(List<T> sensorDataList) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            for (MySQLStoreModel sensorData :
                    sensorDataList) {
                preparedStmt = connection.prepareStatement(sensorData.getInsertIntoTableQuery());
                sensorData.setQueryData(preparedStmt);
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

    public <T extends MySQLStoreModel> void storeSensorData(T sensorData) {
        connection = getConnection();
        // create the mysql insert prepared statement
        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(sensorData.getInsertIntoTableQuery());
            sensorData.setQueryData(preparedStmt);
            // execute the prepared statement
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

    @Override
    public ArrayList<ActivFitSensorData> queryForRunningEvent(Date date) {
        return getRunningEventFromActivFitSensorData(date);
    }

    @Override
    public int queryForTotalStepsInDay(Date userDate) {
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

    @Override
    public int queryHeartRatesForDay(Date date) {
        return getHeartRateSensorDataForGivenDate(date).size();
    }

    private void initSensorModels() {
        sensorModels = new ArrayList<>();
        sensorModels.add(new ActivFitSensorData());
        sensorModels.add(new ActivitySensorData());
        sensorModels.add(new BatterySensorData());
        sensorModels.add(new BluetoothSensorData());
        sensorModels.add(new HeartRateSensorData());
        sensorModels.add(new LightSensorData());
        sensorModels.add(new ScreenUsageSensorData());
    }

    private void createAllTables(Statement stmt) throws SQLException {
        for (MySQLStoreModel sensorData :
                sensorModels) {
            stmt.executeUpdate(sensorData.getCreateTableQuery());
        }
    }

    /**
     * Call to get a reference to a connection with MySQL Server with the credentials
     *
     * @return reference to connection
     */
    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<ActivitySensorData> getActivitySensorDataForGivenDate(Date userDate) {
        connection = getConnection();
        String query = "SELECT * FROM " + ActivitySensorData.MY_SQL_TABLE_NAME + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(userDate)
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
    private ArrayList<ActivFitSensorData> getRunningEventFromActivFitSensorData(Date date) {
        connection = getConnection();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + ActivFitSensorData.MY_SQL_TABLE_NAME + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(date)
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

    private ArrayList<HeartRateSensorData> getHeartRateSensorDataForGivenDate(Date date) {
        connection = getConnection();
        ArrayList<HeartRateSensorData> results = new ArrayList<>();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + HeartRateSensorData.MY_SQL_TABLE_NAME + " WHERE formatted_date LIKE '" + WebAppConstants.inputDateFormat.format(date)
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

    private ArrayList<BatterySensorData> getBatterySensorData() {
        connection = getConnection();
        ArrayList<BatterySensorData> results = new ArrayList<>();
        // if you only need a few columns, specify them by name instead of using "*"
        String query = "SELECT * FROM " + BatterySensorData.MY_SQL_TABLE_NAME;

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
}
