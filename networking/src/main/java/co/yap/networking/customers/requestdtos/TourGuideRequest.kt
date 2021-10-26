package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName

data class TourGuideRequest(
    @SerializedName("viewName")
    val viewName: String? = null,
    @SerializedName("completed")
    val completed: Boolean? = null,
    @SerializedName("skipped")
    val skipped: Boolean? = null,
    @SerializedName("viewed")
    val viewed: Boolean? = null
)