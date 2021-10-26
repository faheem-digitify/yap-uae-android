package co.yap.modules.dashboard.transaction.feedback

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentTransactionFeedbackBinding
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.translation.Strings
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.extentions.setTransactionImage
import co.yap.yapcore.helpers.showReceiptSuccessDialog
import co.yap.yapcore.interfaces.OnItemClickListener

class TransactionFeedbackFragment : BaseBindingFragment<ITransactionFeedback.ViewModel>(),
    ITransactionFeedback.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_transaction_feedback
    override val viewModel: ITransactionFeedback.ViewModel
        get() = ViewModelProviders.of(this).get(TransactionFeedbackViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        initArguments()
    }

    private fun initArguments() {
        arguments?.let { bundle ->
            bundle.getParcelable<Transaction>(Constants.TRANSACTION_DETAIL)?.let {
                it.setTransactionImage(getDataBindingView<FragmentTransactionFeedbackBinding>().layoutMerchant.ivMerchantImage)
            }
            val title = bundle.getString(Constants.FEEDBACK_TITLE)
            val location = bundle.getString(Constants.FEEDBACK_LOCATION)
            viewModel.state.title.set(title)
            viewModel.state.location.set(location)
        }
    }

    override fun setObserver() {
        viewModel.clickEvent.observe(this, clickObserver)
        viewModel.adapter.setItemListener(onClickItem)
    }


    val clickObserver = Observer<Int> { id ->
        when (id) {
            R.id.btnDone -> {
                showFeedbackSuccessDialog()
            }
        }
    }
    val onClickItem = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when (view.id) {
                R.id.cbRequireTransaction -> {
                    viewModel.selectFeedback(pos)
                }
            }
        }
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                requireActivity().finish()
            }
        }
    }

    override fun removeObserver() {
        viewModel.clickEvent.removeObserver(clickObserver)
    }

    override fun onDestroyView() {
        removeObserver()
        super.onDestroyView()
    }
    private fun showFeedbackSuccessDialog() {
        requireActivity().showReceiptSuccessDialog(
            description = getString(Strings.screen_transaction_details_feedback_success_label),
            addOtherVisibility = false,
            callback = {
                when (it) {
                    R.id.btnActionDone -> {
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                }
            }
        )
    }
}