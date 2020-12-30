package servlets.startup

import com.google.gson.Gson
import database.DatabaseManager
import database.DatabaseManager.Companion.getInstance
import database.DbManager
import database.FileCumulator
import database.FileCumulator.Companion.instance
import listeners.FileListener
import sensormodels.store.models.FileStoreModel
import sensormodels.store.models.SuperStoreModel
import utils.IoUtility.appendToFile
import utils.IoUtility.getFileContentsLineByLine
import utils.IoUtility.iterateFilesAndFolder
import utils.UnzipUtility
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.nio.file.FileAlreadyExistsException
import java.nio.file.FileSystems
import java.util.*
import java.util.function.Consumer
import javax.servlet.http.HttpServlet

class StartUpServlet : HttpServlet() {
    private val unZipper = UnzipUtility() // unzips zip folder and files
    private var fileCumulator // Data cumulator into result files
            : FileCumulator? = null
    private var dbManager: DbManager<SuperStoreModel>? = null
    override fun init() {
        println("--------#####--------")
        println("        Server started      ")
        println("--------#####--------")
        fileCumulator = instance
        dbManager = getInstance(servletContext)

        //    unzipDataSource();

        //        Now store all data into all the databases
        storeDataInDatabases(
            fileCumulator!!.sensorModelsMap.values
        ) // store JSON data from file storage
    }

    /** Call to unzip data source and create file structure to accumulate sensor data.  */
    private fun unzipDataSource() {
        try {
            val absoluteDiskPath = servletContext.getRealPath(sourceFileName)
            //                start unzipping the datasource
            unZipper.unzip(absoluteDiskPath, destinationFolder, ListenerClass()) // unzip source file
            val mainFolder = File(destinationFolder) // get main folder to iterate all files
            iterateFilesAndFolder(
                mainFolder, ListenerClass()
            ) // Listener Class listens for Files and Zip Files
        } catch (ignored: FileAlreadyExistsException) {
            println("File already exists hence ignored")
        } catch (ex: Exception) {
            ex.printStackTrace() // some error occurred
        }
        println("\n\n*************** Unzipping complete***************\n\n")
    }

    /**
     * Use to store all sensor data into MongoDB using.
     *
     * @see DatabaseManager
     */
    private fun storeDataInDatabases(sensorModels: Collection<SuperStoreModel>) {
        println("*****************Storing data into Databases******************")
        sensorModels.forEach(
            Consumer { databaseModel: SuperStoreModel ->
                val sensorData = readSensorData(databaseModel)
                dbManager!!.insertSensorDataList(sensorData)
            })
        println("*****************Storing data into Databases complete******************")
    }

    /**
     * Read sensor data from the file location where the cumulative result is stored and return a
     * list.
     *
     * @param sensorModel Sensor Model whose data is to be read
     * @param <T> Type of the sensor model
     * @return list of data read from the file
    </T> */
    private fun <T : FileStoreModel> readSensorData(sensorModel: T): List<T> {
        val file: File = sensorModel.file!!
        val gson = Gson()

        return getFileContentsLineByLine(file).map { s ->
            try {
                val sensorData: T = gson.fromJson(s, sensorModel.javaClass as Type?)
                sensorData.setFormattedDate()
                return@map sensorData
            } catch (ignored: Exception) {
            }
            return@map null
        }.filterNotNull().toList()
    }

    /** Inner Class which listens for Files and Zip Files/folders when found.  */
    internal inner class ListenerClass : FileListener {
        override fun fileFound(file: File) {
            val destinationFile = fileCumulator!!.determineFileCategoryAndGet(
                file
            ) // determine to which sensor file belongs to
            appendToFile(destinationFile, file) // append the data to cumulative file
        }

        override fun zipFileFound(path: String) {
            try {
                unZipper.unzip(
                    path,
                    path.replace(".zip", FileSystems.getDefault().separator),
                    ListenerClass()
                ) // unzip the file
            } catch (ignored: FileAlreadyExistsException) {
                println("File already exists hence ignored")
            } catch (e: IOException) {
                e.printStackTrace() // some error occurred
            }
        }
    }

    companion object {
        private val destinationFolder = "UncompressedData" + FileSystems.getDefault().separator // destination folder
        private const val sourceFileName = "/WEB-INF/classes/SampleUserSmartwatch.zip" // datasource file
    }
}