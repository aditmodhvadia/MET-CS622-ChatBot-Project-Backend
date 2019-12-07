package servlets.startup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import database.MongoDBManager;
import database.MySqlManager;
import listeners.FileListener;
import sensormodels.*;
import utils.FileCumulator;
import utils.IOUtility;
import utils.UnzipUtility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;

public class StartUpServlet extends HttpServlet {
    private static final UnzipUtility unZipper = new UnzipUtility();    // unzips zip folder and files
    private static final IOUtility ioUtility = new IOUtility();     // utility class for IO Operations
    private static final FileCumulator fileCumulator = new FileCumulator(ioUtility);    // Data cumulator into result files
    private static final String destinationFolder = "UncompressedData" + FileSystems.getDefault().getSeparator(); // destination folder
    private static final String sourceFileName = "/WEB-INF/classes/SampleUserSmartwatch.zip";    // datasource file


    @Override
    public void init() throws ServletException {
        System.out.println("--------#####--------");
        System.out.println("        Server started      ");
        System.out.println("--------#####--------");
//        Execute work from here
//        testing libs

//        init MongoDB to get references to Database and Collections
//        MongoDBManager.init();

        MySqlManager.init();

//        TODO: Add check if files already unzipped and if data already present in MongoDB, then don't unzip it and store in MongoDB

        /*try {
            String absoluteDiskPath = getServletContext().getRealPath(sourceFileName);
//                start unzipping the datasource
            unZipper.unzip(absoluteDiskPath, destinationFolder, new ListenerClass());  // unzip source file
            final File mainFolder = new File(destinationFolder);     // get main folder to iterate all files
            ioUtility.iterateFilesAndFolder(mainFolder, new ListenerClass());// Listener Class listens for Files and Zip Files
        } catch (FileAlreadyExistsException ignored) {
            System.out.println("File already exists hence ignored");
        } catch (Exception ex) {
            ex.printStackTrace();   // some error occurred
        }*/
//            Unzipping complete
        System.out.println("\n\n*************** Unzipping complete***************\n\n");

//        Now store all data into MongoDB
//        storeDataInMongoDB();   // store JSON data into mongoDB

//        TODO: Lucene remaining

    }

    /**
     * Use to store all sensor data into MongoDB using
     *
     * @see MongoDBManager
     */
    private static void storeDataInMongoDB() {
        System.out.println("*****************Storing data into MongoDB******************");

        storeActivitySensorDataInMongoDB();   // works

        storeActivFitSensorDataInMongoDB(); //  works

        storeBatterySensorDataInMongoDB();  //  works

        storeBluetoothSensorDataInMongoDB();    //  works

        storeHeartRateSensorDataInMongoDB();  //  works

        storeLightSensorDataInMongoDB();    //  works

        storeScreenUsageSensorDataInMongoDB();  //  works
    }

    /**
     * Use to store Sensor Data into mongoDB
     */
    private static void storeActivitySensorDataInMongoDB() {
        // store Activity Sensor data into MongoDB
        File activityFile = fileCumulator.getActivityFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(activityFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ActivitySensorData activitySensorData = g.fromJson(fileLine, ActivitySensorData.class);
//                insert the new document into mongodb
//                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.activitySensorDataMongoCollection, activitySensorData);
//                removed for now for testing

//                insert data into MYSQL for Activity sensor
                MySqlManager.insertIntoActivityTable(activitySensorData);

            } catch (JsonSyntaxException e) {
//                e.printStackTrace();  //  do nothing
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    /**
     * Use to store Sensor Data into mongoDB
     */
    private static void storeActivFitSensorDataInMongoDB() {
        // store ActivityFit Sensor data into MongoDB
        File activFitFile = fileCumulator.getActivFitFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(activFitFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ActivFitSensorData activFitSensorData = g.fromJson(fileLine, ActivFitSensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.activFitSensorDataMongoCollection, activFitSensorData);

                //insert data into MYSQL for ActivFit sensor
                MySqlManager.insertIntoActivFitTable(activFitSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    private static void storeBatterySensorDataInMongoDB() {
        // store Battery Sensor data into MongoDB
        File batterySensorFile = fileCumulator.getBatterySensorFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(batterySensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                BatterySensorData batterySensorData = g.fromJson(fileLine, BatterySensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.batterySensorDataMongoCollection, batterySensorData);

                //insert data into MYSQL for battery sensor
                MySqlManager.insertIntoBatteryTable(batterySensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    private static void storeBluetoothSensorDataInMongoDB() {
        // store Bluetooth Sensor data into MongoDB
        File bluetoothSensorFile = fileCumulator.getBluetoothFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(bluetoothSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                BluetoothSensorData bluetoothSensorData = g.fromJson(fileLine, BluetoothSensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.bluetoothSensorDataMongoCollection, bluetoothSensorData);

                //insert data into MYSQL for Bluetooth sensor
                MySqlManager.insertIntoBluetoothTable(bluetoothSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    private static void storeHeartRateSensorDataInMongoDB() {
        // store Heart Rate Sensor data into MongoDB
        File heartRateSensorFile = fileCumulator.getHeartRateFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(heartRateSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                HeartRateSensorData heartRateSensorData = g.fromJson(fileLine, HeartRateSensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.heartRateSensorDataMongoCollection, heartRateSensorData);

                //insert data into MYSQL for Heart Rate sensor
                MySqlManager.insertIntoHeartRateTable(heartRateSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    private static void storeLightSensorDataInMongoDB() {
        // store Light Sensor data into MongoDB
        File lightSensorFile = fileCumulator.getLightSensorFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(lightSensorFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                LightSensorData lightSensorData = g.fromJson(fileLine, LightSensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.lightSensorDataMongoCollection, lightSensorData);

                //insert data into MYSQL for Light sensor
                MySqlManager.insertIntoLightTable(lightSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
    }

    private static void storeScreenUsageSensorDataInMongoDB() {
        // store Screen Usage Sensor data into MongoDB
        File screenUsageFile = fileCumulator.getScreenUsageFile();
        for (String fileLine :
                ioUtility.getFileContentsLineByLine(screenUsageFile)) {
            Gson g = new Gson();
            try {
//                converts JSON string into POJO
                ScreenUsageSensorData screenUsageSensorData = g.fromJson(fileLine, ScreenUsageSensorData.class);
//                insert the new document into mongodb
                MongoDBManager.insertDocumentIntoCollection(MongoDBManager.screenUsageSensorDataMongoCollection, screenUsageSensorData);

                //insert data into MYSQL for Screen Usage sensor
                MySqlManager.insertIntoScreenUsageTable(screenUsageSensorData);
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Incorrect JSON format");    // don't store data in mongodb
            }
        }
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
