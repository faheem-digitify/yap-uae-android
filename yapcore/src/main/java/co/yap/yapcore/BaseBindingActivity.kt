package co.yap.yapcore

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import co.yap.app.YAPApplication
import co.yap.localization.LocaleManager
import co.yap.yapcore.helpers.extentions.getCountUnreadMessage
import co.yap.yapcore.helpers.extentions.initializeChatOverLayButton
import co.yap.yapcore.managers.isUserLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseBindingActivity<V : IBase.ViewModel<*>> : BaseActivity<V>() {

    lateinit var viewDataBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // For runtime permission handling if user
        // disable permission from settings manually
        if (YAPApplication.AUTO_RESTART_APP) {
            finishAffinity()
            restartApp()
        }
        super.onCreate(savedInstanceState)
        performDataBinding()
        if (shouldShowChatChatOverLay() == true)
            initializeChatOverLayButton()
    }

    override fun onResume() {
        super.onResume()
        if (shouldShowChatChatOverLay() == true)
            getCountUnreadMessage()
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(intent)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        viewDataBinding.executePendingBindings()
    }

    fun launch(dispatcher: Dispatcher = Dispatcher.Main, block: suspend () -> Unit) {
        lifecycleScope.launch(
            when (dispatcher) {
                Dispatcher.Main -> Dispatchers.Main
                Dispatcher.Background -> Dispatchers.IO
                Dispatcher.LongOperation -> Dispatchers.Default
            }
        ) { block() }
    }

    open fun shouldShowChatChatOverLay() = isUserLogin()

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }
}