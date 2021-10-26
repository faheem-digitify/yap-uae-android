package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CompleteVerificationRequest(
    @SerializedName("countryCode")
    val countryCode: String? = null,
    @SerializedName("mobileNo")
    val mobileNo: String? = null
) : Serializable
