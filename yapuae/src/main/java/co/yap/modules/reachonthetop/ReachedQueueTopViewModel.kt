package co.yap.modules.reachonthetop

import android.app.Application
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.CompleteVerificationRequest
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.managers.SessionManager

class ReachedQueueTopViewModel(application: Application) :
    BaseViewModel<IReachedQueueTop.State>(application), IReachedQueueTop.ViewModel,
    IRepositoryHolder<CustomersRepository> {
    override val clickEvent: SingleClickEvent = SingleClickEvent()

    override val state: ReachedQueueTopState =
        ReachedQueueTopState()

    override fun onCreate() {
        super.onCreate()
        state.firstName = SessionManager.user?.currentCustomer?.firstName ?: ""
        state.countryCode = SessionManager.user?.currentCustomer?.countryCode ?: ""
        state.mobileNo = SessionManager.user?.currentCustomer?.mobileNo ?: ""
    }

    override fun handlePressOnNext(id: Int) {
        clickEvent.setValue(id)
    }

    override val repository: CustomersRepository
        get() = CustomersRepository

    override fun completeVerification(callBack: () -> Unit) {
        launch {
            state.loading = true
            when (val response = repository.completeVerification(
                CompleteVerificationRequest(
                    state.countryCode, state.mobileNo
                )
            )) {
                is RetroApiResponse.Success -> {
                    callBack.invoke()
                    state.loading = false
                }

                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = "${response.error.message}^${AlertType.DIALOG.name}"
                    state.error = response.error.message
                }
            }
        }
    }
}
