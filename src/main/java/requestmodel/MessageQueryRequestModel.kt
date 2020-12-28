package requestmodel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageQueryRequestModel(@field:Expose @field:SerializedName("query") var query: String)