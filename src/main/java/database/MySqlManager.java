package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import sensormodels.BluetoothSensorData;
import sensormodels.HeartRateSensorData;
import sensormodels.LightSensorData;
import sensormodels.ScreenUsageSensorData;
import sensormodels.activfit.ActivFitSensorData;
import sensormodels.activfit.ActivFitSensorDataBuilder;
import sensormodels.activity.ActivitySensorData;
import sensormodels.activity.ActivitySensorDataBuilder;
import sensormodels.battery.BatterySensorData;
import sensormodels.battery.BatterySensorDataBuilder;
import sensormodels.store.models.MySqlStoreModel;
import utils.WebAppConstants;

public class MySqlManager implements DbManager<MySqlStoreModel>, DatabaseQueryRunner {
  private static MySqlManager instance;
  // JDBC driver name and database URL TODO: Not using this one in init()
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  static final String DB_NAME = "sensordata";
  static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;

  //  Database credentials
  static final String USER = "admin";
  static final String PASS = "admin";
  private static Connection connection;
  private static ArrayList<MySqlStoreModel> sensorModels;

  private MySqlManager() {
    init(null);
  }

  /**
   * Singleton method to get the instance of the class.
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
  public void init(@Nullable ServletContext servletContext) {
    initSensorModels();
    connection = null;
    Statement stmt = null;
    try {
      // STEP 2: Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");

      // STEP 3: Open a connection
      System.out.println("Connecting to database...");
      connection = getConnection();

      // STEP 4: Execute a query
      System.out.println("Creating tables...");
      stmt = connection.createStatement();

      // executing statements to created SenorData database tables
      createAllTables(stmt);
      System.out.println("Created tables in given database...");

    } catch (Exception se) {
      // Handle errors for JDBC
      se.printStackTrace();
      // Handle errors for Class.forName
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException ignored) {
        // nothing we can do
      }
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  @Override
  public <V extends MySqlStoreModel> void insertSensorDataList(
      @Nonnull List<? extends V> sensorDataList) {
    //    sensorDataList.forEach(this::insertSensorData);
    connection = getConnection();
    try {
      sensorDataList.forEach(
          sensorData -> {
            try {
              PreparedStatement preparedStmt =
                  connection.prepareStatement(sensorData.getInsertIntoTableQuery());
              sensorData.fillQueryData(preparedStmt);
              preparedStmt.execute();
            } catch (SQLException exception) {
              exception.printStackTrace();
            }
          });
    } finally {
      System.out.println("MySQL Log: Data Inserted for " + sensorDataList.get(0).getTableName());
      try {
        connection.close();
      } catch (SQLException exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public <V extends MySqlStoreModel> void insertSensorData(V sensorData) {
    connection = getConnection();
    // create the mysql insert prepared statement
    PreparedStatement preparedStmt;
    try {
      preparedStmt = connection.prepareStatement(sensorData.getInsertIntoTableQuery());
      sensorData.fillQueryData(preparedStmt);
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
    try {
      ActivitySensorData maxSensorData =
          getActivitySensorDataForGivenDate(userDate).stream()
              .max(
                  Comparator.comparingInt(
                      sensorModel -> sensorModel.getSensorData().getStepCounts()))
              .get();
      return maxSensorData.getSensorData().getStepCounts();
    } catch (NoSuchElementException exception) {
      return 0;
    }
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
    sensorModels.forEach(
        sensorModel -> {
          try {
            stmt.executeUpdate(sensorModel.getCreateTableQuery());
          } catch (SQLException exception) {
            System.out.println("Table " + sensorModel.getTableName() + " already exists");
          }
        });
  }

  /**
   * Call to get a reference to a connection with MySQL Server with the credentials.
   *
   * @return reference to connection
   */
  private Connection getConnection() {
    try {
      return DriverManager.getConnection(DB_URL, USER, PASS);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private ArrayList<ActivitySensorData> getActivitySensorDataForGivenDate(Date userDate) {
    connection = getConnection();
    String query =
        "SELECT * FROM "
            + ActivitySensorData.MY_SQL_TABLE_NAME
            + " WHERE formatted_date LIKE '"
            + WebAppConstants.inputDateFormat.format(userDate)
            + "' ORDER BY step_counts DESC LIMIT 1";
    ArrayList<ActivitySensorData> resultSet = new ArrayList<>();
    // create the java statement
    Statement st;
    try {
      st = connection.createStatement();
      ResultSet rs = st.executeQuery(query);

      while (rs.next()) {
        ActivitySensorData data =
            new ActivitySensorDataBuilder()
                .setTimeStamp(rs.getString("time_stamp"))
                .setTimestamp(rs.getString("time_stamp"))
                .setSensorName(rs.getString("sensor_name"))
                .setStepCounts(rs.getInt("step_counts"))
                .setStepDelta(rs.getInt("step_delta"))
                .build();
        resultSet.add(data);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }

    // execute the query, and get a java resultset
    return resultSet;
  }

  /**
   * Call to search for running event for the given date in the activfit sensor data.
   *
   * @param date given date
   * @return all instances of running event recorded for the given date
   */
  private ArrayList<ActivFitSensorData> getRunningEventFromActivFitSensorData(Date date) {
    connection = getConnection();
    // if you only need a few columns, specify them by name instead of using "*"
    String query =
        "SELECT * FROM "
            + ActivFitSensorData.MY_SQL_TABLE_NAME
            + " WHERE formatted_date LIKE '"
            + WebAppConstants.inputDateFormat.format(date)
            + "' AND activity LIKE "
            + "'running'";
    ArrayList<ActivFitSensorData> resultSet = new ArrayList<>();
    // create the java statement
    Statement st;
    try {
      st = connection.createStatement();
      ResultSet rs = st.executeQuery(query);
      while (rs.next()) {
        resultSet.add(getActivFitSensorDataFromResultSet(rs));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }

    // execute the query, and get a java result set
    return resultSet;
  }

  private ActivFitSensorData getActivFitSensorDataFromResultSet(ResultSet rs) throws SQLException {
    return new ActivFitSensorDataBuilder()
        .setStartTime(rs.getString("start_time"))
        .setEndTime(rs.getString("end_time"))
        .setActivity(rs.getString("activity"))
        .setDuration(rs.getInt("duration"))
        .build();
  }

  /**
   * Close database connection
   *
   * @param connection database connection
   */
  private void closeConnection(Connection connection) {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private ArrayList<HeartRateSensorData> getHeartRateSensorDataForGivenDate(Date date) {
    connection = getConnection();
    ArrayList<HeartRateSensorData> results = new ArrayList<>();
    String query =
        "SELECT * FROM "
            + HeartRateSensorData.MY_SQL_TABLE_NAME
            + " WHERE formatted_date LIKE '"
            + WebAppConstants.inputDateFormat.format(date)
            + "'";

    // create the java statement
    Statement st;
    try {
      st = connection.createStatement();
      ResultSet rs = st.executeQuery(query);

      while (rs.next()) {
        //                HeartRateSensorData data = new HeartRateSensorData();
        HeartRateSensorData heartRateSensorData = new HeartRateSensorData();
        heartRateSensorData.setTimestamp(rs.getString("timestamp"));
        heartRateSensorData.setSensorName(rs.getString("sensor_name"));
        HeartRateSensorData.SensorData sensorData = new HeartRateSensorData.SensorData();
        // sensorData.s(rs.getString("sensor_name"));
        sensorData.setBpm(rs.getInt("bpm"));
        heartRateSensorData.setSensorData(sensorData);
        heartRateSensorData.setFormattedDate();
        results.add(heartRateSensorData);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
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
        BatterySensorData batterySensor =
            new BatterySensorDataBuilder()
                .setSensorName(rs.getString("sensor_name"))
                .setTimeStamp(rs.getString("timestamp"))
                .setPercent(rs.getInt("percent"))
                .setCharging(rs.getBoolean("charging")) // duration if charging doesn't work
                .build();

        results.add(batterySensor);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      closeConnection(connection);
    }

    // execute the query, and get a java resultset
    return results;
  }
}
