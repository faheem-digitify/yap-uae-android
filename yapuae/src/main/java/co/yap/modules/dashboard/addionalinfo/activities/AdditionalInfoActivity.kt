package co.yap.modules.dashboard.addionalinfo.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ActivityAdditionalInfoBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfo
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoViewModel
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.defaults.DefaultNavigator
import co.yap.yapcore.defaults.INavigator
import co.yap.yapcore.interfaces.BackPressImpl
import co.yap.yapcore.interfaces.IBaseNavigator
import kotlinx.android.synthetic.main.activity_additional_info.*

class AdditionalInfoActivity : BaseBindingActivity<IAdditionalInfo.ViewModel>(),
    IFragmentHolder, INavigator, IAdditionalInfo.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_additional_info


    override val viewModel: IAdditionalInfo.ViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoViewModel::class.java)
    override val navigator: IBaseNavigator
        get() = DefaultNavigator(
            this@AdditionalInfoActivity,
            R.id.additional_info_host_fragment
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAdditionalInfo {
            viewModel.setSteps(it) {
                getBindings().stepView.setStepsNumber(viewModel.state.steps.get() ?: -1)
            }
        }
        setObserver()
    }

    private fun getBindings(): ActivityAdditionalInfoBinding =
        viewDataBinding as ActivityAdditionalInfoBinding

    override fun setObserver() {
        viewModel.stepCount.observe(this, Observer {
            if (it < getBindings().stepView.stepCount) {
                step_view.go(it, true)
            } else {
                if (it > 0)
                    step_view.done(true)
            }
        })
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.additional_info_host_fragment)
        if (!BackPressImpl(fragment).onBackPressed()) {
            super.onBackPressed()
        }
    }
}