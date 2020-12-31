package responsemodels

interface QueryResponseBuilder {
    fun setResponseMessage(queryResponseMsg: String): QueryResponseBuilder

    fun build(): QueryResponseMessage
}