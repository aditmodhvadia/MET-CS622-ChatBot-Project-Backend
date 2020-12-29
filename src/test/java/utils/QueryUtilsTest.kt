package utils

import org.junit.Assert
import org.junit.Test
import utils.QueryUtils.determineQueryType
import utils.QueryUtils.extractDateFromQuery
import utils.QueryUtils.getFormattedHeartRatesForTheDays
import utils.QueryUtils.isMatching
import utils.WebAppConstants.NO_HEART_RATE_DATA
import java.util.*

class QueryUtilsTest {
    @Test
    fun isRunningEventMatching() {
        Assert.assertTrue(isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I run today?"))
        Assert.assertTrue(isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I ran yesterday?"))
        Assert.assertTrue(isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I go running today?"))
        Assert.assertTrue(
            isMatching(QueryUtils.RUNNING_EVENT_REGEX, "How much did I run yesterday?")
        )
        Assert.assertTrue(isMatching(QueryUtils.RUNNING_EVENT_REGEX, "Running yesterday?"))
        Assert.assertTrue(isMatching(QueryUtils.RUNNING_EVENT_REGEX, "Run was fun yesterday!"))
    }

    @Test
    fun stepCountEventMatching() {
        Assert.assertTrue(isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How many steps?"))
        Assert.assertTrue(isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How many stepsss?"))
        Assert.assertTrue(
            isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Did I take a step today?")
        )
        Assert.assertTrue(isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Did I walk today?"))
        Assert.assertTrue(isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Walk today?"))
        Assert.assertTrue(isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How much I walked?"))
    }

    @Test
    fun heartRateEventRegex() {
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heartrate"))
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "HeartRate"))
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart rate"))
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart Rate"))
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart"))
        Assert.assertTrue(isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "heart"))
    }

    @Test
    fun extractsDateFromQuery() {
        val today = Calendar.getInstance()
        today[Calendar.HOUR_OF_DAY] = 0
        today[Calendar.MINUTE] = 0
        today[Calendar.MILLISECOND] = 0
        Assert.assertEquals(today.time.date.toLong(), extractDateFromQuery("today")!!.date.toLong())
        today.add(Calendar.DATE, -1)
        Assert.assertEquals(
            today.time.date.toLong(),
            Objects.requireNonNull(extractDateFromQuery("yesterday"))?.date?.toLong()
        )
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar[Calendar.YEAR] = 2018
        calendar[Calendar.MONTH] = 9
        calendar[Calendar.DAY_OF_MONTH] = 12
        Assert.assertEquals(
            calendar.time.date.toLong(),
            Objects.requireNonNull(extractDateFromQuery("10-12-2018"))?.date?.toLong()
        )
    }

    @Test
    fun determineQueryType() {
        Assert.assertEquals(
            QueryUtils.QueryType.HEART_RATE, determineQueryType("what was my heartrate")
        )
        Assert.assertEquals(QueryUtils.QueryType.STEP_COUNT, determineQueryType("how many steps?"))
        Assert.assertEquals(
            QueryUtils.QueryType.RUNNING, determineQueryType("how much did I run today?")
        )
        Assert.assertEquals(
            QueryUtils.QueryType.UNKNOWN, determineQueryType("what was my temperature?")
        )
    }

    @Test
    fun formattedHeartRateCountResult() {
        //    No heart rate data found
        Assert.assertEquals(getFormattedHeartRatesForTheDays(Date(), 0), NO_HEART_RATE_DATA)
        Assert.assertEquals(
            getFormattedHeartRatesForTheDays(Date("11/25/2020"), 10),
            "You received 10 heart rate notifications on 11/25/2020."
        )
        Assert.assertEquals(
            getFormattedHeartRatesForTheDays(Date("11/25/2020"), 1997),
            "You received 1997 heart rate notifications on 11/25/2020."
        )
        Assert.assertEquals(
            getFormattedHeartRatesForTheDays(Date("11/25/2020"), 1),
            "You received 1 heart rate notifications on 11/25/2020."
        )
    }
}