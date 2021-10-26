package co.yap.modules.dashboard.transaction.feedback

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class TransactionFeedbackState : BaseState(), ITransactionFeedback.State {
    override val location: ObservableField<String> = ObservableField()
    override val title: ObservableField<String> = ObservableField()
}