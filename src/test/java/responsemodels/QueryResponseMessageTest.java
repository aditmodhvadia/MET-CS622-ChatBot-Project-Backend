package responsemodels;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryResponseMessageTest {
  private static final String QUERY_MSG = "This is sample query msg.";

  @Test
  public void builderTest() {
    QueryResponseMessage queryResponseMessage =
            new QueryResponseMessage.Builder().setResponseMessage(QUERY_MSG).build();

    assertEquals(QUERY_MSG, queryResponseMessage.getData().getResponseMsg());
  }
}
