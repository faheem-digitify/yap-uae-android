package co.yap.modules.auth.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.defaults.DefaultNavigator
import co.yap.yapcore.defaults.INavigator
import co.yap.yapcore.interfaces.IBaseNavigator

class AuthenticationActivity :
    BaseBindingActivity<IAuth.ViewModel>(), IAuth.View, INavigator, IFragmentHolder {
    override fun getLayoutId(): Int = R.layout.activity_authentication
    override fun getBindingVariable(): Int = BR.viewModel
    override val viewModel: IAuth.ViewModel by viewModels<AuthViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.mobileNo = intent.getStringExtra("mobileNo") ?: ""
        viewModel.countryCode = intent.getStringExtra("countryCode") ?: ""
        viewModel.state.isAccountBlocked = intent.getBooleanExtra("isAccountBlocked", false)
    }

    override val navigator: IBaseNavigator
        get() = DefaultNavigator(this@AuthenticationActivity, R.id.main_nav_host_fragment)

    fun onBackPressedDummy() {
        super.onBackPressed()
    }
}