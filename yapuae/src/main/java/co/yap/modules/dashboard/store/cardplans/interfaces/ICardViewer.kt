package co.yap.modules.dashboard.store.cardplans.interfaces

import co.yap.yapuae.databinding.FragmentViewerCardPlansBinding
import co.yap.yapcore.IBase

interface ICardViewer {
    interface View : IBase.View<ViewModel> {
        fun initArguments()
        fun getBindings(): FragmentViewerCardPlansBinding
    }

    interface ViewModel : IBase.ViewModel<State> {
        fun getFragmentToDisplay(id: String?): Int
    }

    interface State : IBase.State
}