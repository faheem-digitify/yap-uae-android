package co.yap.yapcore.firebase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

fun getFCMToken(onComplete: (String?) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@OnCompleteListener
        }
        onComplete.invoke(task.result)
    })
}