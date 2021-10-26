package co.yap.networking.notification.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class MsTokenResponse(
    @SerializedName("accountUuid") var accountUuid: String? = null,
    @SerializedName("action") var action: String? = null,
    @SerializedName("createdBy") var createdBy: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("isActive") var isActive: Boolean? = null,
    @SerializedName("updatedBy") var updatedBy: String? = null
) : ApiResponse()