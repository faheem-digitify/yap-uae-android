package co.yap.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import co.yap.app.modules.refreal.AdjustReferrerReceiver
import co.yap.app.modules.refreal.DeepLinkNavigation
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.KEY_FCM_TOKEN
import co.yap.yapcore.helpers.SharedPreferenceManager
import com.google.firebase.messaging.RemoteMessage
import com.leanplum.LeanplumPushFirebaseMessagingService

class YapFirebaseMessagingService : LeanplumPushFirebaseMessagingService() {
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SharedPreferenceManager.getInstance(applicationContext).save(KEY_FCM_TOKEN, token)
        Log.d("YapFirebaseMessaging>>", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (SharedPreferenceManager.getInstance(this).getValueBoolien(Constants.ENABLE_LEAN_PLUM_NOTIFICATIONS,
                false)
        ) sendNotification(remoteMessage)
    }

    private fun sendNotification(
        remoteMessage: RemoteMessage
    ) {
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        createNotificationChannel(notificationManager)
        val notificationId = java.util.Random().nextInt()
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, AdjustReferrerReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        intent.putExtra(
            Constants.EXTRA,
            bundleOf(
                "flow_id" to DeepLinkNavigation.DeepLinkFlow.TRANSACTION_DETAILS.flowId
            )
        )
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT,
            bundleOf()
        )
        remoteMessage.notification?.title?.let {notificationTitle->
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext, getNotificationChannelId())
                    .setAutoCancel(true).setSmallIcon(R.drawable.ic_yap).setContentTitle(
                        notificationTitle
                    ).setContentText(remoteMessage.notification?.body ?: "")
            builder.setContentIntent(pendingIntent)
            notificationManager.notify(
                notificationId, builder.build()
            )
        }

    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val morningNotificationChannel =
                NotificationChannel(
                    getNotificationChannelId(),
                    getNotificationChannelName(),
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(morningNotificationChannel)
        }
    }

    private fun getNotificationChannelId() = "transaction"
    private fun getNotificationChannelName() = "NotificationChannelName"

}