package co.yap.networking.transactions.responsedtos.transaction

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class TransactionDataResponseForLeanplum(
    @SerializedName("data")
    var data: TransactionDataForLeanplum,
    @SerializedName("errors")
    var errors: Any?
) : ApiResponse()