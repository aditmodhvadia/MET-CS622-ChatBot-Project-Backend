package utils;

import static utils.WebAppConstants.NO_HEART_RATE_DATA;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sensormodels.activfit.ActivFitSensorData;

public class QueryUtils {
  public static final String RUNNING_EVENT_REGEX = "([rR][ua]n(ning)?)";
  public static final String STEP_COUNT_EVENT_REGEX = "([sS]teps*|[wW]alk(ed)?)";
  public static final String HEART_RATE_EVENT_REGEX = "([hH]eart([rR]ate)?)";
  public static final String DATE_REGEX = "(\\d{2}[-/]\\d{2}[-/]\\d{4})"; //  Regex for Date input

  @Nullable
  public static Date extractDateFromQuery(@Nonnull String query) {
    if (query.isEmpty()) {
      return null;
    }
    if (query.contains("today")) {
      Calendar today = Calendar.getInstance();
      today.set(Calendar.HOUR_OF_DAY, 0);
      today.set(Calendar.MINUTE, 0);
      today.set(Calendar.MILLISECOND, 0);
      return today.getTime();
    }
    if (query.contains("yesterday")) {
      Calendar calendar = Calendar.getInstance(Locale.getDefault());
      calendar.add(Calendar.DATE, -1);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      return calendar.getTime();
    }
    Matcher dateMatcher = Pattern.compile(DATE_REGEX).matcher(query);
    if (dateMatcher.find()) {
      System.out.println("Found date: " + dateMatcher.group(1));
      try {
        return WebAppConstants.inputDateFormat.parse(dateMatcher.group(1).replaceAll("-", "/"));
      } catch (ParseException e) {
        e.printStackTrace();
        System.out.println("Date not parsed");
      }
    }

    return null;
  }

  public enum QueryType {
    RUNNING,
    HEART_RATE,
    STEP_COUNT,
    UNKNOWN
  }

  /**
   * Call to determine the category of the given query and get the callback for the same.
   *
   * @param query given query
   */
  public static QueryType determineQueryType(@Nonnull String query) {
    if (isMatching(RUNNING_EVENT_REGEX, query)) {
      return QueryType.RUNNING;
    } else if (isMatching(STEP_COUNT_EVENT_REGEX, query)) {
      return QueryType.STEP_COUNT;
    } else if (isMatching(HEART_RATE_EVENT_REGEX, query)) {
      return QueryType.HEART_RATE;
    } else {
      return QueryType.UNKNOWN;
    }
  }

  /**
   * Call to check if the given query matches with given pattern.
   *
   * @param pattern given pattern
   * @param query given query
   * @return <code>true</code> if the pattern matches, else <code>false</code>
   */
  public static boolean isMatching(String pattern, String query) {
    Matcher m = Pattern.compile(pattern).matcher(query);
    return m.find();
  }

  /**
   * Use to add given number of days to the given Date.
   *
   * @param userDate given Date
   * @param days given number of days
   * @return Date after adding given number of days
   */
  public static Date addDayToDate(Date userDate, int days) {
    Calendar cal = Calendar.getInstance(); // get Calendar Instance
    cal.setTime(userDate); // set Time to the given Date@param
    cal.add(Calendar.DATE, days); // add given number of days@param to the given Date@param
    return cal.getTime(); // return the new Date
  }

  /**
   * Use to print the Query result data for running activity on the given Date.
   *
   * @param queryResult the given Result from the Query
   */
  public static String getFormattedRunningResultData(ArrayList<ActivFitSensorData> queryResult) {
    if (queryResult.isEmpty()) {
      return "No, there is no running activity.";
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append("Yes, you ran");
      for (ActivFitSensorData data : queryResult) {
        builder
            .append(" from ")
            .append(
                WebAppConstants.outputDateFormat.format(
                    new Date(data.getTimestamp().getStartTime())))
            .append(" to ")
            .append(
                WebAppConstants.outputDateFormat.format(new Date(data.getTimestamp().getEndTime())))
            .append(", ");
        System.out.println(
            "Yes, you ran from "
                + data.getTimestamp().getStartTime()
                + " to "
                + data.getTimestamp().getEndTime());
      }
      builder.deleteCharAt(builder.length() - 1);
      builder.deleteCharAt(builder.length() - 1);
      return builder.toString();
    }
  }

  /**
   * Use to print the Query result from counting the total steps of the day.
   *
   * @param stepCount given step count
   * @param userDate given Date of the step count
   */
  public static String getFormattedTotalStepsForTheDay(int stepCount, Date userDate) {
    if (stepCount == -1) {
      return "No steps record found for the day";
    } else {
      return "You walked "
          + stepCount
          + " steps on "
          + WebAppConstants.inputDateFormat.format(userDate);
    }
  }

  /**
   * Call to get formatted output for HeartRates for the days.
   *
   * @param date date
   * @param heartRateCount total heart rate count
   */
  public static String getFormattedHeartRatesForTheDays(Date date, int heartRateCount) {
    if (heartRateCount == 0) {
      return NO_HEART_RATE_DATA;
    } else {
      String formattedDate = WebAppConstants.inputDateFormat.format(date);
      return String.format(
          "You received %s heart rate notifications on %s.", heartRateCount, formattedDate);
    }
  }
}
