package responsemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
