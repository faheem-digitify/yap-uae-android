package co.yap.modules.dashboard.cards.addpaymentcard.spare.virtual

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.cards.addpaymentcard.main.viewmodels.AddPaymentChildViewModel
import co.yap.networking.cards.responsedtos.VirtualCardDesigns
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent

class AddVirtualCardViewModel(application: Application) :
    AddPaymentChildViewModel<IAddVirtualCard.State>(application), IAddVirtualCard.ViewModel {
    override var adapter: ObservableField<AddVirtualCardAdapter> = ObservableField()
    override val state: AddVirtualCardState = AddVirtualCardState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override fun handlePressOnButton(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        setToolBarTitle(getString(Strings.screen_add_virtual_spare_card_tool_bar_title))
    }

    override fun observeCardNameLength(str: String): Boolean {
        state.enabelCoreButton = str.isNotEmpty() && str.length <= 26
        return str.isNotEmpty() && str.length <= 26
    }

    override fun getCardThemesOption(): MutableList<VirtualCardDesigns> {
        state.cardDesigns?.postValue(parentViewModel?.virtualCardDesignsList)
        return parentViewModel?.virtualCardDesignsList ?: arrayListOf()
    }
}
