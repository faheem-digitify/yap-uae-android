package co.yap.modules.dashboard.transaction.detail

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import co.yap.yapuae.BR
import co.yap.modules.dashboard.transaction.detail.models.TransactionDetail
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.yapcore.BaseState

class TransactionDetailsState : BaseState(), ITransactionDetails.State {

    @get:Bindable
    override var transactionNoteDate: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.transactionNoteDate)
        }
    override var spentVisibility: ObservableField<Boolean> = ObservableField(false)
    override var receiptTitle: ObservableField<String> = ObservableField("")
    override var txnNoteValue: ObservableField<String> = ObservableField()
    override var isTransferTxn: ObservableField<Boolean> = ObservableField(false)
    override var noteVisibility: ObservableBoolean = ObservableBoolean(false)
    override var receiptVisibility: ObservableBoolean = ObservableBoolean(false)
    override var isTransactionInProcessOrRejected: ObservableBoolean = ObservableBoolean(false)
    override var transactionData: ObservableField<TransactionDetail> = ObservableField()
    override var coverImage: ObservableInt = ObservableInt()
    override var showTotalPurchases: ObservableBoolean = ObservableBoolean(false)
    override var showErrorMessage: ObservableBoolean = ObservableBoolean(false)
    override var updatedCategory: ObservableField<TapixCategory> = ObservableField()
    override var categoryDescription: ObservableField<String> = ObservableField()
}