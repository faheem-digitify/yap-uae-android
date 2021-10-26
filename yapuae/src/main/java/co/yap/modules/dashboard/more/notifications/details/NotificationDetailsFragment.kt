package co.yap.modules.dashboard.more.notifications.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.kyc.activities.DocumentsDashboardActivity
import co.yap.modules.location.activities.LocationSelectionActivity
import co.yap.modules.others.fragmentpresenter.activities.FragmentPresenterActivity
import co.yap.modules.setcardpin.activities.SetCardPinWelcomeActivity
import co.yap.networking.cards.responsedtos.Address
import co.yap.networking.customers.responsedtos.documents.GetMoreDocumentsResponse
import co.yap.networking.notification.responsedtos.NotificationAction
import co.yap.translation.Strings
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.EIDStatus
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.PartnerBankStatus
import co.yap.yapcore.helpers.confirm
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.helpers.extentions.makeCall
import co.yap.yapcore.helpers.extentions.showBlockedFeatureAlert
import co.yap.yapcore.managers.SessionManager

class NotificationDetailsFragment : BaseBindingFragment<INotificationDetails.ViewModel>(),
    INotificationDetails.View {
    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutId() = R.layout.fragment_notification_details

    override val viewModel: NotificationDetailsViewModel
        get() = ViewModelProviders.of(this).get(NotificationDetailsViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.notification = arguments?.getParcelable(Constants.data)
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.tvDeleteNotification -> {
                    confirm(
                        message = getString(Strings.screen_notification_listing_display_text_delete_message),
                        title = getString(
                            Strings.screen_notification_listing_display_text_delete_alert_title
                        )
                    ) {
                        viewModel.deleteFcmNotifications(viewModel.state.notification) {
                            navigateBack()
                        }
                    }
                }
                R.id.btnAction -> {
                    when (viewModel.state.notification?.action) {
                        NotificationAction.COMPLETE_VERIFICATION -> {
                            launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
                                putExtra(
                                    Constants.name,
                                    SessionManager.user?.currentCustomer?.firstName.toString()
                                )
                                putExtra(Constants.data, false)
                            }
                        }

                        NotificationAction.SET_PIN -> {
                            SessionManager.getPrimaryCard()?.let { card ->
                                startActivityForResult(
                                    SetCardPinWelcomeActivity.newIntent(
                                        requireContext(),
                                        card
                                    ), RequestCodes.REQUEST_FOR_SET_PIN
                                )
                            } ?: showToast("Debit card not found.")
                        }

                        NotificationAction.UPDATE_EMIRATES_ID -> {
                            if (SessionManager.user?.otpBlocked == true) {
                                if (SessionManager.eidStatus == EIDStatus.NOT_SET &&
                                    PartnerBankStatus.ACTIVATED.status != SessionManager.user?.partnerBankStatus
                                ) {
                                    launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
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
                                        requireActivity(),
                                        FeatureSet.UPDATE_EID
                                    )
                                }
                            } else {
                                launchActivity<DocumentsDashboardActivity>(
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
                        }
                        NotificationAction.HELP_AND_SUPPORT -> {
                            startActivity(
                                FragmentPresenterActivity.getIntent(
                                    requireContext(),
                                    Constants.MODE_HELP_SUPPORT, null
                                )
                            )
                        }
                        NotificationAction.CARD_FEATURES_BLOCKED -> {
                            requireContext().makeCall(SessionManager.helpPhoneNumber)
                        }
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCodes.REQUEST_KYC_DOCUMENTS -> {
                data?.let {
                    val result =
                        data.getBooleanExtra(Constants.result, false)
                    if (result) {
                        startActivityForResult(
                            LocationSelectionActivity.newIntent(
                                context = requireContext(),
                                address = SessionManager.userAddress ?: Address(),
                                headingTitle = getString(Strings.screen_meeting_location_display_text_add_new_address_title),
                                subHeadingTitle = getString(Strings.screen_meeting_location_display_text_subtitle),
                                onBoarding = true
                            ), RequestCodes.REQUEST_FOR_LOCATION
                        )
                    } else if (data.getBooleanExtra(Constants.skipped, false)) {
                        setResult()
                    }
                }
            }
            RequestCodes.REQUEST_FOR_LOCATION -> {
                data?.let {
                    val result = it.getBooleanExtra(Constants.ADDRESS_SUCCESS, false)
                    if (result) {
                        startActivityForResult(
                            FragmentPresenterActivity.getIntent(
                                requireContext(),
                                Constants.MODE_MEETING_CONFORMATION,
                                null
                            ), RequestCodes.REQUEST_MEETING_CONFIRMED
                        )
                    }
                }
            }
            RequestCodes.REQUEST_MEETING_CONFIRMED -> {
                SessionManager.getAccountInfo()
                activity?.finish()
                setResult()
            }
        }
    }

    private fun setResult() {
        val intent = Intent()
        intent.putExtra(Constants.result, true)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> navigateBack()
        }
    }
}
