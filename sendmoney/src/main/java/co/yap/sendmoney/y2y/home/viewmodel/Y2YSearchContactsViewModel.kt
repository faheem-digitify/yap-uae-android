package co.yap.sendmoney.y2y.home.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.sendmoney.y2y.home.interfaces.IY2YSearchContacts
import co.yap.sendmoney.y2y.home.states.Y2YSearchContactsState
import co.yap.sendmoney.y2y.home.yapcontacts.YapContactsAdaptor
import co.yap.sendmoney.y2y.main.viewmodels.Y2YBaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.adapters.SectionsPagerAdapter

class Y2YSearchContactsViewModel(application: Application) :
    Y2YBaseViewModel<IY2YSearchContacts.State>(application),
    IY2YSearchContacts.ViewModel {
    override val state: IY2YSearchContacts.State = Y2YSearchContactsState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val adaptor: YapContactsAdaptor = YapContactsAdaptor(arrayListOf())

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        toggleToolBarVisibility(false)
    }

}