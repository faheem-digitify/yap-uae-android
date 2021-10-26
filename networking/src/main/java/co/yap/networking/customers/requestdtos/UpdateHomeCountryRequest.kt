package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName

data class UpdateHomeCountryRequest(
    @SerializedName("homeCountry") val homeCountry: String
)