package co.yap.modules.dashboard.more.notifications.main

import android.app.Application
import co.yap.yapcore.BaseViewModel

class NotificationsViewModel(application: Application) :
    BaseViewModel<INotifications.State>(application),
    INotifications.ViewModel {
    override val state: NotificationState = NotificationState()
}