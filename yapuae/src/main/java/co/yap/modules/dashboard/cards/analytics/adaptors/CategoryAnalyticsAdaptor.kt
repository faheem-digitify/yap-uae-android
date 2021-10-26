package co.yap.modules.dashboard.cards.analytics.adaptors

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemAnalyticsBinding
import co.yap.yapuae.databinding.ItemAnalyticsCategortyBinding
import co.yap.modules.dashboard.cards.analytics.adaptors.viewholders.CategoryAnalyticsItemViewHolder
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.yapcore.BaseBindingRecyclerAdapter

class CategoryAnalyticsAdaptor(private val list: MutableList<TxnAnalytic>) :
    BaseBindingRecyclerAdapter<TxnAnalytic, RecyclerView.ViewHolder>(list) {

    var checkedPosition: Int = -1
    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_analytics_categorty

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return CategoryAnalyticsItemViewHolder(binding as ItemAnalyticsCategortyBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is CategoryAnalyticsItemViewHolder) {
            holder.onBind(this, list[position], position, onItemClickListener)
        }
    }
}
