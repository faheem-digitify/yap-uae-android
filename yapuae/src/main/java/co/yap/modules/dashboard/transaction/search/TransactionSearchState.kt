package co.yap.modules.dashboard.transaction.search

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.networking.transactions.requestdtos.HomeTransactionsRequest
import co.yap.networking.transactions.requestdtos.REQUEST_PAGE_SIZE
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.widgets.State
import co.yap.yapcore.BaseState

class TransactionSearchState : BaseState(), ITransactionSearch.State {
    override val transactionList: ObservableField<MutableList<HomeTransactionListData>> =
        ObservableField(mutableListOf())
    override var transactionMap: MutableLiveData<MutableMap<String?, List<Transaction>>>? =
        MutableLiveData()
    override var homeTransactionRequest: MutableLiveData<HomeTransactionsRequest>? =
        MutableLiveData()
    override var transactionRequest: HomeTransactionsRequest? =
        HomeTransactionsRequest(
            size = REQUEST_PAGE_SIZE,
            searchField = null
        )
    override var stateLiveData: MutableLiveData<State>? = MutableLiveData()
}
