package servlets.startup;

import com.google.gson.Gson;
import database.FileCumulator;
import database.LuceneManager;
import database.MongoDBManager;
import database.MySqlManager;
import listeners.FileListener;
import sensormodels.*;
import utils.IOUtility;
import utils.UnzipUtility;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StartUpServlet extends HttpServlet {
  private static final UnzipUtility unZipper = new UnzipUtility(); // unzips zip folder and files
  private IOUtility ioUtility; // utility class for IO Operations
  private FileCumulator fileCumulator; // Data cumulator into result files
  private static final String destinationFolder =
      "UncompressedData" + FileSystems.getDefault().getSeparator(); // destination folder
  private static final String sourceFileName =
      "/WEB-INF/classes/SampleUserSmartwatch.zip"; // datasource file

  private MongoDBManager mongoDBManager;
  private LuceneManager luceneManager;
  private MySqlManager mySqlManager;

  @Override
  public void init() {
    System.out.println("--------#####--------");
    System.out.println("        Server started      ");
    System.out.println("--------#####--------");

    ioUtility = IOUtility.getInstance();
    fileCumulator = FileCumulator.getInstance();

    mongoDBManager = MongoDBManager.getInstance();
    luceneManager = LuceneManager.getInstance();
    luceneManager.updateServletContext(getServletContext());
    mySqlManager = MySqlManager.getInstance();

    //    unzipDataSource();

    //        Now store all data into MongoDB, MySQL and Lucene
    storeDataInDatabases(); // store JSON data from file storage
  }

  /** Call to unzip data source and create file structure to accumulate sensor data */
  private void unzipDataSource() {
    try {
      String absoluteDiskPath = getServletContext().getRealPath(sourceFileName);
      //                start unzipping the datasource
      unZipper.unzip(absoluteDiskPath, destinationFolder, new ListenerClass()); // unzip source file
      final File mainFolder = new File(destinationFolder); // get main folder to iterate all files
      ioUtility.iterateFilesAndFolder(
          mainFolder, new ListenerClass()); // Listener Class listens for Files and Zip Files
    } catch (FileAlreadyExistsException ignored) {
      System.out.println("File already exists hence ignored");
    } catch (Exception ex) {
      ex.printStackTrace(); // some error occurred
    }
    //            Unzipping complete
    System.out.println("\n\n*************** Unzipping complete***************\n\n");
  }

  /**
   * Use to store all sensor data into MongoDB using
   *
   * @see MongoDBManager
   */
  private void storeDataInDatabases() {
    System.out.println("*****************Storing data into Databases******************");
    storeActivitySensorData();
    storeActivFitSensorData();
    storeBatterySensorData();
    storeBluetoothSensorData();
    storeHeartRateSensorData();
    storeLightSensorData();
    storeScreenUsageSensorData();
    System.out.println("*****************Storing data into Databases complete******************");
  }

  /** Use to store Sensor Data into mongoDB */
  private void storeActivitySensorData() {
    // store Activity Sensor data into MongoDB
    File activityFile = fileCumulator.getActivityFile();

    Gson gson = new Gson();
    List<ActivitySensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(activityFile).stream()
            .map(
                s -> {
                  try {
                    return gson.fromJson(s, ActivitySensorData.class);
                  } catch (Exception e) {
                    //        e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    //        store activity sensor data in lucene at once
    //        luceneManager.insertSensorDataList(sensorDataList);
    //        insert data into MYSQL for Activity sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store Sensor Data into mongoDB */
  private void storeActivFitSensorData() {
    // store ActivityFit Sensor data into MongoDB
    File activFitFile = fileCumulator.getActivFitFile();

    Gson gson = new Gson();
    List<ActivFitSensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(activFitFile).stream()
            .map(
                s -> {
                  try {
                    return gson.fromJson(s, ActivFitSensorData.class);
                  } catch (Exception e) {
                    //                        e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    //        store data in lucene
    //    luceneManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for ActivFit sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store battery sensor data in databases */
  private void storeBatterySensorData() {
    // store Battery Sensor data into MongoDB
    File batterySensorFile = fileCumulator.getBatterySensorFile();
    Gson g = new Gson();
    List<BatterySensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(batterySensorFile).stream()
            .map(
                s -> {
                  try {
                    BatterySensorData batterySensorData = g.fromJson(s, BatterySensorData.class);
                    batterySensorData.setFormattedDate();
                    return batterySensorData;
                  } catch (Exception e) {
                    //                    e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for battery sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store bluetooth sensor data in databases */
  private void storeBluetoothSensorData() {
    // store Bluetooth Sensor data into MongoDB
    File bluetoothSensorFile = fileCumulator.getBluetoothFile();
    Gson g = new Gson();
    List<BluetoothSensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(bluetoothSensorFile).stream()
            .map(
                s -> {
                  try {
                    BluetoothSensorData bluetoothSensorData =
                        g.fromJson(s, BluetoothSensorData.class);
                    bluetoothSensorData.setFormattedDate();
                    return bluetoothSensorData;
                  } catch (Exception e) {
                    //                    e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for Bluetooth sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store heart rate sensor data in databases */
  private void storeHeartRateSensorData() {
    // store Heart Rate Sensor data into MongoDB
    File heartRateSensorFile = fileCumulator.getHeartRateFile();
    Gson g = new Gson();
    List<HeartRateSensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(heartRateSensorFile).stream()
            .map(
                s -> {
                  try {
                    HeartRateSensorData heartRateSensorData =
                        g.fromJson(s, HeartRateSensorData.class);
                    heartRateSensorData.setFormattedDate();
                    return heartRateSensorData;
                  } catch (Exception e) {
                    //                    e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    //        luceneManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for Heart Rate sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store light sensor data in databases */
  private void storeLightSensorData() {
    // store Light Sensor data into MongoDB
    File lightSensorFile = fileCumulator.getLightSensorFile();
    Gson g = new Gson();
    List<LightSensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(lightSensorFile).stream()
            .map(
                s -> {
                  try {
                    LightSensorData lightSensorData = g.fromJson(s, LightSensorData.class);
                    lightSensorData.setFormattedDate();
                    return lightSensorData;
                  } catch (Exception e) {
                    //                    e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for Light sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Use to store screen usage sensor data in databases */
  private void storeScreenUsageSensorData() {
    // store Screen Usage Sensor data into MongoDB
    File screenUsageFile = fileCumulator.getScreenUsageFile();
    Gson g = new Gson();
    List<ScreenUsageSensorData> sensorDataList =
        IOUtility.getFileContentsLineByLine(screenUsageFile).stream()
            .map(
                s -> {
                  try {
                    ScreenUsageSensorData screenUsageSensorData =
                        g.fromJson(s, ScreenUsageSensorData.class);
                    screenUsageSensorData.setFormattedDate();
                    return screenUsageSensorData;
                  } catch (Exception e) {
                    //                    e.printStackTrace();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    mongoDBManager.insertSensorDataList(sensorDataList);
    // insert data into MYSQL for Screen Usage sensor
    mySqlManager.insertSensorDataList(sensorDataList);
  }

  /** Inner Class which listens for Files and Zip Files/folders when found */
  class ListenerClass implements FileListener {

    @Override
    public void fileFound(File file) {
      File destinationFile =
          fileCumulator.determineFileCategoryAndGet(
              file); // determine to which sensor file belongs to
      ioUtility.appendToFile(destinationFile, file); // append the data to cumulative file
    }

    @Override
    public void zipFileFound(String path) {
      try {
        unZipper.unzip(
            path,
            path.replace(".zip", FileSystems.getDefault().getSeparator()),
            new ListenerClass()); // unzip the file
      } catch (FileAlreadyExistsException ignored) {
        System.out.println("File already exists hence ignored");
      } catch (IOException e) {
        e.printStackTrace(); // some error occurred
      }
    }
  }
}
