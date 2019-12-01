package utils;

import sensormodels.ActivFitSensorData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class QueryUtils {
    //    public static final String RUNNING_EVENT_REGEX = ".run./i";
    public static final String RUNNING_EVENT_REGEX = ".([rR]un|[rR]unning).";
    public static final String STEP_COUNT_EVENT_REGEX = ".([sS]teps*|[wW]alk).";
    public static final String HEART_RATE_EVENT_REGEX = ".([hH]eart|[hH]eartrate).";
//    public static final String RUNNING_EVENT_REGEX = "run|Run";

    public static void determineQueryType(String query, OnQueryResolvedCallback callback) {
        String regex = "(\\d{2}[-,/]\\d{2}[-,/]\\d{4})";    //  Regex for Date input
        Matcher m = Pattern.compile(regex).matcher(query);
        Date date;
        if (m.find()) {
            try {
                date = WebAppConstants.inputDateFormat.parse(m.group(1));

                System.out.println(WebAppConstants.inputDateFormat.format(date));

//                TODO: Resolve query type now and remove this
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
        } else {
            // Bad input
            callback.onDateNotParsed();
        }
    }

    private static boolean isMatching(String pattern, String query) {
        Matcher m = Pattern.compile(pattern).matcher(query);
        return m.find();
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
            for (ActivFitSensorData data : queryResult) {
                builder.append("Yes, you ran from ")
                        .append(data.getTimestamp().getStartTime())
                        .append(" to ")
                        .append(data.getTimestamp().getEndTime());
                System.out.println("Yes, you ran from " + data.getTimestamp().getStartTime() + " to " + data.getTimestamp().getEndTime());
            }
            return builder.toString();
        }
    }

    /**
     * Use to print the Query result from counting the total steps of the day
     *
     * @param stepCount given step count
     * @param userDate  given Date of the step count
     */
    public static String getFormattedTotalStepsForTheDay(int stepCount, Date userDate) {
        if (stepCount == (int) -Infinity) {
            return "No steps record found for the day";
        } else {
            return "You walked " + stepCount + " steps on " + WebAppConstants.inputDateFormat.format(userDate);
        }
    }

    /**
     * Call to get formatted output for HeartRates for the days
     *
     * @param date
     * @param heartRates
     */
    public static String getFormattedHeartRatesForTheDays(Date date, HashMap<String, Integer> heartRates) {
        StringBuilder builder = new StringBuilder();
        if (heartRates.size() == 0) {
            builder.append("No data found in MongoDB or some error occurred.");
        } else {
            String formattedDate = WebAppConstants.inputDateFormat.format(date);
            if (heartRates.containsKey(formattedDate)) {
                builder.append("You received ")
                        .append(heartRates.get(formattedDate))
                        .append(" HeartRate notifications on ")
                        .append(formattedDate);
            } else {
                builder.append("No data found in MongoDB or some error occurred.");
            }
            /*for (String date :
                    heartRates.keySet()) {
                int hearRateCount = heartRates.get(date);
                builder.append("You received ")
                        .append(hearRateCount)
                        .append(" HeartRate notifications on ")
                        .append(date);
            }*/
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
