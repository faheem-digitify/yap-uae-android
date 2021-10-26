package co.yap.yapcore.helpers

import android.content.Context
import co.yap.networking.cards.responsedtos.Card
import co.yap.networking.customers.responsedtos.AccountInfo
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.NotificationAction
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.yapcore.R
import co.yap.yapcore.enums.*
import co.yap.yapcore.helpers.DateUtils.SERVER_DATE_FORMAT
import co.yap.yapcore.helpers.DateUtils.UTC
import co.yap.yapcore.helpers.DateUtils.getCurrentDateWithFormat
import co.yap.yapcore.helpers.extentions.getNotificationOfBlockedFeature
import co.yap.yapcore.helpers.extentions.getUserAccessRestrictions
import co.yap.yapcore.leanplum.KYCEvents
import co.yap.yapcore.leanplum.fireEventWithAttribute
import co.yap.yapcore.leanplum.trackEventWithAttributes
import co.yap.yapcore.managers.SessionManager

object NotificationHelper {
    fun getNotifications(
        accountInfo: AccountInfo?,
        paymentCard: Card?,
        context: Context
    ): ArrayList<HomeNotification> {
        return getNotification(
            accountInfo,
            paymentCard,
            context
        )
    }

    private fun getNotification(
        accountInfo: AccountInfo?,
        paymentCard: Card?,
        context: Context
    ): ArrayList<HomeNotification> {
        if ((accountInfo?.notificationStatuses == AccountStatus.EID_EXPIRED.name
                    || accountInfo?.notificationStatuses == AccountStatus.EID_RESCAN_REQ.name)
        ) {
            fireEventWithAttribute(KYCEvents.EID_EXPIRE.type, "")
            trackEventWithAttributes(SessionManager.user, eidExpire = true)
        }
        val list = ArrayList<HomeNotification>()
        if (accountInfo?.otpBlocked == true) {
            list.add(
                HomeNotification(
                    id = "1",
                    description = Translator.getString(
                        context,
                        Strings.screen_home_help_and_support_desc
                    ),
                    action = NotificationAction.HELP_AND_SUPPORT,
                    imgResId = R.raw.gif_notification_bel,
                    createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                    btnTitle = "Open " + Translator.getString(
                        context,
                        Strings.screen_help_support_display_text_title
                    )
                )
            )
        }
        if ((accountInfo?.notificationStatuses == AccountStatus.ON_BOARDED.name
                    || accountInfo?.notificationStatuses == AccountStatus.CAPTURED_EID.name
                    || accountInfo?.notificationStatuses == AccountStatus.CAPTURED_ADDRESS.name
                    || accountInfo?.notificationStatuses == AccountStatus.BIRTH_INFO_COLLECTED.name
                    || accountInfo?.notificationStatuses == AccountStatus.FATCA_GENERATED.name
                    || accountInfo?.notificationStatuses == AccountStatus.MEETING_SCHEDULED.name)
            && accountInfo.partnerBankStatus != PartnerBankStatus.ACTIVATED.status
        ) {
            list.add(
                HomeNotification(
                    id = "2",
                    title = Translator.getString(
                        context,
                        Strings.screen_b2c_kyc_home_display_text_screen_title
                    ), subTitle = Translator.getString(
                        context,
                        Strings.screen_b2c_kyc_home_display_text_screen_title
                    ),
                    description = Translator.getString(
                        context,
                        Strings.screen_home_complete_verification_desc
                    ),
                    action = NotificationAction.COMPLETE_VERIFICATION,
                    imgResId = R.raw.gif_general_notification,
                    createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                    btnTitle = Translator.getString(
                        context,
                        Strings.screen_b2c_kyc_home_display_text_screen_title
                    )
                )
            )
        }

        if (shouldShowSetPin(paymentCard) && accountInfo?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status) {
            list.add(
                HomeNotification(
                    id = "3",
                    title = Translator.getString(
                        context,
                        Strings.dashboard_timeline_set_pin_stage_action_title
                    ), subTitle = Translator.getString(
                        context,
                        Strings.dashboard_timeline_set_pin_stage_action_title
                    ),
                    description = Translator.getString(context, Strings.screen_home_set_pin_desc),
                    action = NotificationAction.SET_PIN,
                    imgResId = R.raw.gif_set_pin,
                    createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                    btnTitle = Translator.getString(
                        context,
                        Strings.dashboard_timeline_set_pin_stage_action_title
                    ) + " now"
                )
            )
        }
        if (accountInfo?.getUserAccessRestrictions()
                ?.contains(UserAccessRestriction.EID_EXPIRED) == true || !accountInfo?.EIDExpiryMessage.isNullOrBlank()
        ) {
            list.add(
                HomeNotification(
                    id = "4",
                    title = Translator.getString(context, Strings.screen_home_renewed_id_title),
                    subTitle = Translator.getString(
                        context,
                        Strings.screen_home_renewed_id_title
                    ),
                    description = accountInfo?.EIDExpiryMessage ?: Translator.getString(
                        context,
                        Strings.screen_home_renewed_id_desc
                    ),
                    action = NotificationAction.UPDATE_EMIRATES_ID,
                    imgResId = R.raw.gif_general_notification,
                    createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                    btnTitle = Translator.getString(
                        context,
                        Strings.screen_b2c_eid_info_review_button_title_scan_eid
                    )
                )
            )
        }
        accountInfo?.getUserAccessRestrictions()?.forEach {
            accountInfo.getNotificationOfBlockedFeature(it, context)?.let { description ->
                list.add(
                    HomeNotification(
                        id = "5",
                        description = description,
                        action = NotificationAction.CARD_FEATURES_BLOCKED,
                        imgResId = R.raw.gif_notification_bel,
                        createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC),
                        isRead = true,
                        btnTitle = Translator.getString(
                            context,
                            Strings.screen_help_support_display_text_call_us
                        )
                    )
                )
            }
        }

