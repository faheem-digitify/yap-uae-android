package co.yap.modules.dashboard.cards.addpaymentcard.models

import androidx.annotation.Keep

@Keep
data class VirtualCardModel(
    val uploadDate: String? = null,
    val backSideDesignImage: String? = null,
    val designCodeUUID: String? = null,
    val designCodeName: String? = null,
    val designCode: String? = null,
    val frontSideDesignImage: String? = null,
    val status: String? = null,
    val designColorCode: String? = null,
    val productCode: String? = null,
    val designCodeDescription: String? = null,
    val editAndDelete: Boolean? = null
)