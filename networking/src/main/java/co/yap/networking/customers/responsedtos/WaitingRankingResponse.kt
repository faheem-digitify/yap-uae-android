package co.yap.networking.customers.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class WaitingRankingResponse(
    @SerializedName("data") var data: Data? = null,
    @SerializedName("errors") var errors: Any?
) : ApiResponse() {
    data class Data(
        @SerializedName("waitingNewRank") var rank: String?,
        @SerializedName("waitingBehind") var waitingBehind: String?,
        @SerializedName("jump") var jump: String?,
        @SerializedName("completedKyc") var completedKyc: Boolean?,
        @SerializedName("waiting") var waiting: Boolean?,
        @SerializedName("viewable") var viewable: Boolean?,
        @SerializedName("gainPoints") var gainPoints: String?,
        @SerializedName("inviteeDetails") var inviteeDetails: ArrayList<InviteeDetails>?,
        @SerializedName("totalGainedPoints") var totalGainedPoints: String?
    )

    data class InviteeDetails(
        val inviteeCustomerId: String,
        val inviteeCustomerName: String
    )
}
