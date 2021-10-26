package co.yap.modules.dashboard.more.notifications.details

import co.yap.networking.notification.NotificationsApi
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface INotificationDetails {
    interface View : IBase.View<ViewModel>
    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        val repository: NotificationsApi
        fun handlePressOnView(id: Int)
        fun deleteFcmNotifications(item: HomeNotification?, onComplete: (Boolean) -> Unit)
    }

    interface State : IBase.State {
        val notification: HomeNotification?
    }
}