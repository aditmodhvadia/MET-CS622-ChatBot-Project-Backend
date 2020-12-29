package responsemodels;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class QueryResponseMessageTest {
  private static final String QUERY_MSG = "This is sample query msg.";
  private QueryResponseMessage queryResponseMessage;

  @Before
  public void setUp() {
    queryResponseMessage =
        new QueryResponseMessage.QueryResponseMessageBuilder()
            .setResponseMessage(QUERY_MSG)
            .build();
  }

  @Test
  public void getResponseMsg() {
    assertEquals(QUERY_MSG, queryResponseMessage.getData().getResponseMsg());
  }
}
