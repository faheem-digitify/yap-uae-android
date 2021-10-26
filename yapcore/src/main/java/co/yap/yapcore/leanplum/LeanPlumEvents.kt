package co.yap.yapcore.leanplum

import androidx.annotation.Keep

@Keep
enum class SignupEvents(val type: String) {
    SIGN_UP_START("Signup_phone start"),
    SIGN_UP_NUMBER("Signup_phone number"),
    SIGN_UP_NUMBER_ERROR("Signup_phone number error"),
    SIGN_UP_OTP_CORRECT("Signup_OTP correct"),
    SIGN_UP_PASSCODE_CREATED("Signup_passcode created"),
    SIGN_UP_NAME("Signup_name"),
    SIGN_UP_EMAIL("Signup_email"),
    SIGN_UP_END("Signup_end"),
    SIGN_UP_DATE("Signup_date"),
    SIGN_UP_TIMESTAMP("Signup_timestamp"),
    SIGN_UP_LENGTH("Signup_length"),
}

@Keep
enum class KYCEvents(val type: String) {
    SKIP_KYC("clicks on Skip to dashboard"),
    SIGN_UP_ENABLED_PERMISSION("Signup_enabled permissions"),
    KYC_US_CITIIZEN("KYC_deny_US citizen"),
    KYC_ORDERED("KYC_card ordered"),
    EID_FAILURE("EIDA callback - failure"),
    EID_UNDER_AGE_18("EIDA callback - under 18"),// will be catered later
    KYC_PROHIBITED_CITIIZEN("EIDA callback - CB prohibited citizens"),
    CARD_ACTIVE("account_active"),
    KYC_ID_CONFIRMED("KYC_ID confirmed"),
    EID_EXPIRE("eid_expired"),
    EID_EXPIRE_DATE("eid_expiry_date")
}

@Keep
enum class CardEvents(val type: String) {
    VIRTUAL_CARD_SUCCESS("virtual_card_success"),
    CARD_CONTROL_INTERNATIONAL_ON("card_control_international_on"),
    CARD_CONTROL_INTERNATIONAL_OFF("card_control_international_off"),
    CARD_CONTROL_POS_ON("card_control_pos_on"),
    CARD_CONTROL_POS_OFF("card_control_pos_off"),
    CARD_CONTROL_ATM_ON("card_control_atm_on"),
    CARD_CONTROL_ATM_OFF("card_control_atm_off"),
    CARD_CONTROL_ONLINE_ON("card_control_online_on"),
    CARD_CONTROL_ONLINE_OFF("card_control_online_off"),
}

@Keep
enum class AnalyticsEvents(val type: String) {
    ANALYTICS_OPEN("analytics_open")
}

@Keep
enum class Y2YEvents(val type: String) {
    YAP_TO_YAP_SENT("yap_to_yap_sent"),
}

@Keep
enum class TopUpEvents(val type: String) {
    ACCOUNT_TOP_UP_CARD("account_top_up_card"),
    ACCOUNT_TOP_UP_TRANSFER("account_top_up_transfer"),
}

@Keep
enum class SendMoneyEvents(val type: String) {
    SEND_MONEY_LOCAL("send_money_local"),
    SEND_MONEY_INTERNATIONAL("send_money_international"), // params  (LastCountry, LastType )
    QR_PAYMENT_SUCCESS("qr_payment_success")
}

@Keep
enum class MoreB2CEvents(val type: String) {
    OPEN_ATM_MAP("open_atm_map")
}

@Keep
enum class SignInEvents(val type: String) {
    SIGN_IN("sign_in")
}