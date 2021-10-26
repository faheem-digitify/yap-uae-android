package co.yap.networking.transactions.requestdtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class SendEmailRequest(
    @SerializedName("fileUrl") val fileUrl: String? = "",
    @SerializedName("month") val month: String? = "",
    @SerializedName("year") val year: String? = "",
    @SerializedName("statementType") val statementType: String? = ""
) : ApiResponse() {

}