package responsemodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nonnull

class QueryResponseMessage {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data(@field:Expose @field:SerializedName("responseMsg") var responseMsg: String?)
    class QueryResponseMessageBuilder : QueryResponseBuilder {
        private val queryResponseMessage: QueryResponseMessage = QueryResponseMessage()

        @Nonnull
        override fun setResponseMessage(@Nonnull queryResponseMsg: String?): QueryResponseBuilder {
            var data = queryResponseMessage.data
            if (data == null) {
                data = Data(queryResponseMsg)
            } else {
                data.responseMsg = queryResponseMsg
            }
            queryResponseMessage.data = data
            return this
        }

        @Nonnull
        override fun build(): QueryResponseMessage {
            return queryResponseMessage
        }

    }
}