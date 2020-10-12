package servlets.startup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import database.MongoDBManager;
import database.MySqlManager;
import listeners.FileListener;
import database.LuceneManager;
import sensormodels.*;
import database.FileCumulator;
import utils.IOUtility;
import utils.UnzipUtility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class StartUpServlet extends HttpServlet {
    private static final UnzipUtility unZipper = new UnzipUtility();    // unzips zip folder and files
    private static final IOUtility ioUtility = new IOUtility();     // utility class for IO Operations
    private static final FileCumulator fileCumulator = new FileCumulator(ioUtility);    // Data cumulator into result files
    private static final String destinationFolder = "UncompressedData" + FileSystems.getDefault().getSeparator(); // destination folder
    private static final String sourceFileName = "/WEB-INF/classes/SampleUserSmartwatch.zip";    // datasource file

    private MongoDBManager mongoDBManager;
    private LuceneManager luceneManager;
    private MySqlManager mySqlManager;


    @Override
    public void init() throws ServletException {
        System.out.println("--------#####--------");
        System.out.println("        Server started      ");
        System.out.println("--------#####--------");

        mongoDBManager = MongoDBManager.getInstance();
        luceneManager = LuceneManager.getInstance(getServletContext());
        mySqlManager = MySqlManager.getInstance();

//        unzipDataSource();

//        Now store all data into MongoDB, MySQL and Lucene
//        storeDataInDatabases();   // store JSON data from file storage
    }

    /**
     * Call to unzip data source and create file structure to accumulate sensor data
     */
    private void unzipDataSource() {
        try {
            String absoluteDiskPath = getServletContext().getRealPath(sourceFileName);
//                start unzipping the datasource
            unZipper.unzip(absoluteDiskPath, destinationFolder, new ListenerClass());  // unzip source file
            final File mainFolder = new File(destinationFolder);     // get main folder to iterate all files
            ioUtility.iterateFilesAndFolder(mainFolder, new ListenerClass());// Listener Class listens for Files and Zip Files
        } catch (FileAlreadyExistsException ignored) {
            System.out.println("File already exists hence ignored");
        } catch (Exception ex) {
            ex.printStackTrace();   // some error occurred
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
    }

    /**
     * Use to store Sensor Data into mongoDB
     */
    private void storeActivitySensorData() {
        // store Activity Sensor data into MongoDB
        File activityFile = fileCumulator.getActivityFile();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(activityFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // use gson to map it to the model
//        TODO: Check this and implement for all the sensor data methods
        Type heartRateListType = new TypeToken<ArrayList<ActivitySensorData>>() {
        }.getType();
        List<ActivitySensorData> sensorDataList = new Gson().fromJson(reader, heartRateListType);
/*
//        List<ActivitySensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(activityFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ActivitySensorData activitySensorData = g.fromJson(fileLine, ActivitySensorData.class);
                activitySensorData.setFormattedDate();
                sensorDataList.add(activitySensorData);

            } catch (Exception ignored) {
//                ignore false entries
            }
        }*/
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.activitySensorDataMongoCollection, sensorDataList);
//        store activity sensor data in lucene at once
        luceneManager.storeSensorData(sensorDataList);
//        insert data into MYSQL for Activity sensor
        mySqlManager.insertIntoActivityTable(sensorDataList);
    }

    /**
     * Use to store Sensor Data into mongoDB
     */
    private void storeActivFitSensorData() {
        // store ActivityFit Sensor data into MongoDB
        File activFitFile = fileCumulator.getActivFitFile();
        List<ActivFitSensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(activFitFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ActivFitSensorData activFitSensorData = g.fromJson(fileLine, ActivFitSensorData.class);
                activFitSensorData.setFormattedDate();
                sensorDataList.add(activFitSensorData);

            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.activFitSensorDataMongoCollection, sensorDataList);
//        store data in lucene
        luceneManager.storeSensorData(sensorDataList);
        //insert data into MYSQL for ActivFit sensor
        mySqlManager.insertIntoActivFitTable(sensorDataList);
    }

    /**
     * Use to store battery sensor data in databases
     */
    private void storeBatterySensorData() {
        // store Battery Sensor data into MongoDB
        File batterySensorFile = fileCumulator.getBatterySensorFile();
        List<BatterySensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(batterySensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                BatterySensorData batterySensorData = g.fromJson(fileLine, BatterySensorData.class);
                batterySensorData.setFormattedDate();
                sensorDataList.add(batterySensorData);
            } catch (Exception ignored) {
                // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.batterySensorDataMongoCollection, sensorDataList);
        //insert data into MYSQL for battery sensor
        mySqlManager.insertIntoBatteryTable(sensorDataList);
    }

    /**
     * Use to store bluetooth sensor data in databases
     */
    private void storeBluetoothSensorData() {
        // store Bluetooth Sensor data into MongoDB
        File bluetoothSensorFile = fileCumulator.getBluetoothFile();
        List<BluetoothSensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(bluetoothSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                BluetoothSensorData bluetoothSensorData = g.fromJson(fileLine, BluetoothSensorData.class);
                bluetoothSensorData.setFormattedDate();
                sensorDataList.add(bluetoothSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.bluetoothSensorDataMongoCollection, sensorDataList);
        //insert data into MYSQL for Bluetooth sensor
        mySqlManager.insertIntoBluetoothTable(sensorDataList);
    }

    /**
     * Use to store heart rate sensor data in databases
     */
    private void storeHeartRateSensorData() {
        // store Heart Rate Sensor data into MongoDB
        File heartRateSensorFile = fileCumulator.getHeartRateFile();
        List<HeartRateSensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(heartRateSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                HeartRateSensorData heartRateSensorData = g.fromJson(fileLine, HeartRateSensorData.class);
                heartRateSensorData.setFormattedDate();
                sensorDataList.add(heartRateSensorData);
            } catch (Exception ignored) {
                // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.heartRateSensorDataMongoCollection, sensorDataList);
        luceneManager.storeSensorData(sensorDataList);
        //insert data into MYSQL for Heart Rate sensor
        mySqlManager.insertIntoHeartRateTable(sensorDataList);
    }

    /**
     * Use to store light sensor data in databases
     */
    private void storeLightSensorData() {
        // store Light Sensor data into MongoDB
        File lightSensorFile = fileCumulator.getLightSensorFile();
        List<LightSensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(lightSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                LightSensorData lightSensorData = g.fromJson(fileLine, LightSensorData.class);
                lightSensorData.setFormattedDate();
                sensorDataList.add(lightSensorData);

            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.lightSensorDataMongoCollection, sensorDataList);
        //insert data into MYSQL for Light sensor
        mySqlManager.insertIntoLightTable(sensorDataList);
    }

    /**
     * Use to store screen usage sensor data in databases
     */
    private void storeScreenUsageSensorData() {
        // store Screen Usage Sensor data into MongoDB
        File screenUsageFile = fileCumulator.getScreenUsageFile();
        List<ScreenUsageSensorData> sensorDataList = new ArrayList<>();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(screenUsageFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ScreenUsageSensorData screenUsageSensorData = g.fromJson(fileLine, ScreenUsageSensorData.class);
                screenUsageSensorData.setFormattedDate();
                sensorDataList.add(screenUsageSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
        mongoDBManager.insertDocumentsIntoCollection(MongoDBManager.screenUsageSensorDataMongoCollection, sensorDataList);
        //insert data into MYSQL for Screen Usage sensor
        mySqlManager.insertIntoScreenUsageTable(sensorDataList);
    }

    /**
     * Inner Class which listens for Files and Zip Files/folders when found
     */
    static class ListenerClass implements FileListener {

        @Override
        public void fileFound(File file) {
            File destinationFile = fileCumulator.determineFileCategoryAndGet(file);   // determine to which sensor file belongs to
            ioUtility.appendToFile(destinationFile, file);                            // append the data to cumulative file
        }

        @Override
        public void zipFileFound(String path) {
            try {
                unZipper.unzip(path, path.replace(".zip", FileSystems.getDefault().getSeparator()), new ListenerClass());   // unzip the file
            } catch (FileAlreadyExistsException ignored) {
                System.out.println("File already exists hence ignored");
            } catch (IOException e) {
                e.printStackTrace();    // some error occurred
            }
        }
    }
}
