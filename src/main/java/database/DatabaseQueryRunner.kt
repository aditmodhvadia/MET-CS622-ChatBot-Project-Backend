package database

import sensormodels.activfit.ActivFitSensorData
import java.util.*

interface DatabaseQueryRunner {
    /**
     * Get sensor data with running event for the given date.
     *
     * @param date given date
     * @return collection of sensor data with running event
     */
    fun queryForRunningEvent(date: Date): List<ActivFitSensorData>

    /**
     * Number of steps taken by the user on the given date.
     *
     * @param date given date
     * @return total number of steps in a day
     */
    fun queryForTotalStepsInDay(date: Date): Int

    /**
     * Get the number of times the user was notified about their heart rate information for the given
     * date.
     *
     * @param date given date
     * @return number of notifications received by the user for their heart rate in a day.
     */
    fun queryHeartRatesForDay(date: Date): Int
}