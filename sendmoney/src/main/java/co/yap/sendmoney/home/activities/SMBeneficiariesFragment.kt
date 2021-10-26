package co.yap.sendmoney.home.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.sendmoney.BR
import co.yap.sendmoney.R
import co.yap.sendmoney.activities.SendMoneyHomeActivity
import co.yap.sendmoney.databinding.ActivitySendMoneyLandingBinding
import co.yap.sendmoney.editbeneficiary.activity.EditBeneficiaryActivity
import co.yap.sendmoney.fundtransfer.activities.BeneficiaryFundTransferActivity
import co.yap.sendmoney.home.adapters.AllBeneficiariesAdapter
import co.yap.sendmoney.home.interfaces.ISMBeneficiaries
import co.yap.sendmoney.home.main.SMBeneficiaryParentBaseFragment
import co.yap.sendmoney.home.viewmodels.SMBeneficiariesViewModel
import co.yap.sendmoney.y2y.home.activities.YapToYapDashboardActivity
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.EXTRA
import co.yap.yapcore.constants.Constants.OVERVIEW_BENEFICIARY
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.constants.RequestCodes.REQUEST_TRANSFER_MONEY
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.confirm
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlinx.android.synthetic.main.layout_beneficiaries.*
import kotlinx.android.synthetic.main.layout_item_beneficiary.*

