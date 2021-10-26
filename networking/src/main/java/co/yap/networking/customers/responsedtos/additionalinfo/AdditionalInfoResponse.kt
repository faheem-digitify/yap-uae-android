package co.yap.networking.customers.responsedtos.additionalinfo

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

class AdditionalInfoResponse(
    @SerializedName("data")
    val additionalInfo: AdditionalInfo? = null
) : ApiResponse()