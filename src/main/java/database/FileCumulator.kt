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
import utils.WebAppConstants
import java.io.File
import java.lang.reflect.Type
import java.nio.file.FileSystems
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.servlet.ServletContext
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis

class FileCumulator private constructor() : DbManager<FileStoreModel?>, DatabaseQueryRunner {
    val sensorModelsMap = HashMap<String, FileStoreModel>() // <File Name, Sensor Model>
    override fun init(servletContext: ServletContext?) {
        miscFile = createEmptyFile(
            BASE_ADDRESS + MISC_FILE_NAME + FileSystems.getDefault().separator,
            DATA_FILE_NAME
        )
        sensorModelsMap[ActivFitSensorData.FILE_NAME] = ActivFitSensorData()
        sensorModelsMap[ActivitySensorData.FILE_NAME] = ActivitySensorData()
        sensorModelsMap[BatterySensorData.FILE_NAME] = BatterySensorData()
        sensorModelsMap[BluetoothSensorData.FILE_NAME] = BluetoothSensorData()
        sensorModelsMap[HeartRateSensorData.FILE_NAME] = HeartRateSensorData()
        sensorModelsMap[LightSensorData.FILE_NAME] = LightSensorData()
        sensorModelsMap[ScreenUsageSensorData.FILE_NAME] = ScreenUsageSensorData()
        sensorModelsMap
            .values
            .forEach(
                Consumer { fileStoreModel: FileStoreModel ->
                    fileStoreModel.file = createEmptyFile(
                        BASE_ADDRESS
                                + fileStoreModel.fileName
                                + FileSystems.getDefault().separator,
                        DATA_FILE_NAME
                    )
                })
    }

    override fun insertSensorDataList(@Nonnull sensorDataList: List<FileStoreModel?>) {
        println("No need to store in the file system again.")
    }

    override fun insertSensorData(sensorData: FileStoreModel?) {
        println("No need to store in the file system again.")
    }

    override fun queryForRunningEvent(date: Date?): ArrayList<ActivFitSensorData?>? {
        val tomorrow = addDayToDate(date, 1) //        get the next Day Date as well
        //        fetch all record from the collection
        val allSensorData = getSensorFileContents<ActivFitSensorData>(
            sensorModelsMap[ActivFitSensorData.FILE_NAME], 1000
        )
        return allSensorData.stream()
            .filter { sensorData: ActivFitSensorData ->
                val sensorDataStartTime = Date(sensorData.timestamp?.startTime)
                (isWithinDateRange(date, tomorrow, sensorDataStartTime)
                        && shouldBeRunningAndNotUnknown(
                    sensorData.sensorData?.activity ?: ""
                ))
            }
            .collect(Collectors.toCollection { ArrayList() })
    }

    override fun queryHeartRatesForDay(date: Date?): Int {
        val sensorDataList = heartRateSensorFileContents
        val formattedDate = WebAppConstants.inputDateFormat.format(date)
        return sensorDataList.stream()
            .filter { sensorData: HeartRateSensorData -> sensorData.formattedDate == formattedDate }
            .count().toInt()
    }

