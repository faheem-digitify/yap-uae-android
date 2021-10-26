package co.yap.sendmoney.home.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.enums.SendMoneyBeneficiaryType
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.managers.SessionManager

class SMBeneficiaryParentViewModel(application: Application) :
    SMBeneficiaryParentBaseViewModel<ISMBeneficiaryParent.State>(application = application),
    ISMBeneficiaryParent.ViewModel, IRepositoryHolder<CustomersRepository> {
    override var beneficiariesList: MutableLiveData<ArrayList<IBeneficiary>> = MutableLiveData()
    override val state: SMBeneficiaryParentState = SMBeneficiaryParentState()
    override val repository: CustomersRepository = CustomersRepository

    override fun requestDeleteBeneficiary(beneficiaryId: String, completion: () -> Unit) {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
            val response = repository.deleteBeneficiaryFromList(beneficiaryId)
            launch(Dispatcher.Main) {
                when (response) {
                    is RetroApiResponse.Success -> {
                        state.viewState.value = false
                        state.viewState.value = "Deleted Successfully"
                        completion.invoke()
                    }

                    is RetroApiResponse.Error -> {
                        state.viewState.value = false
                        state.viewState.value = "${response.error.message}^${AlertType.DIALOG.name}"
                    }
                }
            }
        }
    }

    override fun requestAllBeneficiaries(sendMoneyType: String, completion: () -> Unit) {
        launch {
            when (val response = repository.getAllBeneficiaries()) {
                is RetroApiResponse.Success -> {
                    state.loading = false
                    val filteredList = getBeneficiariesOfType(sendMoneyType, response.data.data)
                    beneficiariesList.value =
                        filteredList as ArrayList<IBeneficiary>
                    completion.invoke()
                }

                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = "${response.error.message}^${AlertType.DIALOG.name}"
                }
            }
        }
    }

    override fun getBeneficiariesOfType(type: String, list: List<Beneficiary>): List<Beneficiary> {
        return when (type) {
            SendMoneyTransferType.HOME_COUNTRY.name -> {
                list.filter { it.country == SessionManager.homeCountry2Digit }
            }
            SendMoneyTransferType.INTERNATIONAL.name -> {
                list.filter { (it.beneficiaryType == SendMoneyBeneficiaryType.RMT.type || it.beneficiaryType == SendMoneyBeneficiaryType.SWIFT.type) && it.country != SessionManager.homeCountry2Digit }
            }
            SendMoneyTransferType.LOCAL.name -> {
                list.filter { it.beneficiaryType == SendMoneyBeneficiaryType.UAEFTS.type || it.beneficiaryType == SendMoneyBeneficiaryType.DOMESTIC.type }
            }
            else -> list
        }
    }

    override fun getBeneficiaryFromContact(beneficiary: IBeneficiary): Beneficiary {
        return Beneficiary(
            beneficiaryUuid = beneficiary.accountUUID,
            beneficiaryPictureUrl = beneficiary.imgUrl,
            title = beneficiary.fullName
        )
    }
}
