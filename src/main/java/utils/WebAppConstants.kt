package utils

import java.text.SimpleDateFormat

object WebAppConstants {
    private const val DATE_FORMAT = "MM/dd/yyyy"

    @JvmField
    val inputDateFormat = SimpleDateFormat(DATE_FORMAT)
    private const val OUTPUT_DATE_FORMAT = "hh:MM a"

    @JvmField
    val outputDateFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT)
    const val NO_HEART_RATE_DATA = "No data found or some error occurred."
}