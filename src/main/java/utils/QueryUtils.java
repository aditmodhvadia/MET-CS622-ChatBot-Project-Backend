package utils;

import sensormodels.ActivFitSensorData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {
  public static final String RUNNING_EVENT_REGEX = "([rR]un|[rR]unning)";
  public static final String STEP_COUNT_EVENT_REGEX = "([sS]teps*|[wW]alk)";
  public static final String HEART_RATE_EVENT_REGEX = "([hH]eart|[hH]eartrate)";
  public static final String DATE_REGEX = "(\\d{2}[-,/]\\d{2}[-,/]\\d{4})"; //  Regex for Date input

  /**
   * Call to determine the category of the given query and get the callback for the same
   *
   * @param query given query
   * @param callback callback for the query type
   */
  public static void determineQueryType(String query, OnQueryResolvedCallback callback) {
    Matcher m = Pattern.compile(DATE_REGEX).matcher(query);
    Date date;
    if (m.find()) { //  find if user entered date in the query
      try {
        date =
            WebAppConstants.inputDateFormat.parse(
                m.group(1)); //  get the first recognised date from query

        if (isMatching(RUNNING_EVENT_REGEX, query)) {
          callback.onDisplayRunningEventSelected(date);
        } else if (isMatching(STEP_COUNT_EVENT_REGEX, query)) {
          callback.onDisplayTotalStepsInDayEventSelected(date);
        } else if (isMatching(HEART_RATE_EVENT_REGEX, query)) {
          callback.onDisplayHeartRateEventSelected(date);
        } else {
          callback.onNoEventResolved();
        }
      } catch (ParseException e) {
        e.printStackTrace();
        callback.onDateNotParsed();
      }
    } else { //  Bad input
      callback.onDateNotParsed();
    }
  }

  /**
   * Call to check if the given query matches with given pattern
   *
   * @param pattern given pattern
   * @param query given query
   * @return <code>true</code> if the pattern matches, else <code>false</code>
   */
  private static boolean isMatching(String pattern, String query) {
    Matcher m = Pattern.compile(pattern).matcher(query);
    return m.find();
  }

  /**
   * Use to add given number of days to the given Date
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
   * Use to print the Query result data for running activity on the given Date
   *
   * @param queryResult the given Result from the Query
   */
  public static String getFormattedRunningResultData(ArrayList<ActivFitSensorData> queryResult) {
    if (queryResult.isEmpty()) {
      System.out.println("No, there is no running activity.");
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
   * Use to print the Query result from counting the total steps of the day
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
   * Call to get formatted output for HeartRates for the days
   *
   * @param date
   * @param heartRateCount
   */
  public static String getFormattedHeartRatesForTheDays(Date date, int heartRateCount) {
    StringBuilder builder = new StringBuilder();
    if (heartRateCount == 0) {
      builder.append("No data found or some error occurred.");
    } else {
      String formattedDate = WebAppConstants.inputDateFormat.format(date);
      builder
          .append("You received ")
          .append(heartRateCount)
          .append(" HeartRate notifications on ")
          .append(formattedDate);
    }
    return builder.toString();
  }

  public interface OnQueryResolvedCallback {
    void onDisplayRunningEventSelected(Date date);

    void onDisplayHeartRateEventSelected(Date date);

    void onDisplayTotalStepsInDayEventSelected(Date date);

    void onDateNotParsed();

    void onNoEventResolved();
  }
}
