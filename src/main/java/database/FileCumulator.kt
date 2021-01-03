package database

import com.google.gson.Gson
import sensormodels.BluetoothSensorData
import sensormodels.HeartRateSensorData
import sensormodels.LightSensorData
import sensormodels.ScreenUsageSensorData
import sensormodels.activfit.ActivFitSensorData
import sensormodels.activity.ActivitySensorData
import sensormodels.battery.BatterySensorData
import sensormodels.store.models.FileStoreModel
import utils.DatabaseUtils.isWithinDateRange
import utils.DatabaseUtils.shouldBeRunningAndNotUnknown
import utils.IoUtility.createDirectory
import utils.IoUtility.createEmptyFile
import utils.IoUtility.getFileContentsLineByLine
import utils.QueryUtils.addDayToDate
import utils.WebAppConstants.formatted
import java.io.File
import java.lang.reflect.Type
import java.nio.file.FileSystems
import java.util.*
import javax.annotation.Nonnull
import javax.servlet.ServletContext
import kotlin.system.measureTimeMillis

class FileCumulator private constructor() : DbManager<FileStoreModel>, DatabaseQueryRunner {
    val sensorModelsMap = mapOf(
        ActivFitSensorData.FILE_NAME to ActivFitSensorData(),
        ActivitySensorData.FILE_NAME to ActivitySensorData(),
        BatterySensorData.FILE_NAME to BatterySensorData(),
        BluetoothSensorData.FILE_NAME to BluetoothSensorData(),
        HeartRateSensorData.FILE_NAME to HeartRateSensorData(),
        LightSensorData.FILE_NAME to LightSensorData(),
        ScreenUsageSensorData.FILE_NAME to ScreenUsageSensorData(),
    ) // <File Name, Sensor Model>

    override fun init(servletContext: ServletContext?) {
        miscFile = createEmptyFile(
            BASE_ADDRESS + MISC_FILE_NAME + FileSystems.getDefault().separator,
            DATA_FILE_NAME
        )
        sensorModelsMap
            .values
            .forEach { fileStoreModel: FileStoreModel ->
                fileStoreModel.file = createEmptyFile(
                    BASE_ADDRESS
                            + fileStoreModel.fileName
                            + FileSystems.getDefault().separator,
                    DATA_FILE_NAME
                )
            }
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<FileStoreModel>) {
        println("No need to store in the file system again.")
    }

    override fun insertSensorData(sensorData: FileStoreModel) {
        println("No need to store in the file system again.")
    }

    override fun queryForRunningEvent(date: String): List<ActivFitSensorData> {
        val userDate = Date(date)
        val tomorrow = addDayToDate(userDate, 1) //        get the next Day Date as well
        //        fetch all record from the collection
        val allSensorData = getSensorFileContents<ActivFitSensorData>(
            sensorModelsMap[ActivFitSensorData.FILE_NAME]!!, 1000
        )

        return allSensorData.filter {
            val sensorDataStartTime = Date(it.timestamp?.startTime)
            (isWithinDateRange(userDate, tomorrow, sensorDataStartTime)
                    && shouldBeRunningAndNotUnknown(
                it.sensorData?.activity ?: ""
            ))
        }
    }

    override fun queryHeartRatesForDay(date: String): Int {
        return heartRateSensorFileContents.filter { it.formattedDate == date }.count()
    }

    override fun queryForTotalStepsInDay(date: String): Int {
        val activitySensorDataList = getSensorFileContents<ActivitySensorData>(
            sensorModelsMap[ActivitySensorData.FILE_NAME]!!, 1000
        )
        return activitySensorDataList
            .filter { sensorData: ActivitySensorData -> sensorData.formattedDate == date }
            .maxByOrNull { sensorData: ActivitySensorData -> sensorData.sensorData.stepCounts }
            ?.sensorData
            ?.stepCounts ?: 0
    }

    /**
     * Use to determine the category of sensor for the given file and returns the cumulative data txt
     * file.
     *
     * @param inputFile given file
     * @return the cumulative data file
     */
    fun determineFileCategoryAndGet(inputFile: File): File {
        for (fileName in sensorModelsMap.keys) {
            if (inputFile.path.contains(fileName)) {
                return sensorModelsMap[fileName]!!.file!!
            }
        }
        return miscFile!!
    }

