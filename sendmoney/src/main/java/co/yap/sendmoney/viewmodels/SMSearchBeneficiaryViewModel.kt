package co.yap.sendmoney.viewmodels

import android.app.Application
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.beneficiary.RecentBeneficiariesResponse
import co.yap.networking.customers.responsedtos.sendmoney.GetAllBeneficiaryResponse
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.sendmoney.home.adapters.AllBeneficiariesAdapter
import co.yap.sendmoney.home.interfaces.ISMSearchBeneficiary
import co.yap.sendmoney.home.main.SMBeneficiaryParentBaseViewModel
import co.yap.sendmoney.home.states.SMSearchBeneficiaryState
import co.yap.widgets.State
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.SendMoneyTransferType
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.getLocalContacts
import co.yap.yapcore.helpers.extentions.parseRecentItems
import co.yap.yapcore.helpers.extentions.removeOwnContact
import kotlin.math.ceil

class SMSearchBeneficiaryViewModel(application: Application) :
    SMBeneficiaryParentBaseViewModel<ISMSearchBeneficiary.State>(application),
    ISMSearchBeneficiary.ViewModel, IRepositoryHolder<CustomersRepository> {
    override val state: ISMSearchBeneficiary.State = SMSearchBeneficiaryState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var adapter: AllBeneficiariesAdapter = AllBeneficiariesAdapter(arrayListOf())
    override val repository: CustomersRepository = CustomersRepository

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        adapter.sendMoneyType = parentViewModel?.state?.sendMoneyType?.value
        if (parentViewModel?.state?.sendMoneyType?.value == SendMoneyTransferType.ALL_Y2Y_SM.name) {
            getY2YAndSMBeneficiaries {
                adapter.setList(it.sortedBy { beneficiary -> beneficiary.fullName })
                state.viewState.value = false
                if (it.isNullOrEmpty()) state.stateLiveData?.value = State.error(null)
            }
        } else {
            adapter.setList(parentViewModel?.beneficiariesList?.value ?: arrayListOf())
        }
    }

    private fun getY2YAndSMBeneficiaries(success: (ArrayList<IBeneficiary>) -> Unit) {
        fetchCombinedBeneficiariesApis { sendMoneyBeneficiariesResponse, y2yRecentsResponse ->
            launch(Dispatcher.Main) {
                val combinedList: ArrayList<IBeneficiary> = arrayListOf()
                val combinedY2YAndRecentsList: ArrayList<IBeneficiary> = arrayListOf()
                when (sendMoneyBeneficiariesResponse) {
                    is RetroApiResponse.Success -> {
                        combinedList.addAll(sendMoneyBeneficiariesResponse.data.data)
                    }
                }
                when (y2yRecentsResponse) {
                    is RetroApiResponse.Success -> {
                        y2yRecentsResponse.data.data.parseRecentItems(context)
                        combinedY2YAndRecentsList.addAll(
                            y2yRecentsResponse.data.data ?: arrayListOf()
                        )
                    }
                }
                getLocalContactsFromServer { y2yBeneficiaries ->
                    launch {
                        y2yBeneficiaries.forEach {
                            it.mobileNo =
                                Utils.getFormattedPhoneNumber(context, it.countryCode + it.mobileNo)
                        }
                        combinedY2YAndRecentsList.addAll(y2yBeneficiaries.filter { it.yapUser == true })
                        val distinctY2YList =
                            combinedY2YAndRecentsList.distinctBy { it.accountUUID }
                        combinedList.addAll(distinctY2YList)
                        success(combinedList)
                    }
                }
            }
        }
    }

    private fun fetchCombinedBeneficiariesApis(
        responses: (RetroApiResponse<GetAllBeneficiaryResponse>?, RetroApiResponse<RecentBeneficiariesResponse>) -> Unit
    ) {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
                val deferredSM =launchAsync { repository.getAllBeneficiaries() }
                val deferredY2YRecents = launchAsync { repository.getRecentY2YBeneficiaries() }
                responses(deferredSM.await(), deferredY2YRecents.await())
        }
    }
    private suspend fun getLocalContactsFromServer(contactsList: (List<Contact>) -> Unit) {
        launch(Dispatcher.LongOperation) {
            val localContacts = getLocalContacts(context).removeOwnContact()
            if (localContacts.isEmpty()) {
                contactsList.invoke(mutableListOf())
            } else {
                val combineContacts = arrayListOf<Contact>()
                val threshold = 3000
                var lastCount = 0
                val numberOfIteration =
                    ceil((localContacts.size.toDouble()) / threshold.toDouble()).toInt()
                for (x in 1..numberOfIteration) {
                    val itemsToPost = localContacts.subList(
                        lastCount,
                        if ((x * threshold) > localContacts.size) localContacts.size else x * threshold
                    )
                    getY2YFromServer(itemsToPost) { contacts ->
                        contacts?.let { combineContacts.addAll(it) }
                        if (combineContacts.size >= localContacts.size) {
                            combineContacts.sortBy { it.title }
                            contactsList.invoke(combineContacts)
                        }
                    }

                    lastCount = x * threshold
                }
            }
        }
    }

    private suspend fun getY2YFromServer(
        localList: MutableList<Contact>,
        success: (List<Contact>?) -> Unit
    ) {
        when (val response =
            repository.getY2YBeneficiaries(localList)) {
            is RetroApiResponse.Success -> {
                success.invoke(response.data.data)
            }
            is RetroApiResponse.Error -> {
                success.invoke(emptyList())
            }
        }
    }
}