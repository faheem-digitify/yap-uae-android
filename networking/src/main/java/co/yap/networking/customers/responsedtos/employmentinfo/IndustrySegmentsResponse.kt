package co.yap.networking.customers.responsedtos.employmentinfo

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class IndustrySegmentsResponse(
    @SerializedName("data")
    val segments: ArrayList<IndustrySegment>
) : ApiResponse()