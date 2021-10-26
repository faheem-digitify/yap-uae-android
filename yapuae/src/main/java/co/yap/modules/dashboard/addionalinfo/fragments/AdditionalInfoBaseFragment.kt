package co.yap.modules.dashboard.addionalinfo.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoBaseViewModel
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoViewModel
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.IBase

abstract class AdditionalInfoBaseFragment<V : IBase.ViewModel<*>> :
    BaseBindingFragment<V>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel is AdditionalInfoBaseViewModel<*> && activity != null) {
            (viewModel as AdditionalInfoBaseViewModel<*>).parentViewModel =
                ViewModelProviders.of(requireActivity())
                    .get(AdditionalInfoViewModel::class.java)
        }
    }
}