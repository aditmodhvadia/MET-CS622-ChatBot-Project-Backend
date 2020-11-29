package database;

import com.google.gson.Gson;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import sensormodels.ActivitySensorData;
import sensormodels.BatterySensorData;
import sensormodels.BluetoothSensorData;
import sensormodels.DatabaseModel;
import sensormodels.HeartRateSensorData;
import sensormodels.LightSensorData;
import sensormodels.ScreenUsageSensorData;
import sensormodels.activfit.ActivFitSensorData;
import sensormodels.store.models.FileStoreModel;
import utils.DatabaseUtils;
import utils.IoUtility;
import utils.QueryUtils;
import utils.WebAppConstants;

public class FileCumulator implements DbManager<FileStoreModel>, DatabaseQueryRunner {
  private static FileCumulator instance;
  private static final String MISC_FILE_NAME = "Misc";

  private final IoUtility ioUtility; // to perform IO Operations

  private static final String BASE_ADDRESS = "Result" + FileSystems.getDefault().getSeparator();
  private static final String DATA_FILE_NAME = "CumulativeData.txt";

  private final HashMap<String, DatabaseModel> sensorModelsMap =
      new HashMap<>(); // <File Name, Sensor Model>
  public static File miscFile;

  private FileCumulator() {
    this.ioUtility = IoUtility.getInstance();
    ioUtility.createDirectory(BASE_ADDRESS);
    init(null);
  }

  /**
   * Get singleton instance.
   *
   * @return instance
   */
  public static FileCumulator getInstance() {
    if (instance == null) {
      instance = new FileCumulator();
    }
    return instance;
  }

  public HashMap<String, DatabaseModel> getSensorModelsMap() {
    return sensorModelsMap;
  }

  @Override
  public void init(@Nullable ServletContext servletContext) {
    miscFile =
        ioUtility.createEmptyFile(
            BASE_ADDRESS + MISC_FILE_NAME + FileSystems.getDefault().getSeparator(),
            DATA_FILE_NAME);

    sensorModelsMap.put(ActivFitSensorData.FILE_NAME, new ActivFitSensorData());
    sensorModelsMap.put(ActivitySensorData.FILE_NAME, new ActivitySensorData());
    sensorModelsMap.put(BatterySensorData.FILE_NAME, new BatterySensorData());
    sensorModelsMap.put(BluetoothSensorData.FILE_NAME, new BluetoothSensorData());
    sensorModelsMap.put(HeartRateSensorData.FILE_NAME, new HeartRateSensorData());
    sensorModelsMap.put(LightSensorData.FILE_NAME, new LightSensorData());
    sensorModelsMap.put(ScreenUsageSensorData.FILE_NAME, new ScreenUsageSensorData());

    sensorModelsMap
        .values()
        .forEach(
            databaseModel ->
                databaseModel.setFile(
                    ioUtility.createEmptyFile(
                        BASE_ADDRESS
                            + databaseModel.getFileName()
                            + FileSystems.getDefault().getSeparator(),
                        DATA_FILE_NAME)));
  }

  @Override
  public <V extends FileStoreModel> void insertSensorDataList(List<V> sensorDataList) {
    System.out.println("No need to store in the file system again.");
  }

  @Override
  public <V extends FileStoreModel> void insertSensorData(V sensorData) {
    System.out.println("No need to store in the file system again.");
  }

  @Override
  public ArrayList<ActivFitSensorData> queryForRunningEvent(Date today) {
    Date tomorrow = QueryUtils.addDayToDate(today, 1); //        get the next Day Date as well
    //        fetch all record from the collection
    List<ActivFitSensorData> allSensorData =
        getSensorFileContents(
            (ActivFitSensorData) sensorModelsMap.get(ActivFitSensorData.FILE_NAME), 1000);

    return allSensorData.stream()
        .filter(
            sensorData -> {
              Date sensorDataStartTime = new Date(sensorData.getTimestamp().getStartTime());
              return DatabaseUtils.isWithinDateRange(today, tomorrow, sensorDataStartTime)
                  && DatabaseUtils.shouldBeRunningAndNotUnknown(
                      sensorData.getSensorData().getActivity());
            })
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public int queryHeartRatesForDay(Date date) {
    List<HeartRateSensorData> sensorData = getHeartRateSensorFileContents();
    int heartRateCount = 0;
    String formattedDate = WebAppConstants.inputDateFormat.format(date);
    for (HeartRateSensorData data : sensorData) {
      if (data.getFormattedDate().equals(formattedDate)) {
        heartRateCount++;
      }
    }
    return heartRateCount;
  }

  @Override
  public int queryForTotalStepsInDay(Date userDate) {
    List<ActivitySensorData> activitySensorDataList =
        getSensorFileContents(
            (ActivitySensorData) sensorModelsMap.get(ActivitySensorData.FILE_NAME), 1000);
    int maxStepCount = -1; // Max value of step count for the day
    String userFormattedDate = WebAppConstants.inputDateFormat.format(userDate);
    for (ActivitySensorData sensorData : activitySensorDataList) {
      if (sensorData.getFormattedDate().equals(userFormattedDate)) { // both dates are equal
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
   * file.
   *
   * @param inputFile given file
   * @return the cumulative data file
   */
  public File determineFileCategoryAndGet(File inputFile) {
    for (String fileName : sensorModelsMap.keySet()) {
      if (inputFile.getPath().contains(fileName)) {
        return sensorModelsMap.get(fileName).getFile();
      }
    }
    return miscFile;
  }

  static <T extends FileStoreModel> List<T> getSensorFileContents(T sensorModel, int numOfDays) {
    List<T> sensorDataList = new ArrayList<>(); // holds the sensor data
    List<String> fileContents =
        IoUtility.getFileContentsLineByLine(
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

  List<HeartRateSensorData> getHeartRateSensorFileContents() {
    List<HeartRateSensorData> sensorDataList = new ArrayList<>(); // holds the sensor data
    List<String> fileContents =
        IoUtility.getFileContentsLineByLine(
            sensorModelsMap
                .get(HeartRateSensorData.FILE_NAME)
                .getFile()); // holds all lines of the cumulativeFile for the sensor
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
   * Call to calculate brute force search time for running activity in given number of days.
   *
   * @param numOfDays given number of days
   * @return time taken to search the data through brute force
   */
  long searchForRunningActivity(int numOfDays) {
    long searchTime = System.currentTimeMillis(); // used to calculate search time for brute force
    for (ActivFitSensorData sensorData :
        getSensorFileContents(
            (ActivFitSensorData) sensorModelsMap.get(ActivFitSensorData.FILE_NAME),
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
   * time.
   *
   * @param numOfDays given number of days
   */
  long searchForLessBrightData(int numOfDays) {
    long searchTime = System.currentTimeMillis(); // used to calculate search time for brute force
    for (LightSensorData sensorData :
        getSensorFileContents(
            (LightSensorData) sensorModelsMap.get(LightSensorData.FILE_NAME),
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
   * Call to search for 100 bpm data in given number of days and calculate brute force search time.
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
