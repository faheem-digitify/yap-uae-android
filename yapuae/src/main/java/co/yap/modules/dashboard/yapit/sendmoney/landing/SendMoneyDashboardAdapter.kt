package co.yap.modules.dashboard.yapit.sendmoney.landing

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemYapItSendMoneyLandingBinding
import co.yap.modules.dashboard.yapit.sendmoney.landing.viewmodels.SendMoneyDashboardItemViewModel
import co.yap.modules.dashboard.yapit.sendmoney.main.SendMoneyOptions
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.interfaces.OnItemClickListener

class SendMoneyDashboardAdapter(
    context: Context,
    private val list: MutableList<SendMoneyOptions>
) :
    BaseBindingRecyclerAdapter<SendMoneyOptions, RecyclerView.ViewHolder>(list) {
    private var dimensions: IntArray = Utils.getCardDimensions(context, 43, 20)

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return ViewHolder(
            binding as ItemYapItSendMoneyLandingBinding
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) holder.onBind(
            list[position],
            position,
            dimensions,
            onItemClickListener
        )
    }

    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_yap_it_send_money_landing

    class ViewHolder(private val itemYapItSendMoneyBinding: ItemYapItSendMoneyLandingBinding) :
        RecyclerView.ViewHolder(itemYapItSendMoneyBinding.root) {
        fun onBind(
            addMoneyOptions: SendMoneyOptions,
            position: Int,
            dimensions: IntArray,
            onItemClickListener: OnItemClickListener?
        ) {
            val params =
                itemYapItSendMoneyBinding.clMain.layoutParams as GridLayoutManager.LayoutParams
            params.width = dimensions[0]
            params.height = dimensions[1]
            itemYapItSendMoneyBinding.clMain.layoutParams = params
            itemYapItSendMoneyBinding.viewModel =
                SendMoneyDashboardItemViewModel(
                    addMoneyOptions,
                    position,
                    onItemClickListener
                )
            itemYapItSendMoneyBinding.executePendingBindings()
        }
    }
}
