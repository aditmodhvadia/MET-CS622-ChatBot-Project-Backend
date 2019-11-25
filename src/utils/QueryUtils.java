package utils;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {
    //    public static final String RUNNING_EVENT_REGEX = "/run(ning)?/igm";
//    public static final String RUNNING_EVENT_REGEX = ".*([rR]un|[rR]unning).*";
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
                if (query.contains("run") || query.contains("Run") || query.contains("running") || query.contains("Running")) {
                    callback.onDisplayRunningEventSelected(date);
                } else if (query.contains("steps") || query.contains("Steps") || query.contains("step")
                        || query.contains("Step") || query.contains("walk")) {
                    callback.onDisplayTotalStepsInDayEventSelected(date);
                } else if (query.contains("heartrate") || query.contains("heart") || query.contains("Heart") || query.contains("Heartrate")) {
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

    public interface OnQueryResolvedCallback {
        void onDisplayRunningEventSelected(Date date);

        void onDisplayHeartRateEventSelected(Date date);

        void onDisplayTotalStepsInDayEventSelected(Date date);

        void onDateNotParsed();

        void onNoEventResolved();
    }
}
