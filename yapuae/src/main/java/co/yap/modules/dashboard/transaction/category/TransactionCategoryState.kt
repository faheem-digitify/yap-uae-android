package co.yap.modules.dashboard.transaction.category

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class TransactionCategoryState : BaseState(), ITransactionCategory.State {
    override var transactionId: ObservableField<String> = ObservableField()
    override var categoryName: ObservableField<String> = ObservableField()
}