package servlets.startup;

import com.google.gson.Gson;
import database.DatabaseManager;
import database.DbManager;
import database.FileCumulator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;
import listeners.FileListener;
import sensormodels.DatabaseModel;
import utils.IoUtility;
import utils.UnzipUtility;

public class StartUpServlet extends HttpServlet {
  private final UnzipUtility unZipper = new UnzipUtility(); // unzips zip folder and files
  private IoUtility ioUtility; // utility class for IO Operations
  private FileCumulator fileCumulator; // Data cumulator into result files
  private static final String destinationFolder =
      "UncompressedData" + FileSystems.getDefault().getSeparator(); // destination folder
  private static final String sourceFileName =
      "/WEB-INF/classes/SampleUserSmartwatch.zip"; // datasource file

  private DbManager dbManager;

  @Override
  public void init() {
    System.out.println("--------#####--------");
    System.out.println("        Server started      ");
    System.out.println("--------#####--------");

    ioUtility = IoUtility.getInstance();
    fileCumulator = FileCumulator.getInstance();

    dbManager = DatabaseManager.getInstance(getServletContext());

    //    unzipDataSource();

    //        Now store all data into all the databases
    storeDataInDatabases(
        fileCumulator.getSensorModelsMap().values()); // store JSON data from file storage
  }

  /** Call to unzip data source and create file structure to accumulate sensor data. */
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
   * Use to store all sensor data into MongoDB using.
   *
   * @see DatabaseManager
   */
  private void storeDataInDatabases(Collection<DatabaseModel> sensorModels) {
    System.out.println("*****************Storing data into Databases******************");

    sensorModels.forEach(
        databaseModel -> {
          List<DatabaseModel> sensorData = readSensorData(databaseModel);
          this.dbManager.insertSensorDataList(sensorData);
        });
    System.out.println("*****************Storing data into Databases complete******************");
  }

  /**
   * Read sensor data from the file location where the cumulative result is stored and return a
   * list.
   *
   * @param sensorModel Sensor Model whose data is to be read
   * @param <T> Type of the sensor model
   * @return list of data read from the file
   */
  private <T extends DatabaseModel> List<T> readSensorData(T sensorModel) {
    File file = sensorModel.getFile();

    Gson gson = new Gson();
    return IoUtility.getFileContentsLineByLine(file).stream()
        .map(
            s -> {
              try {
                T sensorData = gson.fromJson(s, (Type) sensorModel.getClassObject());
                sensorData.setFormattedDate();
                return sensorData;
              } catch (Exception e) {
                //        e.printStackTrace();
              }
              return null;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /** Inner Class which listens for Files and Zip Files/folders when found. */
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
