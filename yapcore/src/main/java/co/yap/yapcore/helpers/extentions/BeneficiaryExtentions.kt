package co.yap.yapcore.helpers.extentions

import android.content.Context
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.SendMoneyBeneficiaryType
import co.yap.yapcore.enums.TransactionProductCode
import co.yap.yapcore.helpers.Utils

fun Beneficiary?.getProductCode(): String {
    this?.let { beneficiary ->
        return when (beneficiary.beneficiaryType) {
            SendMoneyBeneficiaryType.CASHPAYOUT.type -> TransactionProductCode.CASH_PAYOUT.pCode
            SendMoneyBeneficiaryType.UAEFTS.type -> TransactionProductCode.UAEFTS.pCode
            SendMoneyBeneficiaryType.DOMESTIC.type -> TransactionProductCode.DOMESTIC.pCode
            SendMoneyBeneficiaryType.RMT.type -> TransactionProductCode.RMT.pCode
            SendMoneyBeneficiaryType.SWIFT.type -> TransactionProductCode.SWIFT.pCode
            else -> ""
        }
    } ?: return ""
}

fun Beneficiary?.getBeneficiaryTransferType(): FeatureSet {
    this?.let { beneficiary ->
        return when (beneficiary.beneficiaryType) {
            SendMoneyBeneficiaryType.UAEFTS.type -> FeatureSet.UAEFTS_TRANSFER
            SendMoneyBeneficiaryType.DOMESTIC.type -> FeatureSet.DOMESTIC_TRANSFER
            SendMoneyBeneficiaryType.RMT.type -> FeatureSet.RMT_TRANSFER
            SendMoneyBeneficiaryType.SWIFT.type -> FeatureSet.SWIFT_TRANSFER
            else -> FeatureSet.NONE
        }
    } ?: return FeatureSet.NONE
}

fun Beneficiary?.isRMTAndSWIFT(): Boolean {
    this?.let { beneficiary ->
        return when (beneficiary.beneficiaryType) {
            SendMoneyBeneficiaryType.RMT.type, SendMoneyBeneficiaryType.SWIFT.type -> true
            else -> false
        }
    } ?: return false
}

fun ArrayList<Beneficiary>?.parseRecentItems() {
    this?.forEach {
        it.name = if (it.fullName() == "null null") it.title else it.fullName()
        it.profilePictureUrl = it.beneficiaryPictureUrl
        it.type = it.beneficiaryType
        it.isoCountryCode = it.country
    }
}

fun List<Beneficiary>?.parseRecentItems(context: Context? = null) {
    this?.forEach {
        it.name = if (it.fullName() == "null null") it.title else it.fullName()
        it.profilePictureUrl = it.beneficiaryPictureUrl
        it.type = it.beneficiaryType
        it.isoCountryCode = it.country
        it.mobileNo = context?.let { it1 ->
            Utils.getFormattedPhoneNumber(
                it1,
                if (it.countryCode.isNullOrBlank()) "00971" + it.mobileNo else it.countryCode + it.mobileNo
            )
        }
    }
}
