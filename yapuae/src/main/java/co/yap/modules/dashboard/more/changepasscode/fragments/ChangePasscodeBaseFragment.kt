package co.yap.modules.dashboard.more.changepasscode.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import co.yap.modules.dashboard.more.changepasscode.activities.ChangePasscodeActivity
import co.yap.modules.dashboard.more.changepasscode.viewmodels.ChangePassCodeBaseViewModel
import co.yap.modules.dashboard.more.changepasscode.viewmodels.ChangePassCodeViewModel
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.IBase

abstract class ChangePasscodeBaseFragment<V : IBase.ViewModel<*>> :
    BaseBindingFragment<V>() {

    lateinit var parentActivity: ChangePasscodeActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity != null && activity is ChangePasscodeActivity) {
            parentActivity = activity as ChangePasscodeActivity
        }
        if (viewModel is ChangePassCodeBaseViewModel<*>) {
            (viewModel as ChangePassCodeBaseViewModel<*>).parentViewModel =
                ViewModelProviders.of(requireActivity()).get(ChangePassCodeViewModel::class.java)
        }
    }
}