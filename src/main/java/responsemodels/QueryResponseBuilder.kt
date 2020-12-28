package responsemodels;

import javax.annotation.Nonnull;

public interface QueryResponseBuilder {

  @Nonnull
  QueryResponseBuilder setResponseMessage(@Nonnull String queryResponseMsg);

  @Nonnull
  QueryResponseMessage build();
}
