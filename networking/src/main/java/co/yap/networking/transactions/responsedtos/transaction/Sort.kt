package co.yap.networking.transactions.responsedtos.transaction

import com.google.gson.annotations.SerializedName

data class Sort(
    @SerializedName("sorted")
    var sorted: Boolean?=false,
    @SerializedName("unsorted")
    var unsorted: Boolean?=false
)