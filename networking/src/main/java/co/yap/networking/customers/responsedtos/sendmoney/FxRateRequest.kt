package co.yap.networking.customers.responsedtos.sendmoney

import com.google.gson.annotations.SerializedName

data class FxRateRequest (
    @SerializedName("other_bank_country") val other_bank_country: String
)