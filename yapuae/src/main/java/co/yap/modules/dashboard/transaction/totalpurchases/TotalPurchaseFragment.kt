package co.yap.modules.dashboard.transaction.totalpurchases

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.R
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.extentions.setTransactionImage
import kotlinx.android.synthetic.main.activity_total_purchase.*

class TotalPurchaseFragment : BaseBindingFragment<ITotalPurchases.ViewModel>(),
    ITotalPurchases.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_total_purchase

    override val viewModel: TotalPurchasesViewModel
        get() = ViewModelProviders.of(this).get(TotalPurchasesViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArguments()
    }

    private fun initArguments() {
        arguments?.let { bundle ->
            val txn = bundle.getParcelable<Transaction>(Constants.TRANSACTION_DETAIL)
            viewModel.transaction.set(txn)
            val count = bundle.getInt(Constants.TRANSACTION_COUNT)
            viewModel.state.toolbarTitle = "$count transactions"
            val total = bundle.getDouble(Constants.TOTAL_TRANSACTION)
            viewModel.state.totalSpendings.set(total.toString())
            viewModel.transaction.get().setTransactionImage(ivMerchantLogo)
        }
    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> {
                requireActivity().finish()
            }
        }
    }
}