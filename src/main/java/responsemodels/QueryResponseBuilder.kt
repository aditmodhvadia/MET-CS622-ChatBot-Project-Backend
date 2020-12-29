package responsemodels

import javax.annotation.Nonnull

interface QueryResponseBuilder {
    @Nonnull
    fun setResponseMessage(@Nonnull queryResponseMsg: String?): QueryResponseBuilder

    @Nonnull
    fun build(): QueryResponseMessage
}