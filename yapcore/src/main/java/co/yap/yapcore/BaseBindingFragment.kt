package co.yap.yapcore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import co.yap.yapcore.helpers.extentions.getCountUnreadMessage
import co.yap.yapcore.managers.isUserLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseBindingFragment<V : IBase.ViewModel<*>> : BaseFragment<V>() {

    lateinit var viewDataBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.setVariable(getBindingVariable(), viewModel)
        //viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        if (shouldShowChatChatOverLay() == true)
            requireActivity().getCountUnreadMessage()
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

    open fun shouldShowChatChatOverLay() = requireContext().isUserLogin()

    fun <VB : ViewDataBinding> getDataBindingView() = viewDataBinding as VB

    /**MV
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
}