package co.yap.app.modules.refreal

import androidx.core.os.bundleOf
import co.yap.app.R
import co.yap.app.modules.refreal.DeepLinkNavigation.DeepLinkFlow.SET_PIN
import co.yap.modules.dashboard.cards.addpaymentcard.main.activities.AddPaymentCardActivity
import co.yap.modules.dashboard.cards.analytics.main.activities.CardAnalyticsActivity
import co.yap.modules.dashboard.cards.paymentcarddetail.statments.activities.CardStatementsActivity
import co.yap.modules.dashboard.main.activities.YapDashboardActivity
import co.yap.modules.dashboard.more.cdm.CdmMapFragment
import co.yap.modules.dashboard.more.help.fragments.HelpSupportFragment
import co.yap.modules.dashboard.more.home.fragments.InviteFriendFragment
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.modules.dashboard.more.notifications.main.NotificationsActivity
import co.yap.modules.dashboard.more.yapforyou.activities.YAPForYouActivity
import co.yap.modules.dashboard.transaction.detail.TransactionDetailsActivity
import co.yap.modules.dashboard.yapit.sendmoney.landing.SendMoneyDashboardActivity
import co.yap.modules.dashboard.yapit.topup.cardslisting.TopUpBeneficiariesActivity
import co.yap.modules.kyc.activities.DocumentsDashboardActivity
import co.yap.modules.others.fragmentpresenter.activities.FragmentPresenterActivity
import co.yap.modules.setcardpin.activities.SetCardPinWelcomeActivity
import co.yap.modules.webview.WebViewFragment
import co.yap.networking.customers.responsedtos.documents.GetMoreDocumentsResponse
import co.yap.sendmoney.y2y.home.activities.YapToYapDashboardActivity
import co.yap.translation.Strings
import co.yap.widgets.qrcode.QRCodeFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.constants.RequestCodes.REQUEST_CARD_ADDED
import co.yap.yapcore.enums.EIDStatus
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.PartnerBankStatus
import co.yap.yapcore.enums.UserAccessRestriction
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.IDeepLinkHandler
import co.yap.yapcore.helpers.NotificationHelper
import co.yap.yapcore.helpers.SingletonHolder
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.managers.SessionManager

