package co.yap.modules.dashboard.home.adaptor

import android.view.Gravity
import android.view.View
import androidx.core.view.updatePadding
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemBarChartV2Binding
import co.yap.modules.dashboard.home.component.ChartViewV2
import co.yap.modules.dashboard.home.helpers.transaction.TransactionsViewHelper
import co.yap.modules.dashboard.home.interfaces.IYapHome
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.helpers.extentions.dimen
import co.yap.yapcore.helpers.extentions.getScreenWidth
import kotlinx.android.synthetic.main.item_bar_chart_v2.view.*

class GraphBarsAdapter(
    private val listItems: MutableList<HomeTransactionListData>,
    val viewModel: IYapHome.ViewModel
) : BaseBindingRecyclerAdapter<HomeTransactionListData, GraphBarsAdapter.GraphViewHolder>(listItems),
    View.OnFocusChangeListener {

    private var checkedPosition = 0
    var helper: TransactionsViewHelper? = null

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_bar_chart_v2

    override fun onCreateViewHolder(binding: ViewDataBinding): GraphViewHolder {
        return GraphViewHolder(binding as ItemBarChartV2Binding, viewModel)
    }

    override fun onBindViewHolder(holder: GraphViewHolder, position: Int) {
        val transactionModel: HomeTransactionListData = listItems[position]
        holder.transactionBar.onFocusChangeListener = this
        holder.onBind(position, transactionModel)

        if (checkedPosition == -1) {
            holder.transactionBar.needAnimation = false
            holder.transactionBar.isSelected = false

        } else {
            holder.transactionBar.isSelected = checkedPosition == holder.adapterPosition
        }

        holder.itemView.setOnClickListener { v ->

            if (checkedPosition != holder.adapterPosition) {
                holder.transactionBar.needAnimation = true
                holder.transactionBar.isSelected = true
                helper?.addTooltip(v.findViewById(R.id.transactionBar), transactionModel)
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.adapterPosition
                helper?.barSelectedPosition = checkedPosition
            }
        }
        holder.itemView.isClickable = false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        //if (!viewHolder.transactionBar.hasFocus()) {
        //   viewHolder.transactionBar.unSelectHighlightedBarOnGraphClick(hasFocus)
        //}
    }

    override fun getItemCount(): Int {
        helper?.totalItemCount = listItems.size
        return listItems.size
    }

    class GraphViewHolder(
        private val itemBarChartBinding: ItemBarChartV2Binding,
        private val viewModel: IYapHome.ViewModel
    ) : RecyclerView.ViewHolder(itemBarChartBinding.root) {
        val transactionBar: ChartViewV2 = itemView.transactionBar

        fun onBind(position: Int, transactionModel: HomeTransactionListData) {
            if (position == 0) {
                val prams =
                    (itemBarChartBinding.barParent.layoutParams as RecyclerView.LayoutParams)
                prams.width = getScreenWidth() / 2
                itemBarChartBinding.barParent.layoutParams = prams
                itemBarChartBinding.barParent.gravity = Gravity.START or Gravity.BOTTOM
                itemBarChartBinding.transactionBar.updatePadding(right = getScreenWidth() / 2)
            } else {
                val prams =
                    (itemBarChartBinding.barParent.layoutParams as RecyclerView.LayoutParams)
                prams.width = itemBarChartBinding.transactionBar.context.dimen(R.dimen._9sdp)
                itemBarChartBinding.barParent.layoutParams = prams
            }
            transactionModel.amountPercentage =
                calculatePercentagePerDayFromClosingBalance(transactionModel.closingBalance ?: 0.0)
            transactionBar.barHeight = transactionModel.amountPercentage?.toFloat() ?: 0f


//            val params = binding.cvNotification.layoutParams as RecyclerView.LayoutParams
//            params.width = dimensions[0]
//            //params.height = dimensions[1]
//            binding.cvNotification.layoutParams = params
        }

        private fun calculatePercentagePerDayFromClosingBalance(closingBalance: Double): Double {
            closingBalance.toString().replace("-", "").toDouble()
            val percentage: Double = (closingBalance.toString().replace(
                "-",
                ""
            ).toDouble() / viewModel.MAX_CLOSING_BALANCE)
            return percentage
        }
    }
}