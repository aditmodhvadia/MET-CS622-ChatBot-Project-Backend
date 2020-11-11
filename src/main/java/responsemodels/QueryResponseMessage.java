package responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

public class QueryResponseMessage {

  @SerializedName("data")
  @Expose
  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    public Data(String responseMsg) {
      this.responseMsg = responseMsg;
    }

    @SerializedName("responseMsg")
    @Expose
    private String responseMsg;

    public String getResponseMsg() {
      return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
      this.responseMsg = responseMsg;
    }
  }

  public static class Builder implements QueryResponseBuilder {
    private final QueryResponseMessage queryResponseMessage;

    public Builder() {
      this.queryResponseMessage = new QueryResponseMessage();
    }

    @Nonnull
    @Override
    public QueryResponseBuilder setResponseMessage(@Nonnull String queryResponseMsg) {
      this.queryResponseMessage.setData(new Data(queryResponseMsg));
      return this;
    }

    @Nonnull
    @Override
    public QueryResponseMessage build() {
      return this.queryResponseMessage;
    }
  }
}
