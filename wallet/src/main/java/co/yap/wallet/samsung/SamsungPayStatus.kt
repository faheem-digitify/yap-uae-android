package co.yap.wallet.samsung

sealed class SamsungPayStatus {
    object SPAY_NOT_SUPPORTED : SamsungPayStatus()
    object SPAY_READY : SamsungPayStatus()
    object SPAY_NOT_READY : SamsungPayStatus()
    object SPAY_NOT_ALLOWED_TEMPORALLY : SamsungPayStatus()
    object ERROR_SPAY_SETUP_NOT_COMPLETE : SamsungPayStatus()
    object ERROR_SPAY_APP_NEED_TO_UPDATE : SamsungPayStatus()
    object ERROR_PARTNER_SDK_API_LEVEL : SamsungPayStatus()
    object ERROR_PARTNER_SERVICE_TYPE : SamsungPayStatus()
}