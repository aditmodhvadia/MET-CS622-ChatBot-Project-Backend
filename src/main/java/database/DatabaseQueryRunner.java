package database;

import sensormodels.ActivFitSensorData;

import java.util.ArrayList;
import java.util.Date;

public interface DatabaseQueryRunner {

    /**
     * Get sensor data with running event for the given date.
     *
     * @param date given date
     * @return collection of sensor data with running event
     */
    ArrayList<ActivFitSensorData> queryForRunningEvent(Date date);

    /**
     * Number of steps taken by the user on the given date.
     *
     * @param date given date
     * @return total number of steps in a day
     */
    int queryForTotalStepsInDay(Date date);

    /**
     * Get the number of times the user was notified about their heart rate information for the given date.
     *
     * @param date given date
     * @return number of notifications received by the user for their heart rate in a day.
     */
    int queryHeartRatesForDay(Date date);
}
