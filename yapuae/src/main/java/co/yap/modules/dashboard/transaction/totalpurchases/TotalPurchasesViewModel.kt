package co.yap.modules.dashboard.transaction.totalpurchases

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.home.adaptor.TransactionsListingAdapter
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.helpers.TransactionAdapterType

class TotalPurchasesViewModel(application: Application) :
    BaseViewModel<ITotalPurchases.State>(application), ITotalPurchases.ViewModel {
    override val state: TotalPurchaseState = TotalPurchaseState()
    val repository: TransactionsRepository = TransactionsRepository
    override val adapter: TransactionsListingAdapter = TransactionsListingAdapter(
        arrayListOf(),
        TransactionAdapterType.TOTAL_PURCHASE
    )
    override var transaction: ObservableField<Transaction> = ObservableField()
    var list: MutableList<Transaction> = arrayListOf()
    override fun onCreate() {
        super.onCreate()
        adapter.setList(getTotalPurchase())
    }

    private fun getTotalPurchase(): MutableList<Transaction> {
        list.add(
            Transaction(
                title = "Starbucks",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Usa",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Dubai",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Yap",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Pakistan",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Breera",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        list.add(
            Transaction(
                title = "Starbucks Digitify",
                cardType = "DEBIT",
                currency = "AED",
                productCode = "P019",
                txnType = "DEBIT",
                totalAmount = 117.2128,
                creationDate = "2021-05-05T08:54:53",
                updatedDate = "2021-05-05T08:54:53"
            )
        )
        return list
    }
}