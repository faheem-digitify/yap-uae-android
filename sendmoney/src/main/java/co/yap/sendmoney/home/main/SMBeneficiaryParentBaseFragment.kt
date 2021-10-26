package co.yap.sendmoney.home.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.IBase

abstract class SMBeneficiaryParentBaseFragment<V : IBase.ViewModel<*>> : BaseBindingFragment<V>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel is SMBeneficiaryParentBaseViewModel<*>) {
            (viewModel as SMBeneficiaryParentBaseViewModel<*>).parentViewModel =
                ViewModelProviders.of(activity!!).get(SMBeneficiaryParentViewModel::class.java)
        }
    }

}
