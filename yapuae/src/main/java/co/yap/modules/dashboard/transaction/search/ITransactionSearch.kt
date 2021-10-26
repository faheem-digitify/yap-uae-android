package co.yap.modules.dashboard.transaction.search

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.networking.transactions.requestdtos.HomeTransactionsRequest
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.widgets.advrecyclerview.pagination.PaginatedRecyclerView
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ITransactionSearch {
    interface View : IBase.View<ViewModel> {
    }

    interface ViewModel : IBase.ViewModel<State> {
        fun handlePressOnView(id: Int)
        var clickEvent: SingleClickEvent
        fun clearCoroutine()
        fun getPaginationListener(): PaginatedRecyclerView.Pagination?
        fun requestTransactions(
            transactionRequest: HomeTransactionsRequest?,
            isLoadMore: Boolean = false,
            apiResponse: ((co.yap.widgets.State?, HomeTransactionListData?) -> Unit?)? = null
        )

        val transactionAdapter: ObservableField<HomeTransactionAdapter>?
    }

    interface State : IBase.State {
        val transactionList: ObservableField<MutableList<HomeTransactionListData>>?
        var transactionMap: MutableLiveData<MutableMap<String?, List<Transaction>>>?
        var homeTransactionRequest: MutableLiveData<HomeTransactionsRequest>?
        var transactionRequest: HomeTransactionsRequest?
        // var stateLiveData: MutableLiveData<co.yap.widgets.State>?
    }
}
