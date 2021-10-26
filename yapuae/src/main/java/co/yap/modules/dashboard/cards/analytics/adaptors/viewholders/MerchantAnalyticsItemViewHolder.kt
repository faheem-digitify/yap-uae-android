package co.yap.modules.dashboard.cards.analytics.adaptors.viewholders

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemMarchentAnalyticsBinding
import co.yap.modules.dashboard.cards.analytics.adaptors.MerchantAnalyticsAdaptor
import co.yap.modules.dashboard.cards.analytics.viewmodels.AnalyticsItemViewModel
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.yapcore.helpers.extentions.getColors
import co.yap.yapcore.interfaces.OnItemClickListener

class MerchantAnalyticsItemViewHolder(private val itemAnalyticsBinding: ItemMarchentAnalyticsBinding) :
    RecyclerView.ViewHolder(itemAnalyticsBinding.root) {

    @SuppressLint("SetTextI18n")
    fun onBind(
        adapter: MerchantAnalyticsAdaptor?,
        analyticsItem: TxnAnalytic?,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {

        if (adapter?.checkedPosition != -1) {

            itemView.apply {
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (adapter?.checkedPosition == position) R.color.itemBackground else R.color.white
                    )
                )
                itemAnalyticsBinding.tvName.setTextColor(
                    if (adapter?.checkedPosition == position) context.getColors(R.color.colorMidnightExpress)
                    else context.getColors(R.color.colorMidnightExpress)
                )
                isSelected = adapter?.checkedPosition == position
                isSelected = adapter?.checkedPosition == position
            }
        }



        itemAnalyticsBinding.viewModel =
            AnalyticsItemViewModel(analyticsItem, position, onItemClickListener)
        itemAnalyticsBinding.executePendingBindings()
    }
}