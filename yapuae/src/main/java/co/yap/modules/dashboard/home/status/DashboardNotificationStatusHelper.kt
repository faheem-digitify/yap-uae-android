package co.yap.modules.dashboard.home.status

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentYapHomeBinding
import co.yap.modules.dashboard.addionalinfo.activities.AdditionalInfoActivity
import co.yap.modules.dashboard.home.interfaces.IYapHome
import co.yap.modules.dashboard.yapit.addmoney.main.AddMoneyActivity
import co.yap.modules.others.fragmentpresenter.activities.FragmentPresenterActivity
import co.yap.modules.setcardpin.activities.SetCardPinWelcomeActivity
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.CardDeliveryStatus
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.NotificationStatus
import co.yap.yapcore.enums.PartnerBankStatus
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.DateUtils.DEFAULT_DATE_FORMAT
import co.yap.yapcore.helpers.DateUtils.SERVER_DATE_FORMAT
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager

class DashboardNotificationStatusHelper(
    val fragment: Fragment,
    val binding: FragmentYapHomeBinding,
    val viewModel: IYapHome.ViewModel
) {
    var dashboardNotificationStatusAdapter: DashboardNotificationStatusAdapter? = null
    private fun getStringHelper(resourceKey: String): String = try {
        Translator.getString(getContext(), resourceKey)
    } catch (ignored: Exception) {
        ""
    }

    init {
        val onboardingStagesList = when {
            PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_REQ.status == SessionManager.user?.partnerBankStatus
                    || PartnerBankStatus.ADD_INFO_NOTIFICATION_DONE.status == SessionManager.user?.partnerBankStatus
                    || PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_PROVIDED.status == SessionManager.user?.partnerBankStatus
                    || SessionManager.user?.additionalDocSubmitionDate != null -> {
                getStatusList()
            }
            else -> {
                val list = getStatusList()
                list.removeAt(2)
                list
            }
        }
        initAdapter(onboardingStagesList)
        setUpAdapter()
    }

    fun initAdapter(onboardingStagesList: MutableList<StatusDataModel>) {
        dashboardNotificationStatusAdapter =
            DashboardNotificationStatusAdapter(getContext(), onboardingStagesList)
        dashboardNotificationStatusAdapter?.allowFullItemClickListener = false
    }

    private fun setUpAdapter() {
        dashboardNotificationStatusAdapter?.allowFullItemClickListener = false
        dashboardNotificationStatusAdapter?.setItemListener(object : OnItemClickListener {
            override fun onItemClick(view: View, data: Any, pos: Int) {
                val statusDataModel: StatusDataModel = data as StatusDataModel
                when {
                    PaymentCardOnboardingStage.SHIPPING == statusDataModel.stage && statusDataModel.progressStatus.name != StageProgress.INACTIVE.name -> {
                        openCardDeliveryStatusScreen()
                    }
                    PaymentCardOnboardingStage.SET_PIN == statusDataModel.stage && statusDataModel.progressStatus.name != StageProgress.INACTIVE.name -> {
                        openSetCardPinScreen()
                    }
                    PaymentCardOnboardingStage.TOP_UP == statusDataModel.stage && statusDataModel.progressStatus.name != StageProgress.INACTIVE.name -> {
                        openTopUpScreen()
                    }
                    PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT == statusDataModel.stage && statusDataModel.progressStatus.name != StageProgress.INACTIVE.name -> {
                        openAdditionalRequirementScreen()
                    }
                }
            }
        })

        binding.lyInclude.rvNotificationStatus.adapter = dashboardNotificationStatusAdapter
    }

    fun getStatusList(): MutableList<StatusDataModel> {
        val list = ArrayList<StatusDataModel>()
        list.add(
            StatusDataModel(
                stage = PaymentCardOnboardingStage.SHIPPING,
                statusTitle = getStringHelper(Strings.dashboard_timeline_shipping_stage_title),
                statusDescription = getSubheading(
                    PaymentCardOnboardingStage.SHIPPING,
                    getNotificationStatus(PaymentCardOnboardingStage.SHIPPING)
                ),
                statusAction = getStringHelper(Strings.dashboard_timeline_shipping_stage_action_title),
                statusDrawable = if (getNotificationStatus(PaymentCardOnboardingStage.SHIPPING) == StageProgress.COMPLETED) getContext().resources.getDrawable(
                    R.drawable.ic_dashboard_finish
                ) else getContext().resources.getDrawable(R.drawable.ic_dashboard_delivery),
                progressStatus = getNotificationStatus(PaymentCardOnboardingStage.SHIPPING)
            )
        )
        list.add(
            StatusDataModel(
                stage = PaymentCardOnboardingStage.DELIVERY,
                statusTitle = getStringHelper(Strings.dashboard_timeline_delivery_stage_title),
                statusDescription = getSubheading(
                    PaymentCardOnboardingStage.DELIVERY,
                    getNotificationStatus(PaymentCardOnboardingStage.DELIVERY)
                ),
                statusAction = null,
                statusDrawable = if (getNotificationStatus(PaymentCardOnboardingStage.DELIVERY) == StageProgress.COMPLETED) getContext().resources.getDrawable(
                    R.drawable.ic_dashboard_finish
                ) else getContext().resources.getDrawable(R.drawable.ic_card_small),
                progressStatus = getNotificationStatus(PaymentCardOnboardingStage.DELIVERY)
            )
        )

        list.add(
            StatusDataModel(
                stage = PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT,
                statusTitle = getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_title),
                statusDescription = getSubheading(
                    PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT,
                    getNotificationStatus(PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT)
                ),
                statusAction = when (SessionManager.user?.partnerBankStatus) {
                    PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_PROVIDED.status, PartnerBankStatus.ADD_COMPLIANCE_INFO_SUBMITTED_BY_ADMIN.status -> {
                        null
                    }
                    else -> getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_action_title)
                },
                statusDrawable = if (getNotificationStatus(PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT) == StageProgress.COMPLETED) getContext().resources.getDrawable(
                    R.drawable.ic_dashboard_finish
                ) else getContext().resources.getDrawable(R.drawable.file),
                progressStatus = getNotificationStatus(PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT)
            )
        )

        list.add(
            StatusDataModel(
                stage = PaymentCardOnboardingStage.SET_PIN,
                statusTitle = getStringHelper(Strings.dashboard_timeline_set_pin_stage_title),
                statusDescription = getSubheading(
                    PaymentCardOnboardingStage.SET_PIN,
                    getNotificationStatus(PaymentCardOnboardingStage.SET_PIN)
                ),
                statusAction = getStringHelper(Strings.dashboard_timeline_set_pin_stage_action_title),
                statusDrawable = if (getNotificationStatus(PaymentCardOnboardingStage.SET_PIN) == StageProgress.COMPLETED) getContext().resources.getDrawable(
                    R.drawable.ic_dashboard_finish
                ) else getContext().resources.getDrawable(R.drawable.ic_dashboard_set_pin),
                progressStatus = getNotificationStatus(PaymentCardOnboardingStage.SET_PIN)
            )
        )
        list.add(
            StatusDataModel(
                stage = PaymentCardOnboardingStage.TOP_UP,
                statusTitle = getStringHelper(Strings.dashboard_timeline_top_up_stage_title),
                statusDescription = getSubheading(
                    PaymentCardOnboardingStage.TOP_UP,
                    getNotificationStatus(PaymentCardOnboardingStage.TOP_UP)
                ),
                statusAction = getStringHelper(Strings.dashboard_timeline_top_up_stage_action_title),
                statusDrawable = if (getNotificationStatus(PaymentCardOnboardingStage.TOP_UP) == StageProgress.COMPLETED) getContext().resources.getDrawable(
                    R.drawable.ic_dashboard_finish
                ) else getContext().resources.getDrawable(R.drawable.ic_dashboard_topup),
                progressStatus = getNotificationStatus(PaymentCardOnboardingStage.TOP_UP),
                hideLine = true
            )
        )
        return list
    }

    private fun getNotificationStatus(stage: PaymentCardOnboardingStage): StageProgress {
        return SessionManager.card.value?.let { card ->
            when (stage) {
                PaymentCardOnboardingStage.SHIPPING -> {
                    return (when {
                        SessionManager.user?.partnerBankStatus == PartnerBankStatus.SIGN_UP_PENDING.status || SessionManager.user?.partnerBankStatus == PartnerBankStatus.DOCUMENT_UPLOADED.status -> {
                            if (SessionManager.user?.notificationStatuses == NotificationStatus.EMP_INFO_COMPLETED.name) StageProgress.ACTIVE else StageProgress.INACTIVE
                        }
                        card.deliveryStatus == CardDeliveryStatus.ORDERED.name || card.deliveryStatus == CardDeliveryStatus.BOOKED.name || card.deliveryStatus == CardDeliveryStatus.SHIPPING.name -> {
                            StageProgress.ACTIVE
                        }
                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name -> {
                            StageProgress.COMPLETED
                        }
                        else -> StageProgress.INACTIVE
                    })
                }
                PaymentCardOnboardingStage.DELIVERY -> {
                    return (when {
                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name
                                && (SessionManager.user?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status
                                || SessionManager.user?.partnerBankStatus == PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_REQ.status
                                || SessionManager.user?.partnerBankStatus == PartnerBankStatus.ADD_INFO_NOTIFICATION_DONE.status
                                || SessionManager.user?.partnerBankStatus == PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_PROVIDED.status) -> {
                            StageProgress.COMPLETED
                        }
                        card.deliveryStatus == CardDeliveryStatus.SHIPPING.name -> {
                            StageProgress.INACTIVE
                        }

                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name -> {
                            StageProgress.IN_PROGRESS
                        }

                        else -> StageProgress.INACTIVE
                    })
                }
                PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT -> {
                    return (when (SessionManager.user?.partnerBankStatus) {
                        PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_REQ.status -> {
                            StageProgress.INACTIVE
                        }
                        PartnerBankStatus.ADD_INFO_NOTIFICATION_DONE.status, PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_REQ.status, PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_PROVIDED.status, PartnerBankStatus.ADD_COMPLIANCE_INFO_SUBMITTED_BY_ADMIN.status -> {
                            StageProgress.IN_PROGRESS
                        }

                        PartnerBankStatus.ACTIVATED.status -> {
                            StageProgress.COMPLETED
                        }
                        else -> StageProgress.INACTIVE
                    })
                }
                PaymentCardOnboardingStage.SET_PIN -> {
                    return (when {
                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name && !card.pinCreated && SessionManager.user?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status -> {
                            StageProgress.ACTIVE
                        }
                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name && card.pinCreated && SessionManager.user?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status -> {
                            StageProgress.COMPLETED
                        }
                        else -> StageProgress.INACTIVE
                    })
                }

                PaymentCardOnboardingStage.TOP_UP -> {
                    return (when {
                        card.deliveryStatus == CardDeliveryStatus.SHIPPED.name && SessionManager.user?.partnerBankStatus == PartnerBankStatus.ACTIVATED.status -> {
                            StageProgress.ACTIVE
                        }
                        else -> StageProgress.INACTIVE
                    })
                }

            }
        } ?: return StageProgress.INACTIVE
    }

    private fun getSubheading(stage: PaymentCardOnboardingStage, progress: StageProgress): String {
        return (when (stage) {
            PaymentCardOnboardingStage.SHIPPING -> return (when (progress) {
                StageProgress.ACTIVE, StageProgress.INACTIVE -> getStringHelper(Strings.dashboard_timeline_shipping_stage_description)
                StageProgress.COMPLETED -> getStringHelper(Strings.dashboard_timeline_shipping_stage_completed_description).format(
                    DateUtils.reformatStringDate(
                        SessionManager.card.value?.shipmentDate ?: "",
                        SERVER_DATE_FORMAT,
                        DEFAULT_DATE_FORMAT
                    )
                )
                else -> getStringHelper(Strings.dashboard_timeline_shipping_stage_description)
            })

            PaymentCardOnboardingStage.DELIVERY -> return (when (progress) {
                StageProgress.INACTIVE -> getStringHelper(Strings.dashboard_timeline_delivery_stage_description)
                StageProgress.IN_PROGRESS -> getStringHelper(Strings.dashboard_timeline_delivery_stage_active_description)
                StageProgress.COMPLETED -> getStringHelper(Strings.dashboard_timeline_delivery_stage_completed_description).format(
                    DateUtils.reformatStringDate(
                        SessionManager.user?.partnerBankApprovalDate ?: "",
                        SERVER_DATE_FORMAT,
                        DEFAULT_DATE_FORMAT
                    )
                )

                else -> getStringHelper(Strings.dashboard_timeline_delivery_stage_description)
            })

            PaymentCardOnboardingStage.SET_PIN -> return (when (progress) {
                StageProgress.ACTIVE, StageProgress.INACTIVE -> getStringHelper(Strings.dashboard_timeline_set_pin_stage_description)
                StageProgress.COMPLETED -> getStringHelper(Strings.dashboard_timeline_set_pin_stage_completed_description).format(
                    DateUtils.reformatStringDate(
                        SessionManager.card.value?.activationDate ?: "",
                        SERVER_DATE_FORMAT,
                        DEFAULT_DATE_FORMAT
                    )
                )
                else -> getStringHelper(Strings.dashboard_timeline_set_pin_stage_description)
            })

            PaymentCardOnboardingStage.TOP_UP -> getStringHelper(Strings.dashboard_timeline_top_up_stage_description)

            PaymentCardOnboardingStage.ADDITIONAL_REQUIREMENT -> return (when (progress) {
                StageProgress.INACTIVE -> getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_description)
                StageProgress.IN_PROGRESS -> {
                    return when (SessionManager.user?.partnerBankStatus) {
                        PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_REQ.status, PartnerBankStatus.ADD_INFO_NOTIFICATION_DONE.status -> {
                            getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_description)
                        }
                        PartnerBankStatus.ADDITIONAL_COMPLIANCE_INFO_PROVIDED.status, PartnerBankStatus.ADD_COMPLIANCE_INFO_SUBMITTED_BY_ADMIN.status -> {
                            getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_completed_description).format(
                                DateUtils.reformatStringDate(
                                    SessionManager.user?.additionalDocSubmitionDate ?: "",
                                    SERVER_DATE_FORMAT,
                                    DEFAULT_DATE_FORMAT
                                )
                            )
                        }
                        else -> {
                            getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_description)
                        }
                    }
                }


                StageProgress.COMPLETED -> getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_completed_description).format(
                    DateUtils.reformatStringDate(
                        SessionManager.user?.additionalDocSubmitionDate ?: "",
                        SERVER_DATE_FORMAT,
                        DEFAULT_DATE_FORMAT
                    )
                )
                else -> getStringHelper(Strings.dashboard_timeline_additional_requirement_stage_description)
            })
        })
    }

    private fun openTopUpScreen() {
        getContext().launchActivity<AddMoneyActivity>(type = FeatureSet.TOP_UP)
    }

    private fun openCardDeliveryStatusScreen() {
        getMyFragment().startActivityForResult(
            FragmentPresenterActivity.getIntent(
                getContext(),
                Constants.MODE_STATUS_SCREEN,
                SessionManager.card.value
            ), Constants.EVENT_CREATE_CARD_PIN
        )
    }

    private fun openSetCardPinScreen() {
        getMyFragment().startActivityForResult(
            SessionManager.getPrimaryCard()?.let { card ->
                SetCardPinWelcomeActivity.newIntent(
                    getContext(),
                    card
                )
            }, RequestCodes.REQUEST_FOR_SET_PIN
        )
    }

    private fun getContext(): Context {
        return fragment.requireContext()
    }

    private fun getMyFragment(): Fragment {
        return fragment
    }

    fun notifyAdapter() {
        dashboardNotificationStatusAdapter?.setItemAt(
            2,
            getStatusList()[2]
        )
    }


    private fun openAdditionalRequirementScreen() {
        getMyFragment().launchActivity<AdditionalInfoActivity>(requestCode = RequestCodes.REQUEST_FOR_ADDITIONAL_REQUIREMENT)
    }
}