package co.yap.modules.dashboard.store.cardplans.interfaces

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.store.cardplans.CardPlans
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IMainCardPlans {
    interface View : IBase.View<ViewModel> {
        fun setObservers()
        fun removeObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        var cards: MutableList<CardPlans>
        val cardTag: String get() = "CARD-TAG"
        var selectedPlan: ObservableField<String>
        fun setViewDimensions(percent: Int, view: android.view.View): ConstraintLayout.LayoutParams
        var clickEvent: SingleClickEvent
        fun handlePressOnView(id: Int)
    }

    interface State : IBase.State
}