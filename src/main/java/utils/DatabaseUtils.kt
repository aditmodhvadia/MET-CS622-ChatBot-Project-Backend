package utils

import java.util.*
import javax.annotation.Nonnull

object DatabaseUtils {
    /**
     * Determine given date lies between start date and end date
     *
     * @param startDate start date
     * @param endDate end date
     * @param sensorDate given date
     * @return `true` if within the date range, else `false`
     */
    @JvmStatic
    fun isWithinDateRange(
        startDate: Date?, endDate: Date?, sensorDate: Date
    ): Boolean = sensorDate.after(startDate) && sensorDate.before(endDate)

    /**
     * Determine if given activity matches with running activity
     *
     * @param activity activity
     * @return `true` if activity matches `running`, else `false`
     */
    @JvmStatic
    fun shouldBeRunningAndNotUnknown(@Nonnull activity: String): Boolean =
        activity != "unknown" && activity.toLowerCase() == "running"
}