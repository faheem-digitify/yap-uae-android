package co.yap.modules.dashboard.cards.analytics.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.cards.analytics.interfaces.ICardAnalyticsDetails
import co.yap.modules.dashboard.cards.analytics.main.fragments.CardAnalyticsBaseFragment
import co.yap.modules.dashboard.cards.analytics.viewmodels.CardAnalyticsDetailsViewModel
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.widgets.DividerItemDecoration
import co.yap.widgets.MultiStateView
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.extentions.dimen
import co.yap.yapcore.helpers.extentions.getMerchantCategoryIcon
import co.yap.yapcore.helpers.extentions.setCircularDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import kotlinx.android.synthetic.main.fragment_card_analytics_details.*

class CardAnalyticsDetailsFragment : CardAnalyticsBaseFragment<ICardAnalyticsDetails.ViewModel>() {
    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutId() = R.layout.fragment_card_analytics_details

    override val viewModel: CardAnalyticsDetailsViewModel
        get() = ViewModelProviders.of(this).get(CardAnalyticsDetailsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgument()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        initView()
    }

    private fun getArgument() {
        arguments?.let { bundle ->
            bundle.getParcelable<TxnAnalytic>(Constants.TRANSACTION_DETAIL)?.let { txnAnalytics ->
                viewModel.state.title.set(txnAnalytics.title ?: "")
                viewModel.state.totalSpendings.set(txnAnalytics.totalSpending)
                viewModel.state.ImageUrl.set(txnAnalytics.logoUrl)
                viewModel.state.countWithDate.set(
                    viewModel.getConcatinatedString(
                        txnAnalytics.txnCount ?: 0
                    )
                )
                viewModel.state.monthlyTotalPercentage.set("${txnAnalytics.totalSpendingInPercentage}%")
                viewModel.state.categories.set(txnAnalytics.categories as ArrayList<Any>)
                val array = arrayListOf<Any>(txnAnalytics.yapCategoryId ?: 0)
                viewModel.yapCategoryId?.set(array)
                if (txnAnalytics.title.equals("General")) viewModel.state.percentCardVisibility =
                    false
                viewModel.adapter.analyticsItemTitle =
                    if (txnAnalytics.title.getMerchantCategoryIcon() == -1) null else txnAnalytics.title
                viewModel.adapter.analyticsItemImgUrl = txnAnalytics.logoUrl
                viewModel.fetchMerchantTransactions(
                    Constants.MERCHANT_TYPE,
                    viewModel.parentViewModel?.state?.currentSelectedDate ?: ""
                )

            }
            bundle.getInt(Constants.TRANSACTION_POSITION).let { position ->
                viewModel.state.position = position
            }
        }

    }

    private fun setObservers() {
        viewModel.clickEvent?.observe(this, Observer {
            activity?.onBackPressed()
        })
        viewModel.viewState.observe(this, viewStateObserver)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                R.drawable.line_divider,
                false,
                false, dimen(R.dimen._52sdp)!!
            )
        )
    }

    private fun initView() {
        ivUserImage.setCircularDrawable(
            viewModel.state.title.get() ?: "General",
            viewModel.state.ImageUrl.get() ?: "",
            viewModel.state.position,
            type = Constants.MERCHANT_TYPE
        )

        multiStateView.viewState = MultiStateView.ViewState.CONTENT
    }

    private val viewStateObserver = Observer<Int> {
        when (it) {
            Constants.EVENT_LOADING -> {
                multiStateView.viewState = MultiStateView.ViewState.LOADING
            }
            Constants.EVENT_EMPTY -> {
                multiStateView.viewState = MultiStateView.ViewState.EMPTY
            }
            Constants.EVENT_CONTENT -> {
                multiStateView.viewState = MultiStateView.ViewState.CONTENT
            }
        }
    }

    private fun removeOberver() {
        viewModel.viewState.removeObserver(viewStateObserver)
        viewModel.clickEvent?.removeObservers(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeOberver()
    }
}

