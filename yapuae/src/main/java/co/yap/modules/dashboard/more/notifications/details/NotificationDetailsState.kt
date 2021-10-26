package co.yap.modules.dashboard.more.notifications.details

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.yapcore.BaseState

class NotificationDetailsState : BaseState(), INotificationDetails.State {
    @get:Bindable
    override var notification: HomeNotification? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.notification)
        }
}