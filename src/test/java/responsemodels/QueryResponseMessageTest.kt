package responsemodels

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import responsemodels.QueryResponseMessage.QueryResponseMessageBuilder

class QueryResponseMessageTest {
    private val QUERY_MSG = "This is sample query msg."
    private lateinit var queryResponseMessage: QueryResponseMessage

    @Before
    fun setUp() {
        queryResponseMessage = QueryResponseMessageBuilder()
            .setResponseMessage(QUERY_MSG)
            .build()
    }

    @Test
    fun responseMsg() {
        Assert.assertEquals(QUERY_MSG, queryResponseMessage.data!!.responseMsg)
    }
}