    override fun queryForTotalStepsInDay(date: Date?): Int {
        val activitySensorDataList = getSensorFileContents<ActivitySensorData>(
            sensorModelsMap[ActivitySensorData.FILE_NAME], 1000
        )
        val userFormattedDate = WebAppConstants.inputDateFormat.format(date)
        return activitySensorDataList
            .filter { sensorData: ActivitySensorData -> sensorData.formattedDate == userFormattedDate }
            .maxByOrNull { sensorData: ActivitySensorData -> sensorData.sensorData?.stepCounts ?: 0 }
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
    fun determineFileCategoryAndGet(inputFile: File): File? {
        for (fileName in sensorModelsMap.keys) {
            if (inputFile.path.contains(fileName)) {
                return sensorModelsMap[fileName]!!.file
            }
        }
        return miscFile
    }

    private val heartRateSensorFileContents: List<HeartRateSensorData>
        get() {
            val sensorDataList: MutableList<HeartRateSensorData> = java.util.ArrayList() // holds the sensor data
            val fileContents = getFileContentsLineByLine(
                sensorModelsMap[HeartRateSensorData.FILE_NAME]
                    ?.file
            ) // holds all lines of the cumulativeFile for the sensor
            // will hold the value of current date
            for (fileLine in fileContents) {
                val g = Gson()
                try {
                    //                converts JSON string into POJO
                    val heartRateSensorData = g.fromJson(fileLine, HeartRateSensorData::class.java)
                    heartRateSensorData.setFormattedDate()
                    sensorDataList.add(heartRateSensorData)
                } catch (e: Exception) {
                    //                e.printStackTrace();
                    //                System.out.println("Incorrect JSON format");    // don't store data in
                    // mongodb
                }
            }
            return sensorDataList
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
                sensorModelsMap[ActivFitSensorData.FILE_NAME] as ActivFitSensorData?,
                numOfDays
            )) { // iterate all sensordata and find the result
                if (sensorData.sensorData?.activity.equals("running", ignoreCase = true)) {
                    println("Running event found " + sensorData.timestamp?.startTime)
                }
            }
        }
        println("Brute force took " + timeTaken + "ms for running")
        return timeTaken
    }

    /**
     * Call to search for less bright data in given number of days and calculate brute force search
     * time.
     *
     * @param numOfDays given number of days
     */
    fun searchForLessBrightData(numOfDays: Int): Long {
        var searchTime = System.currentTimeMillis() // used to calculate search time for brute force
        for (sensorData in getSensorFileContents<LightSensorData>(
            sensorModelsMap[LightSensorData.FILE_NAME] as LightSensorData?,
            numOfDays
        )) { // iterate all sensordata and find the result
            if (sensorData.luxValue.equals("less bright", ignoreCase = true)) {
                //                System.out.println("less bright found " + sensorData.getTimestamp());
            }
        }
        //        search complete, calculate search time
        searchTime = System.currentTimeMillis() - searchTime
        println("Brute force took " + searchTime + "ms for less bright")
        return searchTime
    }

    /**
     * Call to search for 100 bpm data in given number of days and calculate brute force search time.
     *
     * @param numOfDays given number of days
     */
    fun searchForHundredBpm(numOfDays: Int): Long {
        var searchTime = System.currentTimeMillis() // used to calculate search time for brute force
        for (sensorData in heartRateSensorFileContents) { // iterate all sensordata and find the result
            if (sensorData.sensorData?.bpm == 100) {
                //                System.out.println("100 bpm found " + sensorData.getTimestamp());
            }
        }
        //        search complete, calculate search time
        searchTime = System.currentTimeMillis() - searchTime
        println("Brute force took " + searchTime + "ms for 100 bpm")
        return searchTime
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

        fun <T : FileStoreModel> getSensorFileContents(sensorModel: FileStoreModel?, numOfDays: Int): List<T> {
            var numOfDays = numOfDays
            val sensorDataList: MutableList<T> = java.util.ArrayList() // holds the sensor data
            val fileContents = getFileContentsLineByLine(
                sensorModel!!.file
            ) // holds all lines of the cumulativeFile for the sensor
            var currentDate = "" // will hold the value of current date
            val g = Gson()
            for (fileLine in fileContents) {
                try {
                    //                converts JSON string into POJO
                    val sensorData: T = g.fromJson(fileLine, sensorModel.javaClass as Type)
                    sensorData.setFormattedDate()
                    //                get date of current sensor data to compare
                    val sensorFormattedDate = getFormattedDateFromTimeStamp(
                        sensorData.startTime
                    )
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

        private fun getFormattedDateFromTimeStamp(timestamp: String?): String {
            return WebAppConstants.inputDateFormat.format(Date(timestamp))
        }
    }

    init {
        createDirectory(BASE_ADDRESS)
        init(null)
    }
}