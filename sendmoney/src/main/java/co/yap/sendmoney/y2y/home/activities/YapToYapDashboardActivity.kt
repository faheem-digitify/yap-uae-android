package co.yap.sendmoney.y2y.home.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.repositories.InviteFriendRepository
import co.yap.sendmoney.BR
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.ActivityYapToYapDashboardBinding
import co.yap.sendmoney.y2y.main.interfaces.IY2Y
import co.yap.sendmoney.y2y.main.viewmodels.Y2YViewModel
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.defaults.DefaultNavigator
import co.yap.yapcore.defaults.INavigator
import co.yap.yapcore.helpers.*
import co.yap.yapcore.helpers.extentions.share
import co.yap.yapcore.interfaces.BackPressImpl
import co.yap.yapcore.interfaces.IBaseNavigator
import com.google.android.material.snackbar.Snackbar

class YapToYapDashboardActivity : BaseBindingActivity<IY2Y.ViewModel>(), INavigator,
    IFragmentHolder {

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.activity_yap_to_yap_dashboard
    override val viewModel: IY2Y.ViewModel
        get() = ViewModelProviders.of(this).get(Y2YViewModel::class.java)

    override val navigator: IBaseNavigator
        get() = DefaultNavigator(
            this@YapToYapDashboardActivity,
            R.id.main_nav_host_fragment
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isSearching.value = intent.getBooleanExtra(ExtraKeys.IS_Y2Y_SEARCHING.name, false)
        viewModel.beneficiary = intent.getParcelableExtra(Beneficiary::class.java.name)
        viewModel.state.fromQR?.set(
            intent.getBooleanExtra(
                ExtraKeys.IS_FROM_QR_CONTACT.name,
                false
            )
        )
        viewModel.position = intent.getIntExtra(ExtraKeys.Y2Y_BENEFICIARY_POSITION.name, 0)
        viewModel.errorEvent.observe(this, errorEvent)
        getBindings().main.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    val errorEvent = Observer<String> {
        if (!it.isNullOrEmpty())
            showErrorSnackBar(it)
        else
            hideErrorSnackBar()
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                onBackPressed()
            }
            R.id.ivRightIcon -> {
                if (getBindings().toolbar.rightIcon == R.drawable.ic_close) {
                    finish()
                } else {
                    InviteFriendRepository().inviteAFriend()
                    context.share(text = Utils.getGeneralInvitationBody(this))
                }
            }
        }
    }

    private fun showErrorSnackBar(errorMessage: String) {
        getSnackBarFromQueue(0)?.let {
            if (it.isShown) {
                it.updateSnackBarText(errorMessage)
            }
        } ?: getBindings().clSnackBar.showSnackBar(
            msg = errorMessage,
            viewBgColor = R.color.errorLightBackground,
            colorOfMessage = R.color.error, duration = Snackbar.LENGTH_INDEFINITE, marginTop = 0
        )
    }

    private fun hideErrorSnackBar() {
        cancelAllSnackBar()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
        if (!BackPressImpl(fragment).onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.errorEvent.removeObservers(this)
    }

    fun getBindings(): ActivityYapToYapDashboardBinding {
        return viewDataBinding as ActivityYapToYapDashboardBinding
    }
}