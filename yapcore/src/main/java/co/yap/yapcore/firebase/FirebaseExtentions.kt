package co.yap.yapcore.firebase

import android.os.Bundle
import androidx.core.os.bundleOf
import co.yap.yapcore.BaseActivity
import co.yap.yapcore.BaseFragment
import co.yap.yapcore.helpers.rx.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

/**
 * Log Events manual on  firebase.
 * <p>
 * @param event the event that will be logged see {@link FirebaseEvent}
 * See also [FirebaseEvent].
 */
fun trackEventWithScreenName(event: FirebaseEvent) {
    Task.run {
        val firebaseAnalytics = Firebase.analytics
        event.event?.let { e ->
            firebaseAnalytics.logEvent(e.trim()) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, event.screenName?.trim() ?: "")
            }
        }
    }
}

/**
 * Log Events manual on  firebase.
 * <p>
 * @param event the event that will be logged see {@link FirebaseEvent}
 *@param additionalParams The additional Params that will be logged with event
 * See also [FirebaseEvent].
 */

fun trackEventWithScreenName(event: FirebaseEvent, additionalParams: Bundle? = null) {
    val firebaseAnalytics = Firebase.analytics
    event.event?.let { e ->
        val Params =
            bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to (event.screenName?.trim() ?: ""))
        additionalParams?.let {
            if (it.keySet().size > 0)
                Params.putAll(it)
        }
        firebaseAnalytics.logEvent(e.trim(), Params)
    }
}

fun BaseFragment<*>.trackScreenViewEvent() {
    getScreenName()?.let {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, it)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
        }
    }
}

fun BaseActivity<*>.trackScreenViewEvent() {
    getScreenName()?.let {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, it)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
        }
    }
}
