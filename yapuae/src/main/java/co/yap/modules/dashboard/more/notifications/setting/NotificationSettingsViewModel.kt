package co.yap.modules.dashboard.more.notifications.setting

import android.app.Application
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.notification.NotificationsApi
import co.yap.networking.notification.NotificationsRepository
import co.yap.networking.notification.responsedtos.NotificationSettings
import co.yap.yapcore.BaseViewModel
import kotlinx.coroutines.delay

class NotificationSettingsViewModel(application: Application) :
    BaseViewModel<INotificationSettings.State>(application = application),
    INotificationSettings.ViewModel {
    override val state: NotificationSettingsState =
        NotificationSettingsState()
    override val repository: NotificationsApi = NotificationsRepository


    override fun getNotificationSettings(onComplete: (Boolean) -> Unit) {
        launch {
            state.loading = true
            when (val response = repository.getNotificationSettings()) {
                is RetroApiResponse.Success -> {
                    state.emailNotificationsAllowed = response.data.data?.emailEnabled
                    state.inAppNotificationsAllowed = response.data.data?.inAppEnabled
                    state.smsNotificationsAllowed = response.data.data?.smsEnabled
                    state.loading = false
                    delay(100)
                    onComplete.invoke(true)
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = response.error.message
                    onComplete.invoke(false)
                }
            }
        }
    }

    override fun saveNotificationSettings() {
        launch {
            state.loading = true
            when (val response = repository.saveNotificationSettings(
                NotificationSettings(
                    state.emailNotificationsAllowed,
                    state.inAppNotificationsAllowed,
                    state.smsNotificationsAllowed
                )
            )) {
                is RetroApiResponse.Success -> {
                    state.loading = false
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = response.error.message
                }
            }
        }
    }
}
