package co.yap.networking.customers.requestdtos

import com.google.gson.annotations.SerializedName
import java.io.File

data class UploadAdditionalInfo(
    @SerializedName("files") val files: File? = null,
    @SerializedName("documentType") val documentType: String? = null,
    @SerializedName("questionAnswer") val questionAnswer: String? = null,
    @SerializedName("id") val id: String? = null,
    @Transient var contentType: String? = null
)