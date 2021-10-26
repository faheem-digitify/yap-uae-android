package co.yap.modules.dashboard.home.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.widget.ImageViewCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemAnalyticsTransactionListBinding
import co.yap.yapuae.databinding.ItemTransactionListBinding
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.translation.Translator.getString
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.enums.TransactionProductCode
import co.yap.yapcore.enums.TransactionStatus
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.ImageBinding
import co.yap.yapcore.helpers.TransactionAdapterType
import co.yap.yapcore.helpers.extentions.*

class TransactionsListingAdapter(
    private val list: MutableList<Transaction>,
    private val adapterType: TransactionAdapterType = TransactionAdapterType.TRANSACTION
) : BaseBindingRecyclerAdapter<Transaction, RecyclerView.ViewHolder>(list) {

    var analyticsItemPosition: Int = 0
    var analyticsItemTitle: String? = null
    var analyticsItemImgUrl: String? = null
    override fun getLayoutIdForViewType(viewType: Int): Int {
        return if (adapterType == TransactionAdapterType.TRANSACTION) R.layout.item_transaction_list else R.layout.item_analytics_transaction_list
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is TransactionListingViewHolder)
            holder.onBind(list[position], position)
        else if (holder is TransactionAnalyticsViewHolder)
            holder.onBind(
                list[position],
                analyticsItemPosition,
                analyticsItemTitle,
                analyticsItemImgUrl, adapterType
            )
    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return if (adapterType == TransactionAdapterType.TRANSACTION) {
            TransactionListingViewHolder(
                binding as ItemTransactionListBinding
            )
        } else
            TransactionAnalyticsViewHolder(
                binding as ItemAnalyticsTransactionListBinding
            )
    }

    class TransactionAnalyticsViewHolder(private val itemAnalyticsTransactionListBinding: ItemAnalyticsTransactionListBinding) :
        RecyclerView.ViewHolder(itemAnalyticsTransactionListBinding.root) {
        fun onBind(
            transaction: Transaction,
            position: Int,
            analyticsItemTitle: String?,
            analyticsItemImgUrl: String?,
            type: TransactionAdapterType
        ) {
            itemAnalyticsTransactionListBinding.viewModel =
                ItemAnalyticsTransactionVM(
                    transaction,
                    position,
                    analyticsItemTitle,
                    analyticsItemImgUrl
                )
            itemAnalyticsTransactionListBinding.dividerBottom.visibility =
                if (type == TransactionAdapterType.TOTAL_PURCHASE) View.VISIBLE else View.GONE
            itemAnalyticsTransactionListBinding.tvCurrency.alpha =
                if (type == TransactionAdapterType.TOTAL_PURCHASE) 0.5f else 1f
            itemAnalyticsTransactionListBinding.ivItemTransaction.setCircularDrawable(
                analyticsItemTitle ?: transaction.merchantName ?: transaction.title ?: "",
                analyticsItemImgUrl ?: transaction.merchantLogo ?: "",
                position, type = Constants.MERCHANT_TYPE

            )
            itemAnalyticsTransactionListBinding.tvTransactionTimeAndCategory.text =
                getString(
                    itemAnalyticsTransactionListBinding.tvCurrency.context,
                    R.string.screen_fragment_home_transaction_time_category,
                    transaction.getTransactionTime(TransactionAdapterType.TOTAL_PURCHASE),
                    DateUtils.reformatStringDate(
                        date = transaction.creationDate ?: "",
                        inputFormatter = DateUtils.SERVER_DATE_FORMAT,
                        outFormatter = DateUtils.FORMAT_SHORT_MONTH_DAY
                    )
                )
            itemAnalyticsTransactionListBinding.executePendingBindings()
        }
    }

    class TransactionListingViewHolder(private val itemTransactionListBinding: ItemTransactionListBinding) :
        RecyclerView.ViewHolder(itemTransactionListBinding.root) {

        fun onBind(transaction: Transaction, position: Int?) {
            val context: Context = itemTransactionListBinding.tvCurrency.context
            handleProductBaseCases(context, transaction, position)

            //prod ticket YM-11574 fix start
            if (transaction.productCode == TransactionProductCode.Y2Y_TRANSFER.pCode) {
                transaction.remarks?.let {
                    itemTransactionListBinding.tvTransactionNote.text = it

                    itemTransactionListBinding.tvTransactionNote.visibility =
                        if (transaction.remarks.isNullOrEmpty() || transaction.remarks.equals(
                                "null"
                            )
                        ) View.GONE else View.VISIBLE
                }

            } else {
                itemTransactionListBinding.tvTransactionNote.visibility = View.GONE
            }
            // prod ticket YM-11574 fix end
            //due to fixes above commenting following code
//            transaction.remarks?.let {
//                itemTransactionListBinding.tvTransactionNote.text = it
//            }

//            itemTransactionListBinding.tvTransactionNote.visibility =
//                if (transaction.remarks.isNullOrEmpty() || transaction.remarks.equals(
//                        "null"
//                    )
//                ) View.GONE else View.VISIBLE


            itemTransactionListBinding.tvTransactionStatus.text = transaction.getStatus()
            itemTransactionListBinding.tvTransactionStatus.visibility =
                if (transaction.getStatus().isEmpty()) View.GONE else View.VISIBLE
            //itemTransactionListBinding.tvCurrency.text = transaction.getCurrency()
            itemTransactionListBinding.tvCurrency.text = transaction.cardHolderBillingCurrency
            itemTransactionListBinding.ivIncoming.setImageResource(transaction.getStatusIcon())

            itemTransactionListBinding.ivIncoming.background =
                if (transaction.getStatusIcon() == co.yap.yapcore.R.drawable.ic_time) context.getDrawable(
                    R.drawable.bg_round_white
                ) else
                    context.getDrawable(android.R.color.transparent)

            itemTransactionListBinding.tvTransactionAmount.text =
                transaction.getFormattedTransactionAmount()
            setContentDataColor(transaction, itemTransactionListBinding)
            if (transaction.isInternationalTransaction()) {
                itemTransactionListBinding.tvForeignCurrency.visibility = View.VISIBLE
                itemTransactionListBinding.tvForeignCurrency.text = getString(
                    context,
                    R.string.common_display_one_variables,
                    transaction.amount?.toString()?.toFormattedCurrency(currency = transaction.currency.toString())?:"0.0"
                )
            }
        }

        private fun handleProductBaseCases(
            context: Context,
            transaction: Transaction,
            position: Int?
        ) {
            val transactionTitle = transaction.getTitle()
            val txnIconResId = transaction.getIcon()
            val categoryTitle: String =
                transaction.getTransferType()
            transaction.productCode?.let {
                if (TransactionProductCode.Y2Y_TRANSFER.pCode == it && !transaction.isTransactionRejected()) {
                    setY2YUserImage(transaction, itemTransactionListBinding, position)
                } else if (TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode == it || TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode == it) {
                    setVirtualCardIcon(transaction, itemTransactionListBinding)
                } else if (TransactionProductCode.ECOM.pCode == it || TransactionProductCode.POS_PURCHASE.pCode == it) {
                    setCategoryIcon(transaction, itemTransactionListBinding, position)
                } else {
                    if (txnIconResId != -1) {
                        itemTransactionListBinding.ivTransaction.setImageResource(txnIconResId)
                    } else {
                        setInitialsAsImage(transaction, itemTransactionListBinding, position)
                    }
                    if (transaction.isTransactionRejected()) itemTransactionListBinding.ivTransaction.background =
                        null

                    ImageViewCompat.setImageTintList(
                        itemTransactionListBinding.ivTransaction,
                        ColorStateList.valueOf(context.getColors(R.color.colorPrimary))
                    )
                }
            }

            itemTransactionListBinding.tvTransactionName.text = transactionTitle
            itemTransactionListBinding.tvTransactionTimeAndCategory.text = getString(
                context,
                R.string.screen_fragment_home_transaction_time_category,
                transaction.getTransactionTime(), categoryTitle
            )
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private fun setCategoryIcon(
            transaction: Transaction,
            itemTransactionListBinding: ItemTransactionListBinding,
            position: Int?
        ) {
            transaction.merchantLogo?.let { logo ->
                itemTransactionListBinding.ivTransaction.loadImage(logo)
                itemTransactionListBinding.ivTransaction.setBackgroundColor(
                    itemTransactionListBinding.ivTransaction.context.getColor(R.color.white)
                )
            } ?: transaction.tapixCategory?.categoryIcon?.let { icon ->
                ImageBinding.loadAnalyticsAvatar(
                    itemTransactionListBinding.ivTransaction,
                    transaction.tapixCategory?.categoryIcon,
                    transaction.merchantName,
                    position ?: 0,
                    true,
                    false,
                    if (transaction.isCategoryGeneral() == true) itemTransactionListBinding.ivTransaction.context.getDrawable(
                        R.drawable.ic_category_general
                    ) else null
                )
            } ?: setInitialsAsImage(transaction, itemTransactionListBinding, position)
        }

        private fun setY2YUserImage(
            transaction: Transaction,
            itemTransactionListBinding: ItemTransactionListBinding, position: Int?
        ) {
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

        private fun setInitialsAsImage(
            transaction: Transaction,
            itemTransactionListBinding: ItemTransactionListBinding, position: Int?
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

        private fun setVirtualCardIcon(
            transaction: Transaction,
            itemTransactionListBinding: ItemTransactionListBinding
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

                    itemTransactionListBinding.ivTransaction.background = null
                    itemTransactionListBinding.ivTransaction.background = gd
                    itemTransactionListBinding.ivTransaction.setImageResource(R.drawable.ic_virtual_card_yap_it)

                } catch (e: Exception) {
                }
            }
                ?: itemTransactionListBinding.ivTransaction.setImageResource(R.drawable.ic_virtual_card_yap_it)
        }

        private fun setContentDataColor(
            transaction: Transaction,
            itemTransactionListBinding: ItemTransactionListBinding
        ) {

            val context = itemTransactionListBinding.ivIncoming.context
            val isTxnCancelled = transaction.isTransactionRejected()
            itemTransactionListBinding.tvTransactionName.setTextColor(context.getColors(if (isTxnCancelled) R.color.greyNormalDark else R.color.colorMidnightExpress))
            itemTransactionListBinding.tvTransactionTimeAndCategory.setTextColor(
                context.getColors(
                    if (isTxnCancelled) R.color.greyNormalDark else R.color.greyDark
                )
            )
            itemTransactionListBinding.tvTransactionAmount.setTextColor(
                context.getColors(transaction.getTransactionAmountColor())
            )
            itemTransactionListBinding.tvCurrency.setTextColor(context.getColors(if (isTxnCancelled) R.color.greyNormalDark else R.color.greyDark))

            //strike-thru textview
            itemTransactionListBinding.tvTransactionAmount.paintFlags =
                if (transaction.isTransactionRejected() || transaction.status == TransactionStatus.FAILED.name) itemTransactionListBinding.tvTransactionAmount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG else 0
        }
    }
}

