package co.yap.sendmoney.y2y.transfer.viewmodels

import android.app.Application
import co.yap.sendmoney.y2y.main.viewmodels.Y2YBaseViewModel
import co.yap.sendmoney.y2y.transfer.interfaces.IY2YFundsTransferSuccess
import co.yap.sendmoney.y2y.transfer.states.Y2YFundsTransferSuccessState

import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.leanplum.SendMoneyEvents
import co.yap.yapcore.leanplum.trackEvent

class Y2YFundsTransferSuccessViewModel(application: Application) :
    Y2YBaseViewModel<IY2YFundsTransferSuccess.State>(application),
    IY2YFundsTransferSuccess.ViewModel {
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val state: Y2YFundsTransferSuccessState = Y2YFundsTransferSuccessState()

    override fun handlePressOnDashboardButton(id: Int) {
        clickEvent.call()
    }

    override fun onCreate() {
        super.onCreate()
        if(parentViewModel?.state?.fromQR?.get() == true) {
            trackEvent(SendMoneyEvents.QR_PAYMENT_SUCCESS.type)
        }
    }

    override fun onResume() {
        super.onResume()
        toggleToolBarVisibility(true)
        setToolBarTitle(getString(Strings.screen_y2y_funds_transfer_success_display_text_title))
        setRightButtonVisibility(false)
        setLeftButtonVisibility(false)
    }
}