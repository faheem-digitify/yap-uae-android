package co.yap.sendmoney.y2y.main.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.beneficiary.RecentBeneficiariesResponse
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.sendmoney.y2y.main.interfaces.IY2Y
import co.yap.sendmoney.y2y.main.states.Y2YState
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.getLocalContacts
import co.yap.yapcore.helpers.extentions.parseRecentItems
import co.yap.yapcore.helpers.extentions.removeOwnContact
import co.yap.yapcore.managers.SessionManager
import kotlin.math.ceil

class Y2YViewModel(application: Application) : BaseViewModel<IY2Y.State>(application),
    IY2Y.ViewModel, IRepositoryHolder<CustomersRepository> {

    override val yapContactLiveData: MutableLiveData<List<IBeneficiary>> = MutableLiveData()
    override val y2yRecentBeneficiries: MutableLiveData<List<IBeneficiary>> = MutableLiveData()
    override val y2yBeneficiries: MutableLiveData<List<IBeneficiary>> = MutableLiveData()
    override var isSearching: MutableLiveData<Boolean> = MutableLiveData(false)
    override var selectedTabPos: MutableLiveData<Int> = MutableLiveData(0)
    override val searchQuery: MutableLiveData<String> = MutableLiveData("")
    override var errorEvent: MutableLiveData<String> = MutableLiveData()
    override var beneficiary: Beneficiary? = null
    override var position: Int = 0
    override val state: Y2YState = Y2YState()
    override val repository: CustomersRepository = CustomersRepository

    override fun onCreate() {
        super.onCreate()
        SessionManager.getCurrenciesFromServer { _, _ -> }
    }

    override fun getY2YAndY2YRecentBeneficiaries(success: (List<IBeneficiary>) -> Unit) {
        fetchCombinedBeneficiariesApis { y2yRecentResponse ->
            launch(Dispatcher.Main) {
                when (y2yRecentResponse) {
                    is RetroApiResponse.Success -> {
                        y2yRecentResponse.data.data.parseRecentItems(context)
                        y2yRecentBeneficiries.value = y2yRecentResponse.data.data
                        success.invoke(y2yRecentBeneficiries.value ?: arrayListOf())
                    }
                    is RetroApiResponse.Error -> {
                    }
                }
                getLocalContactsFromServer { y2yBeneficiaries ->
                    launch {
                        y2yBeneficiaries.forEach {
                            it.mobileNo =
                                Utils.getFormattedPhoneNumber(context, it.countryCode + it.mobileNo)
                        }
                        val partitionedContactList =
                            y2yBeneficiaries.partition { it.yapUser == true }
                        val contacts = composingYapContacts(
                            partitionedContactList,
                            y2yRecentBeneficiries.value ?: arrayListOf()
                        )
                        y2yBeneficiries.value = contacts.first
                        yapContactLiveData.value = contacts.second

                    }
                }
            }
        }
    }

    private fun composingYapContacts(
        contactsList: Pair<List<Contact>, List<Contact>>,
        recents: List<IBeneficiary>
    ): Pair<List<IBeneficiary>, List<IBeneficiary>> {
        var yapContacts: ArrayList<IBeneficiary> = arrayListOf()
        yapContacts.addAll(contactsList.first)
        yapContacts.addAll(recents)
        yapContacts =
            yapContacts.distinctBy { it.accountUUID } as ArrayList<IBeneficiary>
        yapContacts.sortBy { it.fullName }

        val allContacts: ArrayList<IBeneficiary> = arrayListOf()
        allContacts.addAll(contactsList.second)
        allContacts.addAll(yapContacts)
        allContacts.sortBy { it.fullName }

        return Pair(yapContacts, allContacts)
    }

    private fun fetchCombinedBeneficiariesApis(
        responses: (RetroApiResponse<RecentBeneficiariesResponse>?) -> Unit
    ) {
        launch(Dispatcher.Background) {
//            coroutineScope {
                val deferredSM = launchAsync{ repository.getRecentY2YBeneficiaries() }
                responses(deferredSM.await())
//            }
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