package co.yap.modules.dashboard.more.notifications.home

import android.app.Application
import androidx.databinding.ObservableField
import co.yap.networking.models.BaseListResponse
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.notification.NotificationsApi
import co.yap.networking.notification.NotificationsRepository
import co.yap.networking.notification.requestdtos.UpdateNotificationRequest
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.NotificationAction
import co.yap.widgets.State
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.DateUtils.SERVER_DATE_FORMAT
import co.yap.yapcore.helpers.DateUtils.UTC
import co.yap.yapcore.helpers.NotificationHelper
import co.yap.yapcore.leanplum.deleteLeanPlumMessage
import co.yap.yapcore.leanplum.markReadLeanPlumMessage
import co.yap.yapcore.managers.SessionManager
import com.leanplum.Leanplum


class NotificationsHomeViewModel(application: Application) :
    BaseViewModel<INotificationsHome.State>(application),
    INotificationsHome.ViewModel {
    override val state = NotificationsHomeState()
    override val mNotificationsHomeAdapter: ObservableField<NotificationsHomeAdapter>? =
        ObservableField()
    override val repository: NotificationsApi = NotificationsRepository

    override fun onCreate() {
        super.onCreate()
        getNotification()
    }

    override fun getNotification() {
        state.stateLiveData?.value = State.loading(null)
        SessionManager.user?.let { account ->
            SessionManager.card.value?.let { card ->
                state.mNotifications?.value =
                    NotificationHelper.getNotifications(
                        account,
                        card, context
                    )
                if ((state.mNotifications?.value?.size ?: 0) > 0) {
                    state.stateLiveData?.postValue(State.success(null))
                }
                mNotificationsHomeAdapter?.get()?.setData(
                    state.mNotifications?.value ?: mutableListOf()
                )
                getFcmNotifications()
//                loadNotifications()
                mNotificationsHomeAdapter?.get()?.setData(
                    state.mNotifications?.value ?: arrayListOf()
                )
            }
        }

    }

    override fun getFcmNotifications() {
        launch {
            val notifications = mutableListOf<HomeNotification>()
            val notification = launchAsync {
                repository.getAllNotifications()
            }
            val leanplumMessages = launchAsync {
                loadLeanPlumMessages()
            }
            val notificationWait = notification.await()
            val leanplumMessagesWait = leanplumMessages.await()
            if (notificationWait is RetroApiResponse.Success) {
//                notificationWait.data.data
//                for (i in 1..40) {
//                    notifications.add(getTestData(10 + i))
//                }
                notifications.addAll(notificationWait.data.data ?: mutableListOf())
            }
            if (leanplumMessagesWait is RetroApiResponse.Success) {
                notifications.addAll(
                    leanplumMessagesWait.data.data ?: mutableListOf()
                )
            }

            notifications.sortByDescending { combinedNotificationList ->
                DateUtils.stringToDate(
                    combinedNotificationList.createdAt ?: "",
                    SERVER_DATE_FORMAT,
                    UTC
                )?.time
            }

            state.mNotifications?.value?.addAll(notifications)
            mNotificationsHomeAdapter?.get()?.setData(
                state.mNotifications?.value ?: mutableListOf()
            )
            state.stateLiveData?.postValue(
                if ((state.mNotifications?.value?.size
                        ?: 0) > 0
                ) State.success(null) else State.empty(null)
            )
        }
    }

    private fun getTestData(id: Int) = HomeNotification(
        id = id.toString(),
        notificationType = null,
        title = "Test Image Push",
        description = "Yap ios date  frmt test 12345",
        profilePicUrl = "https://digitify.com/wp-content/uploads/2020/01/digitify-logo-1-300x82.png",
        firstName = "dsad",
        lastName = "gafds",
        currency = "AED",
        isDeletable = true,
        isRead = false,
        action = NotificationAction.SET_PIN
    )

    override fun markNotificationRead(
        item: HomeNotification,
        isRead: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        if (item.action == NotificationAction.LEANPLUM) {
            markReadLeanPlumMessage(item.id)
            onComplete.invoke(true)
        } else {
            launch {
                state.loading = true
                when (val response = repository.markNotificationRead(
                    UpdateNotificationRequest(item.id, isRead)
                )) {
                    is RetroApiResponse.Success -> {
                        onComplete.invoke(true)
                        state.loading = false

                    }
                    is RetroApiResponse.Error -> {
                        showToast(response.error.message)
                        state.loading = false
                    }
                }
            }
        }
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

    suspend fun loadLeanPlumMessages(): RetroApiResponse<BaseListResponse<HomeNotification>> {
        val notifications = mutableListOf<HomeNotification>()
        val inbox = Leanplum.getInbox()
        val messageList = inbox.allMessages()
        messageList.forEach {
            notifications.add(
                HomeNotification(
                    id = it.messageId,
                    notificationType = NotificationAction.LEANPLUM.name,
                    title = it.title,
                    profilePicUrl = it.imageFilePath,
                    firstName = it.title,
                    lastName = it.title,
                    currency = "",
                    amount = "",
                    createdAt = DateUtils.dateToString(
                        it.deliveryTimestamp,
                        SERVER_DATE_FORMAT,
                        UTC
                    ),
                    isRead = it.isRead,
                    isDeletable = true,
                    description = it.subtitle,
                    action = NotificationAction.LEANPLUM,
                    subTitle = it.title,
                    imgResId = null
                )
            )
        }

        val response = BaseListResponse<HomeNotification>()
        response.data = notifications
        return RetroApiResponse.Success(
            200,
            response
        )
    }
}