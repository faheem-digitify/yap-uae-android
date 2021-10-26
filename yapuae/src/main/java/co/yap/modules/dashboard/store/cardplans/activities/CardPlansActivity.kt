package co.yap.modules.dashboard.store.cardplans.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.store.cardplans.interfaces.IMainCardPlans
import co.yap.modules.dashboard.store.cardplans.viewmodels.CardPlansMainViewModel
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.defaults.DefaultNavigator
import co.yap.yapcore.defaults.INavigator
import co.yap.yapcore.interfaces.BackPressImpl
import co.yap.yapcore.interfaces.IBaseNavigator
import kotlinx.android.synthetic.main.fragment_prime_metal_card.*

class CardPlansActivity : BaseBindingActivity<IMainCardPlans.ViewModel>(), INavigator,
    IFragmentHolder, IMainCardPlans.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.activity_card_plans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right_slow,R.anim.slide_out_left_slow)
        setObservers()
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, onClickObserver)
    }


    val onClickObserver = Observer<Int> { id ->
        when (id) {
            R.id.ivCross, R.id.ivCrossPrime -> {
                onBackPressed()
            }
        }
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObserver(onClickObserver)
    }

    override val viewModel: IMainCardPlans.ViewModel
        get() = ViewModelProviders.of(this).get(CardPlansMainViewModel::class.java)
    override val navigator: IBaseNavigator
        get() = DefaultNavigator(this@CardPlansActivity, R.id.card_plans_navigation)

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.card_plans_navigation)
        if (!BackPressImpl(fragment).onBackPressed()) {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }
    }