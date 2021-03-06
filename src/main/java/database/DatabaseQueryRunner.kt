package database

import sensormodels.activfit.ActivFitSensorData

interface DatabaseQueryRunner {
    /**
     * Get sensor data with running event for the given date.
     *
     * @param date given date
     * @return collection of sensor data with running event
     */
    fun queryForRunningEvent(date: String): List<ActivFitSensorData>

    /**
     * Number of steps taken by the user on the given date.
     *
     * @param date given date
     * @return total number of steps in a day
     */
    fun queryForTotalStepsInDay(date: String): Int

    /**
     * Get the number of times the user was notified about their heart rate information for the given
     * date.
     *
     * @param date given formatted date
     * @return number of notifications received by the user for their heart rate in a day.
     */
    fun queryHeartRatesForDay(date: String): Int
}