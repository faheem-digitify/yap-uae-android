package co.yap.modules.dashboard.cards.analytics.adaptors

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemMarchentAnalyticsBinding
import co.yap.modules.dashboard.cards.analytics.adaptors.viewholders.MerchantAnalyticsItemViewHolder
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.yapcore.BaseBindingRecyclerAdapter

class MerchantAnalyticsAdaptor(private val list: MutableList<TxnAnalytic>) :
    BaseBindingRecyclerAdapter<TxnAnalytic, RecyclerView.ViewHolder>(list) {

    var checkedPosition: Int = -1

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return MerchantAnalyticsItemViewHolder(binding as ItemMarchentAnalyticsBinding)
    }

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_marchent_analytics


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is MerchantAnalyticsItemViewHolder) {
            holder.onBind(this, list[position], position, onItemClickListener)
        }
    }
}