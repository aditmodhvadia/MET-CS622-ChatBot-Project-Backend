package responsemodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QueryResponseMessage(
    @SerializedName("data")
    @Expose
    var data: Data = Data()
) {
    data class Data(@field:Expose @field:SerializedName("responseMsg") var responseMsg: String = "")
    class QueryResponseMessageBuilder : QueryResponseBuilder {
        private val queryResponseMessage: QueryResponseMessage = QueryResponseMessage()

        override fun setResponseMessage(queryResponseMsg: String): QueryResponseBuilder {
            queryResponseMessage.data.responseMsg = queryResponseMsg
            return this
        }

        override fun build(): QueryResponseMessage {
            return queryResponseMessage
        }
    }
}