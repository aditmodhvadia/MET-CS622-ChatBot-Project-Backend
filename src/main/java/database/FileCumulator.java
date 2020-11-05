package database;

import com.google.gson.Gson;
import sensormodels.*;
import sensormodels.store.models.FileStoreModel;
import utils.IOUtility;
import utils.QueryUtils;
import utils.WebAppConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.util.*;

public class FileCumulator implements DbManager, DatabaseQueryRunner {
  private static FileCumulator instance;
  private static final String ACTIV_FIT = "ActivFit";
  private static final String ACTIVITY = "Activity";
  private static final String BATTERY_SENSOR = "BatterySensor";
  private static final String BLUETOOTH = "Bluetooth";
  private static final String ERROR = "Error";
  private static final String HEART_RATE = "HeartRate";
  private static final String LIGHT_SENSOR = "LightSensor";
  private static final String SCREEN_USAGE = "ScreenUsage";
  private static final String MISC = "Misc";

  private final IOUtility ioUtility; // to perform IO Operations

  private static final String BASE_ADDRESS = "Result" + FileSystems.getDefault().getSeparator();
  private static final String DATA_FILE_NAME = "CumulativeData.txt";

  public static File activityFile,
      activFitFile,
      batterySensorFile,
      bluetoothFile,
      errorFile,
      heartRateFile,
      lightSensorFile,
      screenUsageFile,
      miscFile;
  private final ArrayList<File> files = new ArrayList<>();
  private final HashMap<String, DatabaseModel> sensorModels =
      new HashMap<>(); // <File Name, Sensor Model>

  private FileCumulator() {
    this.ioUtility = IOUtility.getInstance();
    ioUtility.createDirectory(BASE_ADDRESS);
    init();
  }

  public static FileCumulator getInstance() {
    if (instance == null) {
      instance = new FileCumulator();
    }
    return instance;
  }

  public HashMap<String, DatabaseModel> getSensorModels() {
    return sensorModels;
  }

