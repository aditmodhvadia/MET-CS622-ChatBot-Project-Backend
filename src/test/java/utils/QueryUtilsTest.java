package utils;

import org.junit.Test;

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
}
