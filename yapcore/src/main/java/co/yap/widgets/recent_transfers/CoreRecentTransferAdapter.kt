package co.yap.widgets.recent_transfers

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.countryutils.country.utils.CurrencyUtils
import co.yap.networking.customers.responsedtos.sendmoney.CoreRecentBeneficiaryItem
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.R
import co.yap.yapcore.databinding.ItemCoreRecentBeneficiaryBinding
import co.yap.yapcore.enums.SendMoneyBeneficiaryType
import co.yap.yapcore.interfaces.OnItemClickListener

class CoreRecentTransferAdapter(
    var context: Context,
    private val list: MutableList<CoreRecentBeneficiaryItem>
) :
    BaseBindingRecyclerAdapter<CoreRecentBeneficiaryItem, RecyclerView.ViewHolder>(list) {
    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_core_recent_beneficiary

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return ViewHolder(
            binding as ItemCoreRecentBeneficiaryBinding
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.onBind(list[position], position, onItemClickListener)
        }
    }

    class ViewHolder(private val itemCoreRecentBeneficiaryBinding: ItemCoreRecentBeneficiaryBinding) :
        RecyclerView.ViewHolder(itemCoreRecentBeneficiaryBinding.root) {
        fun onBind(
            coreRecentBeneficiary: CoreRecentBeneficiaryItem,
            position: Int,
            onItemClickListener: OnItemClickListener?
        ) {
            when (coreRecentBeneficiary.type) {
                SendMoneyBeneficiaryType.YAP2YAP.type -> {
                    itemCoreRecentBeneficiaryBinding.coreView.topLefticonInt = null
                    itemCoreRecentBeneficiaryBinding.coreView.bottomRightIconInt =
                        R.drawable.ic_package_standered
                }
                else -> {
                    itemCoreRecentBeneficiaryBinding.coreView.bottomRightIconInt = null
                    coreRecentBeneficiary.isoCountryCode?.let { isoCode ->
                        itemCoreRecentBeneficiaryBinding.coreView.topLefticonInt =
                            CurrencyUtils.getFlagDrawable(
                                itemCoreRecentBeneficiaryBinding.root.context,
                                isoCode
                            )
                    }
                }
            }

            itemCoreRecentBeneficiaryBinding.coreView.position = position
            itemCoreRecentBeneficiaryBinding.viewModel =
                CoreRecentBeneficiaryItemViewModel(
                    coreRecentBeneficiary,
                    position,
                    onItemClickListener
                )
            itemCoreRecentBeneficiaryBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = if (list.size > 15) {
        15
    } else list.size

}