package utils;

import java.text.SimpleDateFormat;

public class WebAppConstants {
  private static final String DATE_FORMAT = "MM/dd/yyyy";
  public static final SimpleDateFormat inputDateFormat = new SimpleDateFormat(DATE_FORMAT);
  private static final String OUTPUT_DATE_FORMAT = "hh:MM a";
  public static final SimpleDateFormat outputDateFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT);

  public static final String NO_HEART_RATE_DATA = "No data found or some error occurred.";
}
