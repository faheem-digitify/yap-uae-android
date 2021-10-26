package co.yap.sendmoney.home.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.sendmoney.home.adapters.AllBeneficiariesAdapter
import co.yap.sendmoney.home.interfaces.ISMBeneficiaries
import co.yap.sendmoney.home.main.SMBeneficiaryParentBaseViewModel
import co.yap.sendmoney.home.states.SendMoneyHomeState
import co.yap.translation.Strings
import co.yap.widgets.recent_transfers.CoreRecentTransferAdapter
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.helpers.extentions.parseRecentItems
import co.yap.yapcore.managers.SessionManager

class SMBeneficiariesViewModel(application: Application) :
    SMBeneficiaryParentBaseViewModel<ISMBeneficiaries.State>(application), ISMBeneficiaries.ViewModel,
    IRepositoryHolder<CustomersRepository> {

    override val repository: CustomersRepository = CustomersRepository
    override val state: SendMoneyHomeState = SendMoneyHomeState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var recentTransferData: MutableLiveData<List<Beneficiary>> = MutableLiveData()
    override var recentsAdapter: CoreRecentTransferAdapter = CoreRecentTransferAdapter(
        context,
        mutableListOf()
    )
    override var beneficiariesAdapter: AllBeneficiariesAdapter = AllBeneficiariesAdapter(
        mutableListOf()
    )

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        SessionManager.getCurrenciesFromServer { _, _ -> }
    }

    override fun onResume() {
        super.onResume()
        setToolBarTitle(getString(Strings.screen_send_money_display_text_title))
    }

    override fun requestRecentBeneficiaries(sendMoneyType: String) {
        launch {
            when (val response = repository.getRecentBeneficiaries()) {
                is RetroApiResponse.Success -> {
                    state.loading = false
                    val filteredList =
                        parentViewModel?.getBeneficiariesOfType(sendMoneyType, response.data.data) ?: arrayListOf()
                    if (filteredList.isNullOrEmpty())
                        state.isNoRecentBeneficiary.set(true)
                    else
                        state.isNoRecentBeneficiary.set(false)

                    filteredList.parseRecentItems()
                    recentsAdapter.setList(filteredList)
                }

                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = "${response.error.message}^${AlertType.DIALOG.name}"
                }
            }
        }
    }
}