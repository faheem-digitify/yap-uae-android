package co.yap.modules.dashboard.more.notifications.main

import co.yap.yapcore.IBase

interface INotifications {

    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {

    }

    interface State : IBase.State {

    }
}