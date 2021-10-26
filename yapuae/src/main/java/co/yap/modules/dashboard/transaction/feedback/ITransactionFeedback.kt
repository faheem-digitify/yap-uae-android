package co.yap.modules.dashboard.transaction.feedback

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.yapuae.databinding.FragmentTransactionFeedbackBinding
import co.yap.modules.dashboard.transaction.feedback.adaptor.TransactionFeedbackAdapter
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ITransactionFeedback {
    interface View : IBase.View<ViewModel> {
        fun setObserver()
        fun removeObserver()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val adapter: TransactionFeedbackAdapter
        fun handlePressOnView(id: Int)
        var clickEvent: SingleClickEvent
        var feedbackSelected: ObservableBoolean
        fun selectFeedback(pos: Int)
    }

    interface State : IBase.State{
        val location: ObservableField<String>
        val title: ObservableField<String>
    }
}