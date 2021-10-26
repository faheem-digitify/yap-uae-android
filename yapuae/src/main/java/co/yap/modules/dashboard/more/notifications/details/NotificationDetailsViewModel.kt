package co.yap.modules.dashboard.more.notifications.details

import android.app.Application
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.notification.NotificationsApi
import co.yap.networking.notification.NotificationsRepository
import co.yap.networking.notification.requestdtos.UpdateNotificationRequest
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.NotificationAction
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.leanplum.deleteLeanPlumMessage

class NotificationDetailsViewModel(application: Application) :
    BaseViewModel<INotificationDetails.State>(application),
    INotificationDetails.ViewModel {
    override val state = NotificationDetailsState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val repository: NotificationsApi = NotificationsRepository
    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun deleteFcmNotifications(item: HomeNotification?, onComplete: (Boolean) -> Unit) {
        if (item?.action == NotificationAction.LEANPLUM) {
            deleteLeanPlumMessage(item.id)
            onComplete.invoke(true)
        } else {
            launch {
                state.loading = true
                when (val response = repository.deleteMsCustomerNotification(
                    UpdateNotificationRequest(item?.id)
                )) {
                    is RetroApiResponse.Success -> {
                        state.loading = false
                        onComplete.invoke(true)
                    }
                    is RetroApiResponse.Error -> {
                        showToast(response.error.message)
                        state.loading = false
                    }
                }
            }
        }
    }
}