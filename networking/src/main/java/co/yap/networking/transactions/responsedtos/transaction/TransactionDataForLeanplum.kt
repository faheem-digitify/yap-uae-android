package co.yap.networking.transactions.responsedtos.transaction

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class TransactionDataForLeanplum (
    @SerializedName("lastTransactionType")
    var lastTransactionType: String?=null,
    @SerializedName("lastTransactionTime")
    var lastTransactionTime: String?=null,
    @SerializedName("lastPOSTransactionCategory")
    var lastPOSTransactionCategory: String?=null,
    @SerializedName("totalTransactionCount")
    var totalTransactionCount: String?=null,
    @SerializedName("totalTransactionValue")
    var totalTransactionValue: String?=null
): ApiResponse()