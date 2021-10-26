package co.yap.modules.dashboard.transaction.search

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository.searchTransactions
import co.yap.networking.transactions.requestdtos.HomeTransactionsRequest
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.widgets.State
import co.yap.widgets.Status
import co.yap.widgets.advrecyclerview.pagination.PaginatedRecyclerView
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.DateUtils.UTC
import kotlinx.coroutines.cancelChildren

class TransactionSearchViewModel(application: Application) :
    BaseViewModel<ITransactionSearch.State>(application), ITransactionSearch.ViewModel {
    override val state = TransactionSearchState()
    override var transactionAdapter: ObservableField<HomeTransactionAdapter>? = ObservableField()
    override var clickEvent = SingleClickEvent()

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun requestTransactions(
        transactionRequest: HomeTransactionsRequest?,
        isLoadMore: Boolean, apiResponse: ((State?, HomeTransactionListData?) -> Unit?)?
    ) {
        launch {
            if (!isLoadMore)
                state.stateLiveData?.value = State.loading(null)
            when (val response =
                searchTransactions(state.transactionRequest)) {
                is RetroApiResponse.Success -> {
                    if (transactionRequest?.number == 0) {
                        state.transactionMap?.value = null
                    }
                    if (response.data.data.transaction.isNotEmpty()) {
                        state.stateLiveData?.value = State.success(null)
                        var tempMap: MutableMap<String?, List<Transaction>>
                        tempMap =
                            response.data.data.transaction.sortedByDescending { t ->
                                DateUtils.stringToDate(
                                    t.creationDate ?: "",
                                    DateUtils.SERVER_DATE_FORMAT,
                                    UTC
                                )?.time
                            }
                                .distinct().groupBy { t ->
                                    DateUtils.reformatDate(
                                        t.creationDate,
                                        DateUtils.SERVER_DATE_FORMAT,
                                        DateUtils.FORMAT_DATE_MON_YEAR, UTC
                                    )
                                }.toMutableMap()
//                        state.transactionMap?.value =
                        state.transactionMap?.value?.let {
                            mergeReduce(tempMap)
                        } ?: run {
                            state.transactionMap?.value = tempMap
                            transactionAdapter?.get()?.setData(state.transactionMap?.value)
                        }
                    } else {
                        if (state.transactionMap?.value == null) {
                            state.stateLiveData?.value = State.empty(null)
                            transactionAdapter?.get()?.setData(mutableMapOf())
                        } else {
                            state.stateLiveData?.value = State.ideal(null)
                        }
                    }
                    apiResponse?.invoke(state.stateLiveData?.value, response.data.data)
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = response.error.message
                    state.stateLiveData?.value = State.error(null)
                    apiResponse?.invoke(state.stateLiveData?.value, null)
                }
            }
        }
    }

    override fun clearCoroutine() {
        viewModelJob.cancelChildren()
    }

    override fun getPaginationListener(): PaginatedRecyclerView.Pagination? {
        return object : PaginatedRecyclerView.Pagination() {
            override fun onNextPage(page: Int) {
                state.transactionRequest?.number = page
                requestTransactions(state.transactionRequest, page != 0) { state, date ->
                    notifyPageLoaded()
                    if (date?.last == true || state?.status == Status.IDEAL || state?.status == Status.ERROR) {
                        notifyPaginationCompleted()
                    }
                }
            }
        }
    }

    private fun mergeReduce(newMap: MutableMap<String?, List<Transaction>>) {
        state.transactionMap?.value?.let { map ->
            val tempMap = mutableMapOf<String?, List<Transaction>>()
            var keyToRemove: String? = null
            tempMap.putAll(newMap)
            newMap.keys.forEach { key ->
                if (map.containsKey(key)) {
                    keyToRemove = key
                    return@forEach
                }
            }
            keyToRemove?.let {
                val newTransaction = newMap.getValue(it)
                val oldTransaction = map.getValue(it).toMutableList()
                oldTransaction.addAll(newTransaction)
                state.transactionMap?.value!![it] = oldTransaction
                tempMap.remove(it)
            }
            val groupCount = transactionAdapter?.get()?.groupCount ?: 0
            state.transactionMap?.value?.putAll(tempMap)
            transactionAdapter?.get()?.expandableItemManager?.notifyGroupItemRangeInserted(
                groupCount - 1,
                tempMap.size
            )
        }
    }
}