  @Override
  public void init() {
    activFitFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + ACTIV_FIT + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    activityFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + ACTIVITY + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    batterySensorFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + BATTERY_SENSOR + FileSystems.getDefault().getSeparator(),
            DATA_FILE_NAME);
    bluetoothFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + BLUETOOTH + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    errorFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + ERROR + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    heartRateFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + HEART_RATE + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    lightSensorFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + LIGHT_SENSOR + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    screenUsageFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + SCREEN_USAGE + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);
    miscFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + MISC + FileSystems.getDefault().getSeparator(), DATA_FILE_NAME);

    // add files to ArrayList
    files.add(activityFile);
    files.add(activFitFile);
    files.add(batterySensorFile);
    files.add(bluetoothFile);
    files.add(errorFile);
    files.add(heartRateFile);
    files.add(lightSensorFile);
    files.add(screenUsageFile);

    sensorModels.put(ActivFitSensorData.FILE_NAME, new ActivFitSensorData());
    sensorModels.put(ActivitySensorData.FILE_NAME, new ActivitySensorData());
    sensorModels.put(BatterySensorData.FILE_NAME, new BatterySensorData());
    sensorModels.put(BluetoothSensorData.FILE_NAME, new BluetoothSensorData());
    sensorModels.put(HeartRateSensorData.FILE_NAME, new HeartRateSensorData());
    sensorModels.put(LightSensorData.FILE_NAME, new LightSensorData());
    sensorModels.put(ScreenUsageSensorData.FILE_NAME, new ScreenUsageSensorData());

    for (FileStoreModel sensor : sensorModels.values()) {
      sensor.setFile(
          ioUtility.createEmptyFile(
              BASE_ADDRESS + sensor.getFileName() + FileSystems.getDefault().getSeparator(),
              DATA_FILE_NAME));
    }
  }

  @Override
  public ArrayList<ActivFitSensorData> queryForRunningEvent(Date date) {
    //        get the next Day Date as well
    Date nextDate = QueryUtils.addDayToDate(date, 1);
    //        fetch all record from the collection
    List<ActivFitSensorData> fileData =
        getSensorFileContents(
            (ActivFitSensorData) sensorModels.get(ActivFitSensorData.FILE_NAME), 1000);
    ArrayList<ActivFitSensorData> queryResult =
        new ArrayList<>(); // holds the result from the query
    for (ActivFitSensorData nextData : fileData) {
      //            get startDate of the SensorData entry and then check if it lies between the user
      // entered Date and the next day or not
      Date startDate = new Date(nextData.getTimestamp().getStartTime());
      if (startDate.after(date) && startDate.before(nextDate)) { // check it lies within range
        if (!nextData.getSensorData().getActivity().equals("unknown")
            && Objects.equals(nextData.getSensorData().getActivity(), "running")) {
          //                    SensorData entry lies between the dates and is for running, so add
          // it to the result List
          queryResult.add(nextData);
        }
      }
    }
    return queryResult;
  }

  @Override
  public int queryHeartRatesForDay(Date date) {
    List<HeartRateSensorData> sensorData = getHeartRateSensorFileContents();
    int heartRateCount = 0;
    String formattedDate = WebAppConstants.inputDateFormat.format(date);
    for (HeartRateSensorData data : sensorData) {
      if (data.getFormatted_date().equals(formattedDate)) {
        heartRateCount++;
      }
    }
    return heartRateCount;
  }

  @Override
  public int queryForTotalStepsInDay(Date userDate) {
    List<ActivitySensorData> activitySensorDataList =
        getSensorFileContents(
            (ActivitySensorData) sensorModels.get(ActivitySensorData.FILE_NAME), 1000);
    int maxStepCount = -1; // Max value of step count for the day
    String userFormattedDate = WebAppConstants.inputDateFormat.format(userDate);
    for (ActivitySensorData sensorData : activitySensorDataList) {
      if (sensorData.getFormatted_date().equals(userFormattedDate)) { // both dates are equal
        if (sensorData.getSensorData().getStepCounts() > maxStepCount) {
          //                    found a step count larger than the maxStepCount, so update it
          maxStepCount = sensorData.getSensorData().getStepCounts();
        }
      }
    }
    return maxStepCount;
  }

  /**
   * Use to determine the category of sensor for the given file and returns the cumulative data txt
   * file
   *
   * @param inputFile given file
   * @return the cumulative data file
   */
  public File determineFileCategoryAndGet(File inputFile) {
    for (String fileName : sensorModels.keySet()) {
      if (inputFile.getPath().contains(fileName)) {
        return sensorModels.get(fileName).getFile();
      }
    }
    return miscFile;
  }

  /**
   * Use to search the given query text in the given sensor name
   *
   * @param sensorName given sensor name
   * @param query given query text
   * @return search result from running the query
   * @throws FileNotFoundException if sensor name is incorrect
   */
  String searchSensorData(String sensorName, String query) throws FileNotFoundException {
    if (sensorName.equalsIgnoreCase(ACTIVITY)) {

      return ioUtility.findSearchResultsFromFile(activityFile, query);

    } else if (sensorName.equalsIgnoreCase(ACTIV_FIT)) {

      return ioUtility.findSearchResultsFromFile(activFitFile, query);

    } else if (sensorName.equalsIgnoreCase(BATTERY_SENSOR)) {

      return ioUtility.findSearchResultsFromFile(batterySensorFile, query);

    } else if (sensorName.equalsIgnoreCase(BLUETOOTH)) {

      return ioUtility.findSearchResultsFromFile(bluetoothFile, query);

    } else if (sensorName.equalsIgnoreCase(ERROR)) {

      return ioUtility.findSearchResultsFromFile(errorFile, query);

    } else if (sensorName.equalsIgnoreCase(HEART_RATE)) {

      return ioUtility.findSearchResultsFromFile(heartRateFile, query);

    } else if (sensorName.equalsIgnoreCase(LIGHT_SENSOR)) {

      return ioUtility.findSearchResultsFromFile(lightSensorFile, query);

    } else if (sensorName.equalsIgnoreCase(SCREEN_USAGE)) {

      return ioUtility.findSearchResultsFromFile(screenUsageFile, query);

    } else {

      throw new FileNotFoundException("Illegal file name");
    }
  }

  List<File> getAllSensorFiles() {
    return files;
  }

  public File getActivityFile() {
    return activityFile;
  }

  public File getActivFitFile() {
    return activFitFile;
  }

  public File getBatterySensorFile() {
    return batterySensorFile;
  }

  public File getBluetoothFile() {
    return bluetoothFile;
  }

  public File getErrorFile() {
    return errorFile;
  }

  public File getHeartRateFile() {
    return heartRateFile;
  }

  public File getLightSensorFile() {
    return lightSensorFile;
  }

  public File getScreenUsageFile() {
    return screenUsageFile;
  }

  static <T extends FileStoreModel> List<T> getSensorFileContents(T sensorModel, int numOfDays) {
    List<T> sensorDataList = new ArrayList<>(); // holds the sensor data
    List<String> fileContents =
        IOUtility.getFileContentsLineByLine(
            sensorModel.getFile()); // holds all lines of the cumulativeFile for the sensor
    String currentDate = ""; // will hold the value of current date
    for (String fileLine : fileContents) {
      Gson g = new Gson();
      try {
        //                converts JSON string into POJO
        T sensorData = g.fromJson(fileLine, (Type) sensorModel.getClassObject());
        sensorData.setFormattedDate();
        //                get date of current sensor data to compare
        String sensorFormattedDate = getFormattedDateFromTimeStamp(sensorData.getStartTime());

        if (sensorFormattedDate.equals(currentDate)) {
          //                    add sensor data to list as date is same as current date
          sensorDataList.add(sensorData);
        } else {
          currentDate = sensorFormattedDate; // update current date
          numOfDays--; // decrement num of days left
          if (numOfDays == -1) {
            //                        found data for the specified number of days so return the
            // sensor data list
            return sensorDataList;
          }
        }
      } catch (Exception e) {
        //                e.printStackTrace();
        //                System.out.println("Incorrect JSON format");    // don't store data in
        // mongodb
      }
    }
    return sensorDataList;
  }

  static List<HeartRateSensorData> getHeartRateSensorFileContents() {
    List<HeartRateSensorData> sensorDataList = new ArrayList<>(); // holds the sensor data
    List<String> fileContents =
        IOUtility.getFileContentsLineByLine(
            heartRateFile); // holds all lines of the cumulativeFile for the sensor
    String currentDate = ""; // will hold the value of current date
    for (String fileLine : fileContents) {
      Gson g = new Gson();
      try {
        //                converts JSON string into POJO
        HeartRateSensorData heartRateSensorData = g.fromJson(fileLine, HeartRateSensorData.class);
        heartRateSensorData.setFormattedDate();
        sensorDataList.add(heartRateSensorData);
      } catch (Exception e) {
        //                e.printStackTrace();
        //                System.out.println("Incorrect JSON format");    // don't store data in
        // mongodb
      }
    }

    return sensorDataList;
  }

  public static String getFormattedDateFromTimeStamp(String timestamp) {
    Date sensorDate = new Date(timestamp);
    return WebAppConstants.inputDateFormat.format(sensorDate);
  }

  /**
   * Call to calculate brute force search time for running activity in given number of days
   *
   * @param numOfDays given number of days
   * @return time taken to search the data through brute force
   */
  long searchForRunningActivity(int numOfDays) {
    long searchTime = System.currentTimeMillis(); // used to calculate search time for brute force
    for (ActivFitSensorData sensorData :
        getSensorFileContents(
            (ActivFitSensorData) sensorModels.get(ActivFitSensorData.FILE_NAME),
            numOfDays)) { // iterate all sensordata and find the result
      if (sensorData.getSensorData().getActivity().equalsIgnoreCase("running")) {
        //                System.out.println("Running event found " +
        // sensorData.getTimestamp().getStartTime());
      }
    }
    //        search complete, calculate search time
    searchTime = System.currentTimeMillis() - searchTime;
    System.out.println("Brute force took " + searchTime + "ms for running");
    return searchTime;
  }

  /**
   * Call to search for less bright data in given number of days and calculate brute force search
   * time
   *
   * @param numOfDays given number of days
   */
  long searchForLessBrightData(int numOfDays) {
    long searchTime = System.currentTimeMillis(); // used to calculate search time for brute force
    for (LightSensorData sensorData :
        getSensorFileContents(
            (LightSensorData) sensorModels.get(LightSensorData.FILE_NAME),
            numOfDays)) { // iterate all sensordata and find the result
      if (sensorData.getLuxValue().equalsIgnoreCase("less bright")) {
        //                System.out.println("less bright found " + sensorData.getTimestamp());
      }
    }
    //        search complete, calculate search time
    searchTime = System.currentTimeMillis() - searchTime;
    System.out.println("Brute force took " + searchTime + "ms for less bright");
    return searchTime;
  }

  /**
   * Call to search for 100 bpm data in given number of days and calculate brute force search time
   *
   * @param numOfDays given number of days
   */
  long searchForHundredBpm(int numOfDays) {
    long searchTime = System.currentTimeMillis(); // used to calculate search time for brute force
    for (HeartRateSensorData sensorData :
        getHeartRateSensorFileContents()) { // iterate all sensordata and find the result
      if (sensorData.getSensorData().getBpm() == 100) {
        //                System.out.println("100 bpm found " + sensorData.getTimestamp());
      }
    }
    //        search complete, calculate search time
    searchTime = System.currentTimeMillis() - searchTime;
    System.out.println("Brute force took " + searchTime + "ms for 100 bpm");
    return searchTime;
  }
}