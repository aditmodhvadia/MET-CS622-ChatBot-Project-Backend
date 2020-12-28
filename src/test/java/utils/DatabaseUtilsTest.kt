package utils

import org.junit.Assert
import org.junit.Test
import utils.DatabaseUtils.isWithinDateRange
import utils.DatabaseUtils.shouldBeRunningAndNotUnknown
import java.util.*

class DatabaseUtilsTest {
    @Test
    fun dateShouldBeWithinRange() {
        val today = Date("11/28/2020")
        val yesterday = Date("11/27/2020")
        val tomorrow = Date("11/29/2020")
        Assert.assertTrue(isWithinDateRange(yesterday, tomorrow, today))
        Assert.assertFalse(isWithinDateRange(yesterday, today, tomorrow))
    }

    @Test
    fun shouldBeRunningAndNotUnknown() {
        Assert.assertTrue(shouldBeRunningAndNotUnknown("running"))
        Assert.assertTrue(shouldBeRunningAndNotUnknown("RUNNING"))
        Assert.assertFalse(shouldBeRunningAndNotUnknown("unknown"))
        Assert.assertFalse(shouldBeRunningAndNotUnknown("walking"))
    }
}