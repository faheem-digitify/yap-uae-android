package co.yap.modules.dashboard.more.notifications.home

import androidx.lifecycle.MutableLiveData
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.widgets.MultiStateView
import co.yap.yapcore.BaseState

class NotificationsHomeState : BaseState(), INotificationsHome.State {
    override val mNotifications: MutableLiveData<MutableList<HomeNotification>>? =
        MutableLiveData(mutableListOf())
    override var viewState: MutableLiveData<Any?> = MutableLiveData(MultiStateView.ViewState.EMPTY)
}
