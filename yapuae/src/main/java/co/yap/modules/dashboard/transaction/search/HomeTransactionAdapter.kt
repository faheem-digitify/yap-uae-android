package co.yap.modules.dashboard.transaction.search


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.databinding.ViewDataBinding
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemSearchTransactionChildBinding
import co.yap.yapuae.databinding.ItemTransactionListBinding
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.widgets.advrecyclerview.decoration.IStickyHeaderViewHolder
import co.yap.widgets.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import co.yap.widgets.advrecyclerview.utils.AbstractExpandableItemViewHolder
import co.yap.widgets.advrecyclerview.utils.BaseExpandableRVAdapter
import co.yap.yapcore.enums.TransactionProductCode
import co.yap.yapcore.enums.TransactionStatus
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.ImageBinding
import co.yap.yapcore.helpers.extentions.*

class HomeTransactionAdapter(
    internal var transactionData: MutableMap<String?, List<Transaction>>,
    val expandableItemManager: RecyclerViewExpandableItemManager
) :
    BaseExpandableRVAdapter<Transaction, SearchTransactionChildItemVM, HomeTransactionAdapter.ChildViewHolder, HomeTransactionListData, SearchTransactionGroupItemVM, HomeTransactionAdapter.GroupViewHolder>() {
    var onItemClick: ((view: View, groupPosition: Int, childPosition: Int, data: Transaction?) -> Unit)? =
        null

    init {
        setHasStableIds(true)
    }

    fun setData(transactions: MutableMap<String?, List<Transaction>>?) {
        transactions?.let {
            this.transactionData = transactions
            notifyDataSetChanged()
        } ?: run {
            transactionData = mutableMapOf()
            notifyDataSetChanged()
        }
    }

    @Deprecated("it has bugs")
    fun updateTransactionData(transactions: MutableMap<String?, List<Transaction>>?) {
        transactions?.let {
            var keyToRemove: String? = null
            transactions.keys.forEach { key ->
                if (transactionData.containsKey(key)) {
                    keyToRemove = key
                    return@forEach
                }
            }
            keyToRemove?.let {
                val newTransaction = transactions.getValue(it)
                val oldTransaction = transactionData.getValue(it).toMutableList()
                oldTransaction.addAll(newTransaction)
                transactionData[it] = oldTransaction
                transactions.remove(it)
            }
            val groupCount = groupCount
            transactionData.putAll(transactions)
            expandableItemManager.notifyGroupItemRangeInserted(groupCount, transactions.size)
        }
    }

    override fun getInitialGroupExpandedState(groupPosition: Int) = false

    override fun getChildViewHolder(
        view: View,
        viewModel: SearchTransactionChildItemVM,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ) = ChildViewHolder(view, viewModel, mDataBinding)

    override fun getGroupViewHolder(
        view: View,
        viewModel: SearchTransactionGroupItemVM,
        mDataBinding: ViewDataBinding,
        viewType: Int
    ) = GroupViewHolder(view, viewModel, mDataBinding)

    override fun onBindGroupViewHolder(
        holder: GroupViewHolder,
        groupPosition: Int,
        viewType: Int
    ) {
        val transaction =
            transactionData[transactionData.keys.toList()[groupPosition]] ?: mutableListOf()
        val groupData =
            HomeTransactionListData(transaction = transaction, date = transaction[0].creationDate)
        holder.setItem(groupData, groupPosition)
    }

    override fun onBindChildViewHolder(
        holder: ChildViewHolder,
        groupPosition: Int,
        childPosition: Int,
        viewType: Int
    ) {
        val transaction =
            transactionData[transactionData.keys.toList()[groupPosition]]?.get(childPosition)
        transaction?.let {
            holder.setItem(it, childPosition)
        }
        holder.onClick { view, position, type ->
            onItemClick?.invoke(view, groupPosition, childPosition, transaction)
        }

    }

    override fun getChildLayoutId(viewType: Int) = getChildViewModel(viewType).layoutRes()
    override fun getGroupLayoutId(viewType: Int) = getGroupViewModel(viewType).layoutRes()
    override fun getGroupViewModel(viewType: Int) = SearchTransactionGroupItemVM()
    override fun getGroupVariableId() = BR.homeTransactionGroupItemVM
    override fun getChildViewModel(viewType: Int) = SearchTransactionChildItemVM()
    override fun getChildVariableId() = BR.homeTransactionChildItemVM
    override fun getGroupCount() = transactionData.size
    override fun getChildCount(groupPosition: Int) =
        transactionData[transactionData.keys.toList()[groupPosition]]?.size ?: 0

    override fun getGroupId(groupPosition: Int) = groupPosition.plus(1L)
    override fun getChildId(groupPosition: Int, childPosition: Int) =
        transactionData[transactionData.keys.toList()[groupPosition]]?.get(childPosition)?.id?.toLong()
            ?: childPosition.plus(groupPosition).toLong()

    override fun getGroupItemViewType(groupPosition: Int) = groupPosition
    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        val id: Int =
            transactionData[transactionData.keys.toList()[groupPosition]]?.get(childPosition)?.id
                ?: childPosition.plus(groupPosition)
        return id
    }


    class GroupViewHolder(
        view: View,
        viewModel: SearchTransactionGroupItemVM,
        mDataBinding: ViewDataBinding
    ) : AbstractExpandableItemViewHolder<HomeTransactionListData, SearchTransactionGroupItemVM>(
        view,
        viewModel,
        mDataBinding
    ), IStickyHeaderViewHolder

    inner class ChildViewHolder(
        view: View,
        viewModel: SearchTransactionChildItemVM,
        mDataBinding: ViewDataBinding
    ) : AbstractExpandableItemViewHolder<Transaction, SearchTransactionChildItemVM>(
        view,
        viewModel,
        mDataBinding
    ) {
        private var transaction: Transaction? = null
        private var mPosition: Int? = null
        private var binding: ItemSearchTransactionChildBinding =
            mDataBinding as (ItemSearchTransactionChildBinding)

        override fun setItem(item: Transaction, position: Int) {
            super.setItem(item, position)
            transaction = item
            handleProductBaseCases(itemView.context, item, position)

            //prod ticket YM-11574 fix start
            if (item?.productCode == TransactionProductCode.Y2Y_TRANSFER.pCode) {
                item?.remarks?.let {
                    binding.tvTransactionNote.text = it

                    binding.tvTransactionNote.visibility =
                        if (item?.remarks.isNullOrEmpty() || item?.remarks.equals(
                                "null"
                            )
                        ) View.GONE else View.VISIBLE
                }

            } else {
                binding.tvTransactionNote.visibility = View.GONE
            }

//            item.remarks?.let {
//                binding.tvTransactionNote.text = it
//            }
//
//            binding.tvTransactionNote.visibility =
//                if (item.remarks.isNullOrEmpty() || item.remarks.equals(
//                        "null"
//                    )
//                ) View.GONE else View.VISIBLE
        }

        private fun handleProductBaseCases(
            context: Context,
            transaction: Transaction,
            position: Int?
        ) {

            binding.tvTransactionAmount.setTextColor(
                itemView.context.getColors(transaction.getTransactionAmountColor())
            )
            binding.tvTransactionAmount.paintFlags =
                if (transaction.isTransactionRejected() || transaction.status == TransactionStatus.FAILED.name) binding.tvTransactionAmount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG else 0

            binding.ivIncoming.setImageResource(transaction.getStatusIcon())

            binding.ivIncoming.background =
                if (transaction.getStatusIcon() == co.yap.yapcore.R.drawable.ic_time) context.getDrawable(
                    R.drawable.bg_round_white
                ) else
                    context.getDrawable(android.R.color.transparent)
            val txnIconResId = transaction.getIcon()
            transaction.productCode?.let {
                if (TransactionProductCode.Y2Y_TRANSFER.pCode == it && !transaction.isTransactionRejected()) {
                    setY2YUserImage(transaction, binding, position)
                } else if (TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode == it || TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode == it) {
                    setVirtualCardIcon(transaction, binding)
                } else if (TransactionProductCode.ECOM.pCode == it || TransactionProductCode.POS_PURCHASE.pCode == it) {
                    setCategoryIcon(transaction, binding, position)
                } else {
                    if (txnIconResId != -1) {
                        binding.ivTransaction.setImageResource(txnIconResId)
                    } else {
                        setInitialsAsImage(transaction, binding, position)
                    }
                    if (transaction.isTransactionRejected()) binding.ivTransaction.background =
                        null
                }
            }

        }


        private fun setVirtualCardIcon(
            transaction: Transaction,
            itemSearchTransactionBinding: ItemSearchTransactionChildBinding
        ) {
            transaction.virtualCardDesign?.let {
                try {
                    val startColor = Color.parseColor(it.designCodeColors?.firstOrNull()?.colorCode)
                    val endColor = Color.parseColor(
                        if (it.designCodeColors?.size ?: 0 > 1) it.designCodeColors?.get(1)?.colorCode else it.designCodeColors?.firstOrNull()?.colorCode
                    )
                    val gd = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColor, endColor)
                    )
                    gd.shape = GradientDrawable.OVAL

                    itemSearchTransactionBinding.ivTransaction.background = null
                    itemSearchTransactionBinding.ivTransaction.background = gd
                    itemSearchTransactionBinding.ivTransaction.setImageResource(R.drawable.ic_virtual_card_yap_it)

                } catch (e: Exception) {
                }
            }
                ?: itemSearchTransactionBinding.ivTransaction.setImageResource(R.drawable.ic_virtual_card_yap_it)
        }

        private fun setInitialsAsImage(
            transaction: Transaction,
            itemTransactionListBinding: ItemSearchTransactionChildBinding, position: Int?
        ) {
            itemTransactionListBinding.ivTransaction.background = null
            ImageBinding.loadAvatar(
                imageView = itemTransactionListBinding.ivTransaction,
                imageUrl = "",
                fullName = transaction.title,
                position = position ?: 0,
                colorType = "Beneficiary"
            )
        }
        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setCategoryIcon(
            transaction: Transaction,
            itemSearchTransactionBinding: ItemSearchTransactionChildBinding,
            position: Int?
        ) {
            transaction.merchantLogo?.let { logo ->
                itemSearchTransactionBinding.ivTransaction.loadImage(logo)
            } ?: transaction.tapixCategory?.categoryIcon?.let { icon ->
                ImageBinding.loadAnalyticsAvatar(
                    itemSearchTransactionBinding.ivTransaction,
                    transaction.tapixCategory?.categoryIcon,
                    transaction.merchantName,
                    position ?: 0,
                    true,
                    false,
                    if (transaction.isCategoryGeneral() == true) itemSearchTransactionBinding.ivTransaction.context.getDrawable(
                        R.drawable.ic_category_general
                    ) else null
                )
            } ?: setInitialsAsImage(transaction, itemSearchTransactionBinding, position)
        }
        private fun setY2YUserImage(
            transaction: Transaction,
            itemTransactionListBinding: ItemSearchTransactionChildBinding, position: Int?
        ) {
            if (transaction.isTransactionRejected()) {
                if (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode ||
                    transaction.productCode == TransactionProductCode.TOP_UP_VIA_CARD.pCode ||
                    transaction.productCode == TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode
                ) {
                    itemTransactionListBinding.ivTransaction.setImageResource(R.drawable.ic_reverted)
                } else {
                    itemTransactionListBinding.ivTransaction.background = null
                    ImageBinding.loadAvatar(
                        imageView = itemTransactionListBinding.ivTransaction,
                        imageUrl = if (TxnType.valueOf(
                                transaction.txnType ?: ""
                            ) == TxnType.DEBIT
                        ) transaction.receiverProfilePictureUrl else transaction.senderProfilePictureUrl,
                        fullName = if (transaction.txnType == TxnType.DEBIT.type) transaction.receiverName else transaction.senderName,
                        position = position ?: 0,
                        colorType = "Beneficiary"
                    )
                    itemTransactionListBinding.ivTransaction.alpha = 0.5f
                }
            } else {
                itemTransactionListBinding.ivTransaction.background = null
                ImageBinding.loadAvatar(
                    imageView = itemTransactionListBinding.ivTransaction,
                    imageUrl = if (TxnType.valueOf(
                            transaction.txnType ?: ""
                        ) == TxnType.DEBIT
                    ) transaction.receiverProfilePictureUrl else transaction.senderProfilePictureUrl,
                    fullName = if (transaction.txnType == TxnType.DEBIT.type) transaction.receiverName else transaction.senderName,
                    position = position ?: 0,
                    colorType = "Beneficiary"
                )
            }
        }

    }


}
