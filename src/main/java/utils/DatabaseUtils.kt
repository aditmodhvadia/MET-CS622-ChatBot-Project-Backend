package utils;

import java.util.Date;
import javax.annotation.Nonnull;

public class DatabaseUtils {
  /**
   * Determine given date lies between start date and end date
   *
   * @param startDate start date
   * @param endDate end date
   * @param sensorDate given date
   * @return <code>true</code> if within the date range, else <code>false</code>
   */
  public static boolean isWithinDateRange(
      @Nonnull Date startDate, @Nonnull Date endDate, @Nonnull Date sensorDate) {
    return sensorDate.after(startDate) && sensorDate.before(endDate);
  }

  /**
   * Determine if given activity matches with running activity
   *
   * @param activity activity
   * @return <code>true</code> if activity matches <code>running</code>, else <code>false</code>
   */
  public static boolean shouldBeRunningAndNotUnknown(@Nonnull String activity) {
    return !activity.equals("unknown") && activity.toLowerCase().equals("running");
  }
}