    private val heartRateSensorFileContents: List<HeartRateSensorData>
        get() {
            val g = Gson()
            return getFileContentsLineByLine(
                sensorModelsMap[HeartRateSensorData.FILE_NAME]
                    ?.file!!
            ).mapNotNull {
                try {
                    //                converts JSON string into POJO
                    val heartRateSensorData = g.fromJson(it, HeartRateSensorData::class.java).apply {
                        setFormattedDate()
                    }
                    heartRateSensorData
                } catch (e: Exception) {
                    //                e.printStackTrace();
                    //                System.out.println("Incorrect JSON format");    // don't store data in
                    // mongodb
                    null
                }
            }
        }

    /**
     * Call to calculate brute force search time for running activity in given number of days.
     *
     * @param numOfDays given number of days
     * @return time taken to search the data through brute force
     */
    fun searchForRunningActivity(numOfDays: Int): Long {
        val timeTaken = measureTimeMillis {
            for (sensorData in getSensorFileContents<ActivFitSensorData>(
                sensorModelsMap[ActivFitSensorData.FILE_NAME] as ActivFitSensorData,
                numOfDays
            )) { // iterate all sensordata and find the result
                if (sensorData.sensorData?.activity.equals("running", ignoreCase = true)) {
                    println("Running event found " + sensorData.timestamp?.startTime)
                }
            }
        }
        println("Brute force took ${timeTaken}ms for running")
        return timeTaken
    }

    /**
     * Call to search for less bright data in given number of days and calculate brute force search
     * time.
     *
     * @param numOfDays given number of days
     */
    fun searchForLessBrightData(numOfDays: Int): Long {
        val timeTaken = measureTimeMillis {
            for (sensorData in getSensorFileContents<LightSensorData>(
                sensorModelsMap[LightSensorData.FILE_NAME] as LightSensorData,
                numOfDays
            )) { // iterate all sensordata and find the result
                if (sensorData.luxValue.equals("less bright", ignoreCase = true)) {
                    //                System.out.println("less bright found " + sensorData.getTimestamp());
                }
            }
        }
        //        search complete, calculate search time
        println("Brute force took ${timeTaken}ms for less bright")
        return timeTaken
    }

    /**
     * Call to search for 100 bpm data in given number of days and calculate brute force search time.
     *
     * @param numOfDays given number of days
     */
    fun searchForHundredBpm(numOfDays: Int): Long {
        val timeTaken = measureTimeMillis {
            for (sensorData in heartRateSensorFileContents) { // iterate all sensordata and find the result
                if (sensorData.sensorData?.bpm == 100) {
                    //                System.out.println("100 bpm found " + sensorData.getTimestamp());
                }
            }
        }
        //        search complete, calculate search time
        println("Brute force took ${timeTaken}ms for 100 bpm")
        return timeTaken
    }

    companion object {
        private const val MISC_FILE_NAME = "Misc"
        private val BASE_ADDRESS = "Result" + FileSystems.getDefault().separator
        private const val DATA_FILE_NAME = "CumulativeData.txt"
        var miscFile: File? = null

        /**
         * Get singleton instance.
         *
         * @return instance
         */
        @JvmStatic
        var instance: FileCumulator? = null
            get() {
                if (field == null) {
                    field = FileCumulator()
                }
                return field
            }
            private set

        fun <T : FileStoreModel> getSensorFileContents(sensorModel: FileStoreModel, numOfDays: Int): List<T> {
            var numOfDays = numOfDays
            val sensorDataList: MutableList<T> = mutableListOf() // holds the sensor data
            val fileContents = getFileContentsLineByLine(
                sensorModel.file!!
            ) // holds all lines of the cumulativeFile for the sensor
            var currentDate = "" // will hold the value of current date
            val g = Gson()
            for (fileLine in fileContents) {
                try {
                    //                converts JSON string into POJO
                    val sensorData: T = g.fromJson(fileLine, sensorModel.javaClass as Type)
                    sensorData.setFormattedDate()
                    //                get date of current sensor data to compare
                    val sensorFormattedDate = Date(sensorData.startTime).formatted()
                    if (sensorFormattedDate == currentDate) {
                        //                    add sensor data to list as date is same as current date
                        sensorDataList.add(sensorData)
                    } else {
                        currentDate = sensorFormattedDate // update current date
                        numOfDays-- // decrement num of days left
                        if (numOfDays == -1) {
                            //                        found data for the specified number of days so return the
                            // sensor data list
                            return sensorDataList
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
            return sensorDataList
        }
    }

    init {
        createDirectory(BASE_ADDRESS)
        init(null)
    }
}