class SMBeneficiariesFragment : SMBeneficiaryParentBaseFragment<ISMBeneficiaries.ViewModel>(),
    ISMBeneficiaries.View {

    private var onTouchListener: RecyclerTouchListener? = null
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.activity_send_money_landing

    override val viewModel: SMBeneficiariesViewModel
        get() = ViewModelProviders.of(this).get(SMBeneficiariesViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.sendMoneyType.set(viewModel.parentViewModel?.state?.sendMoneyType?.value)
        if (viewModel.state.sendMoneyType.get() == SendMoneyTransferType.ALL_Y2Y_SM.name) {
            skipCurrentFragment()
        } else {
            viewModel.beneficiariesAdapter.sendMoneyType =
                viewModel.parentViewModel?.state?.sendMoneyType?.value
            viewModel.parentViewModel?.requestAllBeneficiaries(
                viewModel.state.sendMoneyType.get() ?: ""
            )
            viewModel.requestRecentBeneficiaries(viewModel.state.sendMoneyType.get() ?: "")
            setObservers()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.state.sendMoneyType.get() != SendMoneyTransferType.ALL_Y2Y_SM.name) {
            initComponents()
        }
    }

    private fun initComponents() {
        viewModel.recentsAdapter.allowFullItemClickListener = true
        viewModel.recentsAdapter.setItemListener(recentItemClickListener)
    }

    private fun setObservers() {
        viewModel.clickEvent.observe(this, clickListener)
        viewModel.parentViewModel?.beneficiariesList?.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                // show and hide views for no beneficiary
                viewModel.state.isNoBeneficiary.set(true)
                viewModel.state.hasBeneficiary.set(false)
            } else {
                initSwipeListener()
                viewModel.state.isNoBeneficiary.set(false)
                viewModel.state.hasBeneficiary.set(true)
                getAdaptor().setList(it)
            }
        })

    }

    private val recentItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is Beneficiary)
                startMoneyTransfer(data, pos)
        }
    }

    private fun initSwipeListener() {
        activity?.let { activity ->
            onTouchListener =
                RecyclerTouchListener(activity, getBinding().layoutBeneficiaries.rvAllBeneficiaries)
                    .setClickable(
                        object : RecyclerTouchListener.OnRowClickListener {
                            override fun onRowClicked(position: Int) {
                                viewModel.clickEvent.setPayload(
                                    SingleClickEvent.AdaptorPayLoadHolder(
                                        foregroundContainer,
                                        getAdaptor().getDataForPosition(position),
                                        position
                                    )
                                )
                                viewModel.clickEvent.setValue(foregroundContainer.id)
                            }

                            override fun onIndependentViewClicked(
                                independentViewID: Int,
                                position: Int
                            ) {
                            }
                        }).setSwipeOptionViews(R.id.btnEdit, R.id.btnDelete)
                    .setSwipeable(
                        R.id.foregroundContainer, R.id.swipe
                    )
                    { viewID, position ->
                        viewModel.clickEvent.setPayload(
                            SingleClickEvent.AdaptorPayLoadHolder(
                                activity.findViewById(viewID),
                                getAdaptor().getDataForPosition(position),
                                position
                            )
                        )
                        viewModel.clickEvent.setValue(viewID)
                    }
            rvAllBeneficiaries.addOnItemTouchListener(onTouchListener!!)
        }
    }

    private fun startMoneyTransfer(beneficiary: Beneficiary?, position: Int) {
        trackEventWithScreenName(FirebaseEvent.CLICK_BENEFICIARY)
        launchActivityForActivityResult<BeneficiaryFundTransferActivity>(
            requestCode = REQUEST_TRANSFER_MONEY,
            type = beneficiary.getBeneficiaryTransferType()
        ) {
            putExtra(Constants.BENEFICIARY, beneficiary)
            putExtra(Constants.POSITION, position)
            putExtra(Constants.IS_NEW_BENEFICIARY, false)
        }
    }

    private fun startY2YTransfer(
        beneficiary: Beneficiary?,
        position: Int = 0
    ) {
        launchActivity<YapToYapDashboardActivity>(type = FeatureSet.Y2Y_TRANSFER) {
            putExtra(Beneficiary::class.java.name, beneficiary)
            putExtra(ExtraKeys.Y2Y_BENEFICIARY_POSITION.name, position)
        }
    }

    private fun openEditBeneficiary(beneficiary: Beneficiary?) {
        beneficiary?.let {
            trackEventWithScreenName(FirebaseEvent.EDIT_BENEFICIARY)
            val bundle = Bundle()
            bundle.putBoolean(OVERVIEW_BENEFICIARY, false)
            bundle.putString(Constants.IS_IBAN_NEEDED, "loadFromServer")
            bundle.putParcelable(Beneficiary::class.java.name, beneficiary)
            launchActivity<EditBeneficiaryActivity>(
                requestCode = RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST,
                type = FeatureSet.EDIT_SEND_MONEY_BENEFICIARY
            ) {
                putExtra(EXTRA, bundle)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        onTouchListener?.let { rvAllBeneficiaries.addOnItemTouchListener(it) }
    }

    override fun onPause() {
        onTouchListener?.let { rvAllBeneficiaries.removeOnItemTouchListener(it) }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clickEvent.removeObservers(this)
    }

    private val clickListener = Observer<Int> {
        when (it) {
            R.id.addContactsButton -> {
                startAddBeneficiaryFlow()
            }
            R.id.layoutSearchView -> {
                navigate(R.id.action_sendMoneyLandingActivity_to_searchBeneficiariesFragment)
            }
            R.id.foregroundContainer -> {
                viewModel.clickEvent.getPayload()?.let { payload ->
                    when (payload.itemData) {
                        is Beneficiary -> {
                            startMoneyTransfer(payload.itemData as Beneficiary, payload.position)
                        }
                        is Contact -> {
                            startY2YTransfer(
                                viewModel.parentViewModel?.getBeneficiaryFromContact(payload.itemData as Contact),
                                payload.position
                            )
                        }
                    }
                }
                viewModel.clickEvent.setPayload(null)

            }
            R.id.btnEdit -> {
                viewModel.clickEvent.getPayload()?.let { payload ->
                    if (payload.itemData is Beneficiary) {
                        openEditBeneficiary(payload.itemData as Beneficiary)
                    }
                }
                viewModel.clickEvent.setPayload(null)
            }

            R.id.btnDelete -> {
                viewModel.clickEvent.getPayload()?.let { payload ->
                    if (payload.itemData is Beneficiary) deleteBeneficiary(
                        payload.itemData as Beneficiary,
                        payload.position
                    )

                }
                viewModel.clickEvent.setPayload(null)
            }
            R.id.tvCancel, R.id.tbBtnBack -> activity?.finish()
        }
    }

    private fun deleteBeneficiary(beneficiary: Beneficiary, position: Int) {
        if (SessionManager.user?.otpBlocked == true) {
            showBlockedFeatureAlert(
                requireActivity(),
                FeatureSet.DELETE_SEND_MONEY_BENEFICIARY
            )
        } else {
            confirmDeleteBeneficiary(beneficiary, position)
        }
    }

    private fun confirmDeleteBeneficiary(beneficiary: Beneficiary, position: Int) {
        confirm(
            message = getString(Strings.screen_send_money_display_text_delete_message),
            title = getString(Strings.screen_send_money_display_text_delete),
            positiveButton = getString(Strings.common_button_yes),
            negativeButton = getString(Strings.common_button_cancel)
        ) {
            viewModel.parentViewModel?.requestDeleteBeneficiary(beneficiary.id.toString()) {
                trackEventWithScreenName(FirebaseEvent.DELETE_BENEFICIARY)
                viewModel.parentViewModel?.beneficiariesList?.value?.remove(beneficiary)
                viewModel.beneficiariesAdapter.removeItemAt(position)
            }
        }
    }

    private fun getAdaptor(): AllBeneficiariesAdapter {
        return viewModel.beneficiariesAdapter
    }

    private fun getBinding(): ActivitySendMoneyLandingBinding {
        return (viewDataBinding as ActivitySendMoneyLandingBinding)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST -> {
                        if (data.getBooleanExtra(Constants.BENEFICIARY_CHANGE, false)) {
                            val isMoneyTransfer =
                                data.getValue(Constants.IS_TRANSFER_MONEY, "BOOLEAN") as? Boolean
                            val isDismissFlow =
                                data.getValue(
                                    Constants.TERMINATE_ADD_BENEFICIARY,
                                    "BOOLEAN"
                                ) as? Boolean
                            val beneficiary =
                                data.getValue(
                                    Beneficiary::class.java.name,
                                    "PARCEABLE"
                                ) as? Beneficiary
                            when {
                                isMoneyTransfer == true -> {
                                    beneficiary?.let {
                                        startMoneyTransfer(it, 0)
                                        viewModel.parentViewModel?.requestAllBeneficiaries(
                                            viewModel.state.sendMoneyType.get() ?: ""
                                        )
                                    }
                                }
                                isDismissFlow == true -> {
                                }
                                else -> viewModel.parentViewModel?.requestAllBeneficiaries(
                                    viewModel.state.sendMoneyType.get() ?: ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> activity?.finish()
            R.id.ivRightIcon -> {
                startAddBeneficiaryFlow()
            }
        }
    }

    private fun startAddBeneficiaryFlow() {
        trackEventWithScreenName(FirebaseEvent.ADD_BENEFICIARY)
        launchActivity<SendMoneyHomeActivity>(
            requestCode = RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST,
            type = FeatureSet.ADD_SEND_MONEY_BENEFICIARY
        ) {
            putExtra(
                ExtraKeys.SEND_MONEY_TYPE.name,
                viewModel.state.sendMoneyType.get()
            )
        }
    }

    private fun skipCurrentFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.sendMoneyLandingActivity, true) // starting destination skiped
            .build()

        navigate(
            destinationId = R.id.action_sendMoneyLandingActivity_to_searchBeneficiariesFragment,
            args = null,
            navOptions = navOptions
        )
    }
}