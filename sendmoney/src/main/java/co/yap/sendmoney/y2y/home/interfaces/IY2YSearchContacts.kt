package co.yap.sendmoney.y2y.home.interfaces

import androidx.databinding.ObservableField
import co.yap.sendmoney.y2y.home.yapcontacts.YapContactsAdaptor
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.adapters.SectionsPagerAdapter

interface IY2YSearchContacts {
    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        val adaptor: YapContactsAdaptor
        fun handlePressOnView(id: Int)
    }

    interface State : IBase.State
}