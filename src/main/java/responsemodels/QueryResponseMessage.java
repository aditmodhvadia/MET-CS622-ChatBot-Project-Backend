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

  public static class QueryResponseMessageBuilder implements QueryResponseBuilder {
    private final QueryResponseMessage queryResponseMessage;

    public QueryResponseMessageBuilder() {
      this.queryResponseMessage = new QueryResponseMessage();
    }

    @Nonnull
    @Override
    public QueryResponseBuilder setResponseMessage(@Nonnull String queryResponseMsg) {
      Data data = this.queryResponseMessage.getData();
      if (data == null) {
        data = new Data(queryResponseMsg);
      } else {
        data.setResponseMsg(queryResponseMsg);
      }
      this.queryResponseMessage.setData(data);
      return this;
    }

    @Nonnull
    @Override
    public QueryResponseMessage build() {
      return this.queryResponseMessage;
    }
  }
}
