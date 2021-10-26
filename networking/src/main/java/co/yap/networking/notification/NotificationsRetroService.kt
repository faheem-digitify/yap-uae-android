package co.yap.networking.notification

import co.yap.networking.models.ApiResponse
import co.yap.networking.models.BaseListResponse
import co.yap.networking.models.BaseResponse
import co.yap.networking.notification.requestdtos.FCMTokenRequest
import co.yap.networking.notification.requestdtos.UpdateNotificationRequest
import co.yap.networking.notification.responsedtos.FcmNotificationCount
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.MsTokenResponse
import co.yap.networking.notification.responsedtos.NotificationSettings
import retrofit2.Response
import retrofit2.http.*

interface NotificationsRetroService {
    @POST(NotificationsRepository.URL_MS_LOGIN_TOKEN)
    suspend fun sendFcmTokenToServer(
        @Body msTokenRequest: FCMTokenRequest
    ): Response<BaseResponse<MsTokenResponse>>

    @GET(NotificationsRepository.URL_CUSTOMER_NOTIFICATIONS)
    suspend fun getTransactionsNotifications(
    ): Response<BaseListResponse<HomeNotification>>

    @GET(NotificationsRepository.URL_CUSTOMER_NOTIFICATION_COUNT)
    suspend fun getTransactionsNotificationsCount(): Response<FcmNotificationCount>

    @PUT(NotificationsRepository.URL_CUSTOMER_NOTIFICATION_READABLE)
    suspend fun markNotificationRead(
        @Body request: UpdateNotificationRequest?
    ): Response<ApiResponse>

    @HTTP(
        method = "DELETE",
        path = NotificationsRepository.URL_DELETE_CUSTOMER_NOTIFICATION,
        hasBody = true
    )
    suspend fun deleteMsCustomerNotification(@Body request: UpdateNotificationRequest?): Response<ApiResponse>

    @GET(NotificationsRepository.URL_SETTING_CUSTOMER_NOTIFICATION)
    suspend fun getNotificationSettings(): Response<BaseResponse<NotificationSettings>>

    @POST(NotificationsRepository.URL_SETTING_CUSTOMER_NOTIFICATION)
    suspend fun saveNotificationSettings(@Body request: NotificationSettings?): Response<ApiResponse>

}