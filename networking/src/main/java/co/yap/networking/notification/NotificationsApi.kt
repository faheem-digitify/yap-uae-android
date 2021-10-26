package co.yap.networking.notification

import co.yap.networking.models.ApiResponse
import co.yap.networking.models.BaseListResponse
import co.yap.networking.models.BaseResponse
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.notification.requestdtos.UpdateNotificationRequest
import co.yap.networking.notification.requestdtos.FCMTokenRequest
import co.yap.networking.notification.responsedtos.FcmNotificationCount
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.MsTokenResponse
import co.yap.networking.notification.responsedtos.NotificationSettings

interface NotificationsApi {
    suspend fun sendFcmTokenToServer(msObject: FCMTokenRequest): RetroApiResponse<BaseResponse<MsTokenResponse>>
    suspend fun getAllNotifications(): RetroApiResponse<BaseListResponse<HomeNotification>>
    suspend fun markNotificationRead(request: UpdateNotificationRequest?): RetroApiResponse<ApiResponse>
    suspend fun getTransactionsNotificationsCount(): RetroApiResponse<FcmNotificationCount>
    suspend fun deleteMsCustomerNotification(request: UpdateNotificationRequest?): RetroApiResponse<ApiResponse>
    suspend fun getNotificationSettings(): RetroApiResponse<BaseResponse<NotificationSettings>>
    suspend fun saveNotificationSettings(request: NotificationSettings?): RetroApiResponse<ApiResponse>
}