package co.yap.yapcore.enums

import androidx.annotation.Keep

@Keep
enum class PartnerBankStatus(val status: String) {
    SIGN_UP_PENDING("SIGN_UP_PENDING"),
    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED"),
    PHYSICAL_CARD_PENDING("PHYSICAL_CARD_PENDING"),
    PHYSICAL_CARD_SUCCESS("PHYSICAL_CARD_SUCCESS"),
    HARD_KYC_PENDING("HARD_KYC_PENDING"),
    HARD_KYC_DONE("HARD_KYC_DONE"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    INITIAL_VERIFICATION_SUCCESSFUL("INITIAL_VERIFICATION_SUCCESSFUL"),
    ACTIVATED("ACTIVATED"),
    REJECTED_AT_INITIAL_VERIFICATION("REJECTED_AT_INITIAL_VERIFICATION"),
    PROCESSED_SUCCESSFULLY("PROCESSED_SUCCESSFULLY"),
    ADDITIONAL_COMPLIANCE_INFO_REQ("ADDITIONAL_COMPLIANCE_INFO_REQ"),
    ADDITIONAL_COMPLIANCE_INFO_PROVIDED("ADDITIONAL_COMPLIANCE_INFO_PROVIDED"),
    ADD_INFO_NOTIFICATION_DONE("ADD_INFO_NOTIFICATION_DONE"),
    ADD_COMPLIANCE_INFO_SUBMITTED_BY_ADMIN("ADD_COMPLIANCE_INFO_SUBMITTED_BY_ADMIN")

}
