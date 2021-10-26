package co.yap.modules.dashboard.more.notifications.setting

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.yapcore.BaseState

class NotificationSettingsState : BaseState(), INotificationSettings.State {
    @get:Bindable
    override var inAppNotificationsAllowed: Boolean? = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.inAppNotificationsAllowed)
        }

    @get:Bindable
    override var smsNotificationsAllowed: Boolean? = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.smsNotificationsAllowed)
        }

    @get:Bindable
    override var emailNotificationsAllowed: Boolean? = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.emailNotificationsAllowed)
        }
}