        return list
    }

    fun shouldShowSetPin(paymentCard: Card?): Boolean {
        return when {
            paymentCard?.status == PaymentCardStatus.INACTIVE.name && paymentCard.deliveryStatus == CardDeliveryStatus.SHIPPED.name -> true
            paymentCard?.status == PaymentCardStatus.ACTIVE.name && !paymentCard.pinCreated -> true
            else -> false
        }
    }

    private fun getNotificationTestData(
        accountInfo: AccountInfo?,
        paymentCard: Card?,
        context: Context
    ): ArrayList<HomeNotification> {

        val list = ArrayList<HomeNotification>()
        list.add(
            HomeNotification(
                id = "1",
                description = Translator.getString(
                    context,
                    Strings.screen_home_help_and_support_desc
                ),
                action = NotificationAction.HELP_AND_SUPPORT,
                imgResId = R.raw.gif_notification_bel,
                createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                btnTitle = "Open " + Translator.getString(
                    context,
                    Strings.screen_help_support_display_text_title
                )
            )
        )

        list.add(
            HomeNotification(
                id = "2",
                title = Translator.getString(
                    context,
                    Strings.screen_b2c_kyc_home_display_text_screen_title
                ), subTitle = Translator.getString(
                    context,
                    Strings.screen_b2c_kyc_home_display_text_screen_title
                ),
                description = Translator.getString(
                    context,
                    Strings.screen_home_complete_verification_desc
                ),
                action = NotificationAction.COMPLETE_VERIFICATION,
                imgResId = R.raw.gif_general_notification,
                createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                btnTitle = Translator.getString(
                    context,
                    Strings.screen_b2c_kyc_home_display_text_screen_title
                )
            )
        )
        list.add(
            HomeNotification(
                id = "3",
                title = Translator.getString(
                    context,
                    Strings.dashboard_timeline_set_pin_stage_action_title
                ), subTitle = Translator.getString(
                    context,
                    Strings.dashboard_timeline_set_pin_stage_action_title
                ),
                description = Translator.getString(context, Strings.screen_home_set_pin_desc),
                action = NotificationAction.SET_PIN,
                imgResId = R.raw.gif_set_pin,
                createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                btnTitle = Translator.getString(
                    context,
                    Strings.dashboard_timeline_set_pin_stage_action_title
                ) + " now"
            )
        )
        list.add(
            HomeNotification(
                id = "4",
                title = Translator.getString(context, Strings.screen_home_renewed_id_title),
                subTitle = Translator.getString(
                    context,
                    Strings.screen_home_renewed_id_title
                ),
                description = Translator.getString(
                    context,
                    Strings.screen_home_renewed_id_desc
                ),
                action = NotificationAction.UPDATE_EMIRATES_ID,
                imgResId = R.raw.gif_general_notification,
                createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                btnTitle = Translator.getString(
                    context,
                    Strings.screen_b2c_eid_info_review_button_title_scan_eid
                )
            )
        )

        list.add(
            HomeNotification(
                id = "5",
                description = Translator.getString(
                    context,
                    Strings.iban_or_debit_card_freeze_or_blocked_message
                ).format(SessionManager.helpPhoneNumber),
                action = NotificationAction.CARD_FEATURES_BLOCKED,
                imgResId = R.raw.gif_notification_bel,
                createdAt = getCurrentDateWithFormat(SERVER_DATE_FORMAT, UTC), isRead = true,
                btnTitle = Translator.getString(
                    context,
                    Strings.screen_help_support_display_text_call_us
                )
            )
        )

        return list
    }

}