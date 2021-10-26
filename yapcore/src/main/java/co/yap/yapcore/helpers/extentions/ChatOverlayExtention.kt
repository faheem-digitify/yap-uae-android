package co.yap.yapcore.helpers.extentions

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import co.yap.networking.authentication.AuthRepository
import co.yap.widgets.CounterFloatingActionButton
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants.KEY_LP_CHAT_COUNT
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.managers.SessionManager
import com.liveperson.infra.ConversationViewParams
import com.liveperson.infra.ICallback
import com.liveperson.infra.InitLivePersonProperties
import com.liveperson.infra.LPConversationsHistoryStateToDisplay
import com.liveperson.infra.auth.LPAuthenticationParams
import com.liveperson.infra.auth.LPAuthenticationType
import com.liveperson.infra.callbacks.InitLivePersonCallBack
import com.liveperson.messaging.sdk.api.LivePerson
import com.liveperson.messaging.sdk.api.model.ConsumerProfile


const val BRAND_ID: String = "17038977"

fun Activity.initializeChatOverLayButton(unreadCount: Int = 0) {
    try {
        initLivePersonChatOnly()
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(
            dimen(R.dimen.margin_medium),
            dimen(R.dimen.margin_medium),
            dimen(R.dimen.margin_medium),
            dimen(R.dimen.margin_btn_side_paddings_xl)
        )
        param.gravity = Gravity.END or Gravity.BOTTOM
        val view = layoutInflater.inflate(
            R.layout.layout_overlay_live_chat,
            null
        ) as? CounterFloatingActionButton
        (window.decorView as FrameLayout).findViewById<FrameLayout>(android.R.id.content)
            .addView(view, param)
        view?.setOnClickListener { chatSetup() }
        updateCount(unreadCount)
    } catch (e: Exception) {
    }
}

fun Activity.overLayButtonVisibility(visibility: Int) {
    val fab =
        ((window.decorView as FrameLayout).findViewById<View>(R.id.faLiveChat) as? CounterFloatingActionButton)
    if (visibility == View.VISIBLE) {
        getCountUnreadMessage()
    } else {
        fab?.visibility = visibility
    }
}

fun Activity.chatSetup() {
    SessionManager.user?.let {
        LivePerson.initialize(
            this,
            InitLivePersonProperties(
                BRAND_ID, it.uuid,
                object : InitLivePersonCallBack {
                    override fun onInitSucceed() {
                        openChatActivity()
                    }

                    override fun onInitFailed(e: Exception) {
                        toast("Unable to open chat")
                    }
                })
        )
    }
}

fun Activity.initLivePersonChatOnly() {
    SessionManager.user?.let {
//        val monitoringParams = MonitoringInitParams(appInstallId)
        LivePerson.initialize(
            this,
            InitLivePersonProperties(
                BRAND_ID, it.uuid,
                object : InitLivePersonCallBack {
                    override fun onInitSucceed() {
                        SessionManager.user?.currentCustomer?.let {
                            val authParams = LPAuthenticationParams(LPAuthenticationType.AUTH)
                            authParams.hostAppJWT = AuthRepository.getJwtToken()
                            val consumerProfile = ConsumerProfile.Builder()
                                .setFirstName(it.firstName)
                                .setLastName(it.lastName)
                                .setPhoneNumber(it.getCompletePhone())
                                .build()
                            LivePerson.setUserProfile(consumerProfile)
                        }
                    }

                    override fun onInitFailed(e: Exception) {
                        toast("Unable to open chat")
                    }
                })
        )
    }
}

fun Activity.showConversation(
    lpAuthParams: LPAuthenticationParams?,
    convViewParams: ConversationViewParams?
) = LivePerson.showConversation(this, lpAuthParams, convViewParams)

private fun Activity.openChatActivity() {
    SessionManager.user?.currentCustomer?.let {
        val authParams = LPAuthenticationParams(LPAuthenticationType.AUTH)
        authParams.hostAppJWT = AuthRepository.getJwtToken()
        val params = ConversationViewParams(false)
            .setHistoryConversationsStateToDisplay(LPConversationsHistoryStateToDisplay.ALL)
            .setHistoryConversationsMaxDays(180)
            .setReadOnlyMode(false)

        showConversation(authParams, params)
        SharedPreferenceManager.getInstance(this).save(KEY_LP_CHAT_COUNT, 0)
        val consumerProfile = ConsumerProfile.Builder()
            .setFirstName(it.firstName)
            .setLastName(it.lastName)
            .setPhoneNumber(it.getCompletePhone())
            .build()
        LivePerson.setUserProfile(consumerProfile)
    }
}

fun Activity.getCountUnreadMessage() {
    updateCount(SharedPreferenceManager.getInstance(this).getValueInt(KEY_LP_CHAT_COUNT))
    SessionManager.user?.let {
        LivePerson.getUnreadMessagesCount(
            it.uuid,
            object : ICallback<Int, java.lang.Exception> {
                override fun onSuccess(count: Int?) {
                    SharedPreferenceManager.getInstance(this@getCountUnreadMessage)
                        .save(KEY_LP_CHAT_COUNT, count ?: 0)
                    updateCount(count ?: 0)
                }

                override fun onError(p0: java.lang.Exception?) {
                }
            }
        )
    }
}

private fun Activity.updateCount(unreadCount: Int) {
    try {
        val fab =
            (window.decorView as FrameLayout).findViewById<View>(R.id.faLiveChat) as? CounterFloatingActionButton
        fab?.count = unreadCount
        fab?.visibility = (if (unreadCount > 0) View.VISIBLE else View.GONE)
    } catch (e: Exception) {
    }
}