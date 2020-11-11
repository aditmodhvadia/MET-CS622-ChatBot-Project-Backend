package utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QueryUtilsTest {

  @Test
  public void isRunningEventMatching() {
    assertTrue(QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I run today?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I ran yesterday?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "did I go running today?"));
    assertTrue(
        QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "How much did I run yesterday?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "Running yesterday?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.RUNNING_EVENT_REGEX, "Run was fun yesterday!"));
  }

  @Test
  public void stepCountEventMatching() {
    assertTrue(QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How many steps?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How many stepsss?"));
    assertTrue(
        QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Did I take a step today?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Did I walk today?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "Walk today?"));
    assertTrue(QueryUtils.isMatching(QueryUtils.STEP_COUNT_EVENT_REGEX, "How much I walked?"));
  }

  @Test
  public void heartRateEventRegex() {
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heartrate"));
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "HeartRate"));
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart rate"));
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart Rate"));
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "Heart"));
    assertTrue(QueryUtils.isMatching(QueryUtils.HEART_RATE_EVENT_REGEX, "heart"));
  }

  @Test
  public void extractsDateFromQuery() {
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.MILLISECOND, 0);
    assertEquals(today.getTime().getDate(), QueryUtils.extractDateFromQuery("today").getDate());

    today.add(Calendar.DATE, -1);
    assertEquals(
            today.getTime().getDate(),
            Objects.requireNonNull(QueryUtils.extractDateFromQuery("yesterday")).getDate());
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    calendar.set(Calendar.YEAR, 2018);
    calendar.set(Calendar.MONTH, 9);
    calendar.set(Calendar.DAY_OF_MONTH, 12);
    assertEquals(
            calendar.getTime().getDate(),
            Objects.requireNonNull(QueryUtils.extractDateFromQuery("10-12-2018")).getDate());
  }

  @Test
  public void determineQueryType() {
    assertEquals(
            QueryUtils.QueryType.HEART_RATE, QueryUtils.determineQueryType("what was my heartrate"));

    assertEquals(QueryUtils.QueryType.STEP_COUNT, QueryUtils.determineQueryType("how many steps?"));

    assertEquals(
            QueryUtils.QueryType.RUNNING, QueryUtils.determineQueryType("how much did I run today?"));

    assertEquals(
            QueryUtils.QueryType.UNKNOWN, QueryUtils.determineQueryType("what was my temperature?"));
  }
}
