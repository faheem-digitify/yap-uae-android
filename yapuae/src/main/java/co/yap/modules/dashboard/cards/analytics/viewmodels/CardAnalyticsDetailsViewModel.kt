package co.yap.modules.dashboard.cards.analytics.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.modules.dashboard.cards.analytics.interfaces.ICardAnalyticsDetails
import co.yap.modules.dashboard.cards.analytics.main.viewmodels.CardAnalyticsBaseViewModel
import co.yap.modules.dashboard.cards.analytics.states.CardAnalyticsDetailsState
import co.yap.modules.dashboard.home.adaptor.TransactionsListingAdapter
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.networking.transactions.responsedtos.transaction.TransactionAnalyticsDetailsResponse
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.TransactionAdapterType
import co.yap.yapcore.managers.SessionManager
import kotlin.math.abs

class CardAnalyticsDetailsViewModel(application: Application) :
    CardAnalyticsBaseViewModel<ICardAnalyticsDetails.State>(application),
    ICardAnalyticsDetails.ViewModel {
    override val state = CardAnalyticsDetailsState()
    override val adapter: TransactionsListingAdapter = TransactionsListingAdapter(
        arrayListOf(),
        TransactionAdapterType.ANALYTICS_DETAILS
    )
    override var transactionResponse: TransactionAnalyticsDetailsResponse =
        TransactionAnalyticsDetailsResponse()
    val repository: TransactionsRepository = TransactionsRepository
    var list: MutableList<Transaction> = arrayListOf()
    var viewState: MutableLiveData<Int> = MutableLiveData(Constants.EVENT_LOADING)
    override var yapCategoryId: ObservableField<ArrayList<Any>> = ObservableField()

    override var clickEvent: SingleClickEvent? = SingleClickEvent()

    override fun handleOnClickEvent(id: Int) {
        clickEvent?.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()

        setToolBarTitle(state.title.get()?.trim() ?: "Analytics")
        adapter.analyticsItemPosition = parentViewModel?.selectedItemPosition?.value ?: 0
    }

    override fun fetchMerchantTransactions(merchantType: String, currentDate: String) {
        launch {
            when (val response = repository.getTransactionsOfMerchant(
                merchantType,
                SessionManager.getCardSerialNumber(),
                parentViewModel?.state?.currentSelectedDate,
                if (merchantType.equals("merchant-name")) {
                    state.categories.get()
                } else yapCategoryId.get()
            )
            ) {
            is RetroApiResponse.Success -> {
            response.data.data?.let { resp ->
                transactionResponse = resp
                state.avgSpending.set("${transactionResponse.averageSpending}")
                transactionResponse.currentToLastMonth?.let {
                    state.currToLast.set(
                        when {
                            it < 0 -> "-${abs(it)}"
                            it > 0 -> "+${abs(it)}"
                            else -> "${abs(it)}"
                        }
                    )
                }
                if (!transactionResponse.txnAnalytics.isNullOrEmpty()) {
                    viewState.value = Constants.EVENT_CONTENT
                    list = transactionResponse.txnAnalytics ?: arrayListOf()
                    list.sortByDescending {
                        it.creationDate
                    }
                    adapter.setList(list)
                } else viewState.value = Constants.EVENT_EMPTY
            }
        }
            is RetroApiResponse.Error -> {
            state.toast = response.error.message
            viewState.value = Constants.EVENT_EMPTY
        }
        }
        }
    }
    override fun getConcatinatedString(count: Int): String {
        var concatenatedString = ""
        var date = parentViewModel?.state?.currentSelectedMonth ?: ""
        if (date.contains(",")) date = date.replace(",", "")
        concatenatedString =
            "$date ・ $count ${getString(Strings.screen_yap_analytics_detail_transaction_count)}"
        return concatenatedString
    }
}