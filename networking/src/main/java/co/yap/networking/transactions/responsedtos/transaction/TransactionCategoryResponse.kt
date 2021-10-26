package co.yap.networking.transactions.responsedtos.transaction

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class TransactionCategoryResponse(
    @SerializedName("data")
    val txnCategories: List<TapixCategory>? = null
) : ApiResponse()