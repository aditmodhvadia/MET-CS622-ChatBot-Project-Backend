package requestmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageQueryRequestModel {
  @SerializedName("query")
  @Expose
  private String query;

  public MessageQueryRequestModel(String query) {
    this.query = query;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
