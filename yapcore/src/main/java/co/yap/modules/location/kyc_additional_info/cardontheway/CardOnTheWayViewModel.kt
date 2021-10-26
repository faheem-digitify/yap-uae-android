package co.yap.modules.location.kyc_additional_info.cardontheway

import android.app.Application
import co.yap.modules.location.viewmodels.LocationChildViewModel
import co.yap.yapcore.SingleClickEvent

class CardOnTheWayViewModel(application: Application) :
    LocationChildViewModel<ICardOnTheWay.State>(application),
    ICardOnTheWay.ViewModel {
    override val state: ICardOnTheWay.State =
        CardOnTheWayState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()

    override fun onResume() {
        super.onResume()
        setProgress(100)
    }

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }
}