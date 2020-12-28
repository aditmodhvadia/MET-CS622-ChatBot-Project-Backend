package utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import org.junit.Test;

public class DatabaseUtilsTest {

  @Test
  public void isWithinDateRange() {
    Date today = new Date("11/28/2020");
    Date yesterday = new Date("11/27/2020");
    Date tomorrow = new Date("11/29/2020");

    assertTrue(DatabaseUtils.isWithinDateRange(yesterday, tomorrow, today));
    assertFalse(DatabaseUtils.isWithinDateRange(yesterday, today, tomorrow));
  }

  @Test
  public void shouldBeRunningAndNotUnknown() {
    assertTrue(DatabaseUtils.shouldBeRunningAndNotUnknown("running"));
    assertTrue(DatabaseUtils.shouldBeRunningAndNotUnknown("RUNNING"));
    assertFalse(DatabaseUtils.shouldBeRunningAndNotUnknown("unknown"));
    assertFalse(DatabaseUtils.shouldBeRunningAndNotUnknown("walking"));
  }
}