class DeepLinkNavigation private constructor(private val activity: YapDashboardActivity) :
    IDeepLinkHandler {
    companion object :
        SingletonHolder<DeepLinkNavigation, YapDashboardActivity>(::DeepLinkNavigation)

    override fun handleDeepLinkFlow(flowId: String?) {

        flowId?.let {
            when (it) {
                SET_PIN.flowId -> {
                    if (NotificationHelper.shouldShowSetPin(SessionManager.card.value) && SessionManager.user?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status) {
                        SessionManager.getPrimaryCard()?.let { card ->
                            activity.startActivityForResult(
                                SetCardPinWelcomeActivity.newIntent(
                                    activity,
                                    card
                                ), RequestCodes.REQUEST_FOR_SET_PIN
                            )
                        } ?: activity.toast("Debit card not found.")
                    }
                }
                DeepLinkFlow.CARD_STATMENT.flowId -> {
                    SessionManager.getPrimaryCard()?.let {
                        activity.launchActivity<CardStatementsActivity> {
                            putExtra("card", it)
                            putExtra("isFromDrawer", false)
                        }
                    }
                }
                DeepLinkFlow.ANALYTICS.flowId -> {
                    activity.launchActivity<CardAnalyticsActivity>(type = FeatureSet.ANALYTICS)
                }
                DeepLinkFlow.TERM_CONDITION.flowId -> {
                    activity.startFragment<WebViewFragment>(
                        fragmentName = WebViewFragment::class.java.name, bundle = bundleOf(
                            Constants.PAGE_URL to Constants.URL_TERMS_CONDITION
                        ), showToolBar = false
                    )
                }
                DeepLinkFlow.UPDATE_EMIRATES_ID.flowId -> {
                    if (SessionManager.user?.getUserAccessRestrictions()
                            ?.contains(UserAccessRestriction.EID_EXPIRED) == true || !SessionManager.user?.EIDExpiryMessage.isNullOrBlank()
                    ) {
                        if (SessionManager.user?.otpBlocked == true) {
                            if (SessionManager.eidStatus == EIDStatus.NOT_SET &&
                                PartnerBankStatus.ACTIVATED.status != SessionManager.user?.partnerBankStatus
                            ) {
                                activity.launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
                                    putExtra(
                                        Constants.name,
                                        SessionManager.user?.currentCustomer?.firstName.toString()
                                    )
                                    putExtra(Constants.data, true)
                                    putExtra(
                                        "document",
                                        GetMoreDocumentsResponse.Data.CustomerDocument.DocumentInformation(
                                            identityNo = SessionManager.user?.currentCustomer?.identityNo
                                        )
                                    )
                                }
                            } else {
                                showBlockedFeatureAlert(
                                    activity,
                                    FeatureSet.UPDATE_EID
                                )
                            }
                        } else {
                            activity.launchActivity<DocumentsDashboardActivity>(
                                requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS,
                                type = FeatureSet.UPDATE_EID
                            ) {
                                putExtra(
                                    Constants.name,
                                    SessionManager.user?.currentCustomer?.firstName.toString()
                                )
                                putExtra(Constants.data, true)
                                putExtra(
                                    "document",
                                    GetMoreDocumentsResponse.Data.CustomerDocument.DocumentInformation(
                                        identityNo = SessionManager.user?.currentCustomer?.identityNo
                                    )
                                )
                            }
                        }
                        return@let
                    }
                }
                DeepLinkFlow.PERSON_DETAILS.flowId, DeepLinkFlow.SETTINGS.flowId -> {
                    activity.launchActivity<MoreActivity> { }
                }
                DeepLinkFlow.HELP_SUPPORT.flowId -> {
                    activity.startActivity(
                        FragmentPresenterActivity.getIntent(
                            activity,
                            Constants.MODE_HELP_SUPPORT, null
                        )
                    )
                }
                DeepLinkFlow.INVITE_FRIEND.flowId -> {
                    activity.startFragment<InviteFriendFragment>(
                        InviteFriendFragment::class.java.name, false,
                        bundleOf()
                    )
                }
                DeepLinkFlow.LOCATE_ATM_CDM.flowId -> {
                    activity.startFragment<HelpSupportFragment>(CdmMapFragment::class.java.name)
                }
                DeepLinkFlow.NOTIFICATIONS.flowId -> {
                    activity.launchActivity<NotificationsActivity>(requestCode = RequestCodes.REQUEST_NOTIFICATION_FLOW)
                }
                DeepLinkFlow.YAP_FOR_YOU.flowId -> {
                    activity.launchActivity<YAPForYouActivity>(type = FeatureSet.YAP_FOR_YOU)
                }
                DeepLinkFlow.ADD_SPARE_CARD.flowId -> {
                    activity.startActivityForResult(
                        AddPaymentCardActivity.newIntent(activity),
                        REQUEST_CARD_ADDED
                    )
                }
                DeepLinkFlow.CARDS.flowId -> {
                    activity.getViewBinding().bottomNav.selectedItemId = R.id.yapCards
                    activity.getViewBinding().viewPager.setCurrentItem(2, false)
                }
                DeepLinkFlow.QR_CODE.flowId -> {
                    QRCodeFragment {}.show(activity.supportFragmentManager, "")
                }
                DeepLinkFlow.YAP_TO_YAP.flowId -> {
                    activity.launchActivity<YapToYapDashboardActivity>(
                        requestCode = RequestCodes.REQUEST_Y2Y_TRANSFER,
                        type = FeatureSet.YAP_TO_YAP
                    ) {
                        putExtra(ExtraKeys.IS_Y2Y_SEARCHING.name, false)
                    }
                }
                DeepLinkFlow.SEND_MONEY.flowId -> {
                    activity.launchActivity<SendMoneyDashboardActivity>(type = FeatureSet.SEND_MONEY)
                }
                DeepLinkFlow.TOP_UP.flowId -> {
                    activity.launchActivity<TopUpBeneficiariesActivity>(requestCode = RequestCodes.REQUEST_SHOW_BENEFICIARY) {
                        putExtra(
                            Constants.SUCCESS_BUTTON_LABEL,
                            activity.getString(Strings.screen_topup_success_display_text_dashboard_action_button_title)
                        )
                    }
                }
                DeepLinkFlow.YAP_STORE.flowId -> {
                    activity.getViewBinding().bottomNav.selectedItemId = R.id.yapStore
                    // activity.getViewBinding().viewPager.setCurrentItem(1, false)
                }
                DeepLinkFlow.TRANSACTION_DETAILS.flowId -> {
                    activity.launchActivity<TransactionDetailsActivity>()
                }
                else -> {
                }
            }
        }
        SessionManager.deepLinkFlowId.value = null
    }

    enum class DeepLinkFlow(val flowId: String) {
        SET_PIN("set_pin"),
        CARD_STATMENT("card_statements"),
        ANALYTICS("analytics"),
        TERM_CONDITION("terms_conditions"),
        UPDATE_EMIRATES_ID("update_emirates_id"),
        PERSON_DETAILS("personal_details"),
        SETTINGS("settings"),
        HELP_SUPPORT("help_support"),
        INVITE_FRIEND("invite_friend"),
        LOCATE_ATM_CDM("locate_atm_cdm"),
        NOTIFICATIONS("notifications"),
        YAP_FOR_YOU("yap_for_you"),
        ADD_SPARE_CARD("add_spare_card"),
        CARDS("cards"),
        QR_CODE("qr_code"),
        YAP_TO_YAP("yap_to_yap"),
        SEND_MONEY("send_money"),
        TOP_UP("top_up"),
        YAP_STORE("yap_store"),
        DASHBOARD("dashboard"),
        TRANSACTION_DETAILS("TransactionDetails"),
    }
}





