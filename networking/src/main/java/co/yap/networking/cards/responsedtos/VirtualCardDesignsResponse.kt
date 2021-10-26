package co.yap.networking.cards.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class VirtualCardDesignsResponse(
    @SerializedName("data") val data: List<VirtualCardDesigns>? = null
) : ApiResponse()
