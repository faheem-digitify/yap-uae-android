package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName

data class BirthInfoRequest(
    @SerializedName("countryOfBirth")
    val countryOfBirth: String,
    @SerializedName("cityOfBirth")
    val cityOfBirth: String,
    @SerializedName("dualNationality")
    val dualNationality: String,
    @SerializedName("isDualNationality")
    val isDualNationality: Boolean
)
