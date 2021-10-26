package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName

data class SendInviteFriendRequest(
    @SerializedName("inviterCustomerId")
    val inviterCustomerId: String,
    @SerializedName("referralDate")
    val referralDate: String
)