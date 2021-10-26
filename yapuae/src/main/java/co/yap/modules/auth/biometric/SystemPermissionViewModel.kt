package co.yap.modules.auth.biometric

import android.annotation.TargetApi
import android.app.Application
import android.os.Build
import co.yap.yapuae.R
import co.yap.modules.Constants
import co.yap.translation.Strings
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.leanplum.KYCEvents
import co.yap.yapcore.leanplum.trackEvent

class SystemPermissionViewModel(application: Application) :
    BaseViewModel<ISystemPermission.State>(application),
    ISystemPermission.ViewModel {

    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override var screenType: String = ""
    override fun onCreate() {
        super.onCreate()
        setupViews()
    }

    override fun handleOnPressView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun getTouchScreenValues(isGranted: Boolean) {
        when (isGranted) {
            true -> {
                SharedPreferenceManager.getInstance(context).save(co.yap.yapcore.constants.Constants.KEY_TOUCH_ID_ENABLED,
                    true)
                trackEvent(KYCEvents.SIGN_UP_ENABLED_PERMISSION.type, "TouchID")
                trackEventWithScreenName(FirebaseEvent.SETUP_TOUCH_ID)
            }
            else -> {
                trackEventWithScreenName(FirebaseEvent.NO_TOUCH_ID)
                SharedPreferenceManager.getInstance(context).save(co.yap.yapcore.constants.Constants.KEY_TOUCH_ID_ENABLED,
                    false)
            }
        }
    }

    override fun getNotificationScreenValues(isGranted: Boolean) {
        when (isGranted) {
            true -> {
                trackEventWithScreenName(FirebaseEvent.ACCEPT_NOTIFICATIONS)
                SharedPreferenceManager.getInstance(context).save(co.yap.yapcore.constants.Constants.ENABLE_LEAN_PLUM_NOTIFICATIONS,true)
            }
            else -> {
                trackEventWithScreenName(FirebaseEvent.DECLINE_NOTIFICATIONS)
                SharedPreferenceManager.getInstance(context).save(co.yap.yapcore.constants.Constants.ENABLE_LEAN_PLUM_NOTIFICATIONS,false)
            }
        }
    }


    fun setupViews() {
        if (screenType == Constants.TOUCH_ID_SCREEN_TYPE) touchIdViews() else notificationViews()
    }

    override val state: SystemPermissionState = SystemPermissionState()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun touchIdViews() {
        state.icon = R.drawable.ic_fingerprint
        state.title = getString(Strings.screen_system_permission_text_title)
        state.subTitle = getString(Strings.screen_system_permission_text_sub_title)
        state.termsAndConditionsVisibility = true
        state.denied = getString(Strings.screen_system_permission_text_denied)
        state.buttonTitle = getString(Strings.screen_system_permission_button_touch_id)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun notificationViews() {
        state.icon = R.drawable.ic_notification
        state.title = getString(Strings.screen_notification_permission_text_title)
        state.subTitle = getString(Strings.screen_notification_permission_text_sub_title)
        state.denied = getString(Strings.screen_system_permission_text_denied)
        state.termsAndConditionsVisibility = false
        state.buttonTitle = getString(Strings.screen_notification_permission_button_title)
    }
}