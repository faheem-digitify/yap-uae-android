package co.yap.networking.cards.responsedtos

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName


data class SPayCardResponse(
    @SerializedName("data") val data: SPayCardData? = null
) : ApiResponse()

data class SPayCardData(
    @SerializedName("cardInfo") var cardInfo: CardInfo? = null,
    @SerializedName("tokenizationAuthenticationValue") var tokenizationAuthenticationValue: String? = null
) : ApiResponse()

data class CardInfo(
    @SerializedName("encryptedData") var encryptedData: String? = null,
    @SerializedName("encryptedKey") var encryptedKey: String? = null,
    @SerializedName("iv") var iv: String? = null,
    @SerializedName("oaepHashingAlgorithm") var oaepHashingAlgorithm: String? = null,
    @SerializedName("publicKeyFingerprint") var publicKeyFingerprint: String? = null
) : ApiResponse()