package utils

import sensormodels.activfit.ActivFitSensorData
import utils.WebAppConstants.NO_HEART_RATE_DATA
import java.text.ParseException
import java.util.*
import java.util.regex.Pattern
import javax.annotation.Nonnull

object QueryUtils {
    const val RUNNING_EVENT_REGEX = "([rR][ua]n(ning)?)"
    const val STEP_COUNT_EVENT_REGEX = "([sS]teps*|[wW]alk(ed)?)"
    const val HEART_RATE_EVENT_REGEX = "([hH]eart([rR]ate)?)"
    private const val DATE_REGEX = "(\\d{2}[-/]\\d{2}[-/]\\d{4})" //  Regex for Date input

    @JvmStatic
    fun extractDateFromQuery(@Nonnull query: String): Date? {
        if (query.isEmpty()) {
            return null
        }
        if (query.contains("today")) {
            val today = Calendar.getInstance()
            today[Calendar.HOUR_OF_DAY] = 0
            today[Calendar.MINUTE] = 0
            today[Calendar.MILLISECOND] = 0
            return today.time
        }
        if (query.contains("yesterday")) {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.add(Calendar.DATE, -1)
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.time
        }
        val dateMatcher = Pattern.compile(DATE_REGEX).matcher(query)
        if (dateMatcher.find()) {
            println("Found date: " + dateMatcher.group(1))
            try {
                return WebAppConstants.inputDateFormat.parse(dateMatcher.group(1).replace("-".toRegex(), "/"))
            } catch (e: ParseException) {
                e.printStackTrace()
                println("Date not parsed")
            }
        }
        return null
    }

    /**
     * Call to determine the category of the given query and get the callback for the same.
     *
     * @param query given query
     */
    @JvmStatic
    fun determineQueryType(@Nonnull query: String?): QueryType {
        return if (isMatching(RUNNING_EVENT_REGEX, query)) {
            QueryType.RUNNING
        } else if (isMatching(STEP_COUNT_EVENT_REGEX, query)) {
            QueryType.STEP_COUNT
        } else if (isMatching(HEART_RATE_EVENT_REGEX, query)) {
            QueryType.HEART_RATE
        } else {
            QueryType.UNKNOWN
        }
    }

    /**
     * Call to check if the given query matches with given pattern.
     *
     * @param pattern given pattern
     * @param query given query
     * @return `true` if the pattern matches, else `false`
     */
    @JvmStatic
    fun isMatching(pattern: String?, query: String?): Boolean {
        val m = Pattern.compile(pattern).matcher(query)
        return m.find()
    }

    /**
     * Use to add given number of days to the given Date.
     *
     * @param userDate given Date
     * @param days given number of days
     * @return Date after adding given number of days
     */
    @JvmStatic
    fun addDayToDate(userDate: Date?, days: Int): Date {
        val cal = Calendar.getInstance() // get Calendar Instance
        cal.time = userDate // set Time to the given Date@param
        cal.add(Calendar.DATE, days) // add given number of days@param to the given Date@param
        return cal.time // return the new Date
    }

    /**
     * Use to print the Query result data for running activity on the given Date.
     *
     * @param queryResult the given Result from the Query
     */
    @JvmStatic
    fun getFormattedRunningResultData(queryResult: ArrayList<ActivFitSensorData>): String {
        return if (queryResult.isEmpty()) {
            "No, there is no running activity."
        } else {
            val builder = StringBuilder()
            builder.append("Yes, you ran")
            for (data in queryResult) {
                builder
                    .append(" from ")
                    .append(
                        WebAppConstants.outputDateFormat.format(
                            Date(data.timestamp?.startTime)
                        )
                    )
                    .append(" to ")
                    .append(
                        WebAppConstants.outputDateFormat.format(Date(data.timestamp?.endTime))
                    )
                    .append(", ")
                println(
                    "Yes, you ran from "
                            + data.timestamp?.startTime
                            + " to "
                            + data.timestamp?.endTime
                )
            }
            builder.deleteCharAt(builder.length - 1)
            builder.deleteCharAt(builder.length - 1)
            builder.toString()
        }
    }

    /**
     * Use to print the Query result from counting the total steps of the day.
     *
     * @param stepCount given step count
     * @param userDate given Date of the step count
     */
    @JvmStatic
    fun getFormattedTotalStepsForTheDay(stepCount: Int, userDate: Date?): String {
        return if (stepCount == -1) {
            "No steps record found for the day"
        } else {
            ("You walked "
                    + stepCount
                    + " steps on "
                    + WebAppConstants.inputDateFormat.format(userDate))
        }
    }

    /**
     * Call to get formatted output for HeartRates for the days.
     *
     * @param date date
     * @param heartRateCount total heart rate count
     */
    @JvmStatic
    fun getFormattedHeartRatesForTheDays(date: Date?, heartRateCount: Int): String {
        return if (heartRateCount == 0) {
            NO_HEART_RATE_DATA
        } else {
            val formattedDate = WebAppConstants.inputDateFormat.format(date)
            String.format(
                "You received %s heart rate notifications on %s.", heartRateCount, formattedDate
            )
        }
    }

    enum class QueryType {
        RUNNING, HEART_RATE, STEP_COUNT, UNKNOWN
    }
}