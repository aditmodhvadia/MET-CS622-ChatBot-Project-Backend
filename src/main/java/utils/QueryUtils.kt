package utils

import sensormodels.activfit.ActivFitSensorData
import utils.WebAppConstants.NO_HEART_RATE_DATA
import utils.WebAppConstants.formatted
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
        val dateMatcher = Pattern.compile(DATE_REGEX).matcher(query)
        return when {
            query.isEmpty() -> {
                null
            }
            query.contains("today") -> {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            }
            query.contains("yesterday") -> {
                Calendar.getInstance(Locale.getDefault()).apply {
                    add(Calendar.DATE, -1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            }
            dateMatcher.find() -> {
                println("Found date: " + dateMatcher.group(1))
                try {
                    Date(Date(dateMatcher.group(1).replace("-".toRegex(), "/")).formatted())
                } catch (e: ParseException) {
                    e.printStackTrace()
                    println("Date not parsed")
                    null
                }
            }
            else -> null
        }
    }

    /**
     * Call to determine the category of the given query and get the callback for the same.
     *
     * @param query given query
     */
    @JvmStatic
    fun determineQueryType(query: String): QueryType {
        return when {
            isMatching(RUNNING_EVENT_REGEX, query) -> {
                QueryType.RUNNING
            }
            isMatching(STEP_COUNT_EVENT_REGEX, query) -> {
                QueryType.STEP_COUNT
            }
            isMatching(HEART_RATE_EVENT_REGEX, query) -> {
                QueryType.HEART_RATE
            }
            else -> {
                QueryType.UNKNOWN
            }
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
    fun isMatching(pattern: String, query: String): Boolean = Pattern.compile(pattern).matcher(query).find()

    /**
     * Use to add given number of days to the given Date.
     *
     * @param userDate given Date
     * @param days given number of days
     * @return Date after adding given number of days
     */
    @JvmStatic
    fun addDayToDate(userDate: Date, days: Int): Date = Calendar.getInstance().apply {
        time = userDate
        add(Calendar.DATE, days)
    }.time

    /**
     * Use to print the Query result data for running activity on the given Date.
     *
     * @param queryResult the given Result from the Query
     */
    @JvmStatic
    fun getFormattedRunningResultData(queryResult: List<ActivFitSensorData>): String {
        return if (queryResult.isEmpty()) {
            "No, there is no running activity."
        } else {
            val builder = StringBuilder("Yes, you ran")
            queryResult.joinToString {
                "from ${
                    WebAppConstants.outputDateFormat.format(Date(it.timestamp?.startTime))
                } to ${WebAppConstants.outputDateFormat.format(Date(it.timestamp?.endTime))}"
            }
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
    fun getFormattedTotalStepsForTheDay(stepCount: Int, userDate: String): String {
        return if (stepCount == -1) {
            "No steps record found for the day"
        } else {
            "You walked $stepCount steps on $userDate"
        }
    }

    /**
     * Call to get formatted output for HeartRates for the days.
     *
     * @param date date
     * @param heartRateCount total heart rate count
     */
    @JvmStatic
    fun getFormattedHeartRatesForTheDays(date: String, heartRateCount: Int): String {
        return if (heartRateCount == 0) {
            NO_HEART_RATE_DATA
        } else {
            "You received $heartRateCount heart rate notifications on $date."
        }
    }

    enum class QueryType {
        RUNNING, HEART_RATE, STEP_COUNT, UNKNOWN
    }
}