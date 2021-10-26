package co.yap.sendmoney.home.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.sendmoney.BR
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.FragmentSearchBeneficiaryBinding
import co.yap.sendmoney.editbeneficiary.activity.EditBeneficiaryActivity
import co.yap.sendmoney.fundtransfer.activities.BeneficiaryFundTransferActivity
import co.yap.sendmoney.home.interfaces.ISMSearchBeneficiary
import co.yap.sendmoney.home.main.SMBeneficiaryParentBaseFragment
import co.yap.sendmoney.viewmodels.SMSearchBeneficiaryViewModel
import co.yap.sendmoney.y2y.home.activities.YapToYapDashboardActivity
import co.yap.translation.Strings
import co.yap.widgets.MultiStateView
import co.yap.widgets.State
import co.yap.widgets.Status
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.confirm
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.managers.SessionManager
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlinx.android.synthetic.main.layout_item_beneficiary.*

class SearchBeneficiariesFragment :
    SMBeneficiaryParentBaseFragment<ISMSearchBeneficiary.ViewModel>(),
    ISMSearchBeneficiary.View {
    private var onTouchListener: RecyclerTouchListener? = null
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_search_beneficiary

    override val viewModel: SMSearchBeneficiaryViewModel
        get() = ViewModelProviders.of(this).get(SMSearchBeneficiaryViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        initSwipeListener()
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickListener)
        getBindings().etSearch.afterTextChanged {
            viewModel.adapter.filter.filter(it)
        }
        viewModel.state.stateLiveData?.observe(this, Observer { handleState(it) })
        viewModel.adapter.filterCount.observe(this, Observer {
            viewModel.state.stateLiveData?.value =
                if (it == 0) State.empty("") else State.success("")
        })
    }

    private fun initSwipeListener() {
        activity?.let { activity ->
            onTouchListener =
                RecyclerTouchListener(activity, getBindings().rvAllBeneficiaries)
                    .setClickable(
                        object : RecyclerTouchListener.OnRowClickListener {
                            override fun onRowClicked(position: Int) {
                                viewModel.clickEvent.setPayload(
                                    SingleClickEvent.AdaptorPayLoadHolder(
                                        foregroundContainer,
                                        viewModel.adapter.getDataForPosition(position),
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
                                viewModel.adapter.getDataForPosition(position),
                                position
                            )
                        )
                        viewModel.clickEvent.setValue(viewID)
                    }

            onTouchListener?.setSwipeable(viewModel.parentViewModel?.state?.sendMoneyType?.value != SendMoneyTransferType.ALL_Y2Y_SM.name)
        }
    }

    private val clickListener = Observer<Int> {
        when (it) {
            R.id.tvCancel -> {
                Utils.hideKeyboard(getBindings().etSearch)
                if (viewModel.parentViewModel?.state?.sendMoneyType?.value == SendMoneyTransferType.ALL_Y2Y_SM.name) {
                    requireActivity().finish()
                } else {
                    navigateBack()
                }
            }
            R.id.foregroundContainer -> {
                viewModel.clickEvent.getPayload()?.let { payload ->
                    when (payload.itemData) {
                        is IBeneficiary -> {
                            if ((payload.itemData as IBeneficiary).isYapUser) {
                                startY2YTransfer(
                                    viewModel.parentViewModel?.getBeneficiaryFromContact(payload.itemData as IBeneficiary),
                                    payload.position
                                )
                            } else
                                startMoneyTransfer(
                                    payload.itemData as Beneficiary,
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
                viewModel.parentViewModel?.beneficiariesList?.value =
                    viewModel.parentViewModel?.beneficiariesList?.value
                viewModel.adapter.removeItemAt(position)
            }
        }
    }

    private fun startMoneyTransfer(beneficiary: Beneficiary?, position: Int) {
        Utils.hideKeyboard(getBindings().etSearch)
        trackEventWithScreenName(FirebaseEvent.CLICK_BENEFICIARY)
        launchActivityForActivityResult<BeneficiaryFundTransferActivity>(
            requestCode = RequestCodes.REQUEST_TRANSFER_MONEY,
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
        launchActivity<YapToYapDashboardActivity>(
            requestCode = RequestCodes.REQUEST_Y2Y_TRANSFER,
            type = FeatureSet.Y2Y_TRANSFER
        ) {
            putExtra(Beneficiary::class.java.name, beneficiary)
            putExtra(ExtraKeys.Y2Y_BENEFICIARY_POSITION.name, position)
        }
    }

    private fun openEditBeneficiary(beneficiary: Beneficiary?) {
        Utils.hideKeyboard(getBindings().etSearch)
        beneficiary?.let {
            trackEventWithScreenName(FirebaseEvent.EDIT_BENEFICIARY)
            val bundle = Bundle()
            bundle.putBoolean(Constants.OVERVIEW_BENEFICIARY, false)
            bundle.putString(Constants.IS_IBAN_NEEDED, "loadFromServer")
            bundle.putParcelable(Beneficiary::class.java.name, beneficiary)
            launchActivity<EditBeneficiaryActivity>(
                requestCode = RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST,
                type = FeatureSet.EDIT_SEND_MONEY_BENEFICIARY
            ) {
                putExtra(Constants.EXTRA, bundle)
            }
        }
    }

    private fun handleState(state: State?) {
        when (state?.status) {
            Status.EMPTY -> {
                getBindings().multiStateView.viewState = MultiStateView.ViewState.EMPTY
            }
            Status.ERROR -> {
                getBindings().multiStateView.viewState = MultiStateView.ViewState.ERROR
                getBindings().rvAllBeneficiaries.showOriginalAdapter()
            }
            Status.SUCCESS -> {
                getBindings().multiStateView.viewState = MultiStateView.ViewState.CONTENT
            }
            else -> throw IllegalStateException("Provided multi state is not handled $state")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST -> {
                if (data?.getBooleanExtra(Constants.BENEFICIARY_CHANGE, false) == true) {
                    viewModel.parentViewModel?.requestAllBeneficiaries(
                        viewModel.parentViewModel?.state?.sendMoneyType?.value ?: ""
                    ) {
                        viewModel.adapter.setList(
                            viewModel.parentViewModel?.beneficiariesList?.value ?: arrayListOf()
                        )
                    }
                }
            }
            RequestCodes.REQUEST_Y2Y_TRANSFER -> {
                if (data?.getBooleanExtra(Constants.MONEY_TRANSFERED, false) == true) {
                    setResultData()
                }
            }
        }
    }

    private fun setResultData() {
        val intent = Intent()
        intent.putExtra(Constants.MONEY_TRANSFERED, true)
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    override fun onPause() {
        super.onPause()
        onTouchListener?.let { getBindings().rvAllBeneficiaries.removeOnItemTouchListener(it) }
    }

    override fun onResume() {
        super.onResume()
        onTouchListener?.let { getBindings().rvAllBeneficiaries.addOnItemTouchListener(it) }
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
        viewModel.state.stateLiveData?.removeObservers(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    private fun getBindings(): FragmentSearchBeneficiaryBinding {
        return viewDataBinding as FragmentSearchBeneficiaryBinding
    }
}