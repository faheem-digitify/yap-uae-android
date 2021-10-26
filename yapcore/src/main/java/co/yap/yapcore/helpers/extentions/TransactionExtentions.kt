package co.yap.yapcore.helpers.extentions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.format.DateFormat
import android.widget.ImageView
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.widgets.CoreCircularImageView
import co.yap.yapcore.R
import co.yap.yapcore.enums.*
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.DateUtils.FORMATE_MONTH_DAY
import co.yap.yapcore.helpers.DateUtils.SERVER_DATE_FORMAT
import co.yap.yapcore.helpers.ImageBinding
import co.yap.yapcore.helpers.TransactionAdapterType
import co.yap.yapcore.managers.SessionManager
import java.util.*

fun Transaction?.getTitle(): String {
    this?.let { transaction -> // poo4 poo6
        return (when (transaction.productCode) {
            TransactionProductCode.Y2Y_TRANSFER.pCode, TransactionProductCode.UAEFTS.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.CASH_PAYOUT.pCode, TransactionProductCode.DOMESTIC.pCode -> {
                String.format(
                    "%s %s",
                    if (transaction.txnType == TxnType.DEBIT.type) "Sent to" else "Received from",
                    if (transaction.txnType == TxnType.DEBIT.type) transaction.receiverName
                        ?: transaction.title else transaction.senderName
                        ?: transaction.title

                )
            }
            TransactionProductCode.TOP_UP_VIA_CARD.pCode -> {
                transaction.maskedCardNo?.let {
                    String.format(
                        "%s %s",
                        "Top up via",
                        "*" + it.substring(it.length - 4, it.length)
                    )
                } ?: transaction.title ?: "Unknown"

            }
            TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode -> "Remove from ${if (transaction.cardType == CardType.PREPAID.type) transaction.cardName1 ?: "Virtual Card" else transaction.cardName2 ?: "Virtual Card"}"
            TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode -> "Add to ${if (transaction.cardType == CardType.PREPAID.type) transaction.cardName1 ?: "Virtual Card" else transaction.cardName2 ?: "Virtual Card"}"
            TransactionProductCode.POS_PURCHASE.pCode, TransactionProductCode.ECOM.pCode -> "${transaction.merchantName}"
            TransactionProductCode.ATM_WITHDRAWL.pCode -> {
                if (transaction.category.equals(
                        "DECLINE_FEE",
                        true
                    )
                ) "ATM decline fee" else "ATM Withdrawal"
            }
            TransactionProductCode.ATM_DEPOSIT.pCode -> "Cash deposit"
            TransactionProductCode.REFUND_MASTER_CARD.pCode -> "Refund from ${transaction.merchantName}"
            TransactionProductCode.FUND_LOAD.pCode -> transaction.senderName?.let { "Received from ${transaction.senderName}" }
                ?: "Received transfer"


            else -> transaction.title ?: "Unknown"
        })
    } ?: return "Unknown"
}

fun Transaction?.getIcon(): Int {
    return this?.let { transaction ->
        return when {
            transaction.isTransactionRejected() -> R.drawable.ic_transaction_rejected
            else -> when (transaction.productCode) {
                TransactionProductCode.TOP_UP_VIA_CARD.pCode -> {
                    R.drawable.ic_icon_card_transfer
                }
                TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode, TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode -> {
                    R.drawable.ic_plus_transactions
                }
                TransactionProductCode.VIRTUAL_ISSUANCE_FEE.pCode -> {
                    R.drawable.icon_virtual_card_issuance
                }
                else -> return when (transaction.getProductType()) {
                    TransactionProductType.IS_BANK, TransactionProductType.IS_INCOMING -> R.drawable.ic_transaction_bank
                    TransactionProductType.IS_TRANSACTION_FEE -> R.drawable.ic_transaction_fee
                    TransactionProductType.IS_REFUND -> R.drawable.ic_refund
                    TransactionProductType.IS_CASH -> R.drawable.ic_cash_out_trasaction
                    else -> -1
                }
            }
        }
    } ?: -1
}

fun Transaction?.getStatus(): String {
    return when (this?.productCode) {
        TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.ATM_DEPOSIT.pCode -> this.cardAcceptorLocation
            ?: ""
        TransactionProductCode.FUND_LOAD.pCode -> this.otherBankName ?: ""
        else ->
            when {
                this.isTransactionRejected() -> "Rejected transaction"
                this.isTransactionInProgress() -> "Transaction in process"
                else -> ""
            }
    }
}

fun Transaction?.getTransferType(transactionType: TransactionAdapterType? = TransactionAdapterType.TRANSACTION): String {
    this?.let { txn ->
        return when {
            txn.getProductType() == TransactionProductType.IS_TRANSACTION_FEE -> "Fee"
            txn.getProductType() == TransactionProductType.IS_REFUND -> "Refund"
            txn.getProductType() == TransactionProductType.IS_INCOMING -> "Inward bank transfer"
            TransactionProductCode.Y2Y_TRANSFER.pCode == txn.productCode -> "YTY"
            TransactionProductCode.TOP_UP_VIA_CARD.pCode == txn.productCode -> "Add money"
            TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode == txn.productCode || TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode == txn.productCode || TransactionProductCode.ATM_DEPOSIT.pCode == txn.productCode || TransactionProductCode.FUND_LOAD.pCode == txn.productCode -> {
                if (txn.category.equals("REVERSAL", true)) "Reversal" else "Deposit"
            }
            TransactionProductCode.ATM_WITHDRAWL.pCode == txn.productCode || TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode == txn.productCode || TransactionProductCode.FUND_WITHDRAWL.pCode == txn.productCode || TransactionProductCode.FUNDS_WITHDRAWAL_BY_CHEQUE.pCode == txn.productCode -> {
                if (txn.category.equals("REVERSAL", true)) "Reversal" else "Withdraw money"
            }
            TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode == txn.productCode -> {
                "Money moved"
            }
            /*TransactionProductCode.POS_PURCHASE.pCode == txn.productCode -> {
                "In store shopping"
            }
            TransactionProductCode.ECOM.pCode == txn.productCode -> {
                "Online shopping"
            }*/
            TransactionProductCode.POS_PURCHASE.pCode == txn.productCode || TransactionProductCode.ECOM.pCode == txn.productCode -> {
                setDescriptiveCategory(txn, transactionType)
            }
            TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode == txn.productCode -> {
                "Money moved"
            }
            transactionType == TransactionAdapterType.ANALYTICS_DETAILS -> {
                DateUtils.reformatStringDate(
                    date = this.creationDate ?: "",
                    inputFormatter = SERVER_DATE_FORMAT,
                    outFormatter = FORMATE_MONTH_DAY
                )
            }
            else -> return (when (txn.productCode) {
                TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.CASH_PAYOUT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.UAEFTS.pCode -> {
                    "Send money"
                }
                else ->
                    "Transaction"
            })
        }
    } ?: return "Transaction"
}

fun Transaction?.getStatusIcon(): Int {
    this?.let { transaction ->
        when {
            transaction.isTransactionInProgress() -> return R.drawable.ic_time
            transaction.isTransactionRejected() -> return android.R.color.transparent
            else -> return when (transaction.productCode) {
                TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.FUNDS_WITHDRAWAL_BY_CHEQUE.pCode, TransactionProductCode.FUND_WITHDRAWL.pCode, TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode -> {
                    R.drawable.ic_identifier_atm_withdrawl
                }
                TransactionProductCode.ATM_DEPOSIT.pCode, TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode, TransactionProductCode.TOP_UP_VIA_CARD.pCode -> {
                    R.drawable.ic_identifier_atm_deposite
                }
                TransactionProductCode.Y2Y_TRANSFER.pCode -> {
                    if (transaction.txnType == TxnType.DEBIT.type) R.drawable.ic_outgoing_transaction_y2y else android.R.color.transparent
                }
                TransactionProductCode.CASH_PAYOUT.pCode, TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.SWIFT.pCode -> {
                    R.drawable.ic_outgoing_transaction_y2y
                }
                else -> android.R.color.transparent
            }
        }
    } ?: return android.R.color.transparent
}


fun String?.getMerchantCategoryIcon(): Int {
    this?.let { title ->
        return ImageBinding.getResId(
            "ic_" + ImageBinding.getDrawableName(
                title
            ) + "_no_bg"
        )
    } ?: return -1
}

fun Transaction?.getCurrency(): String {
    this?.let { transaction ->
        return (when (transaction.productCode) {
            TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode -> {
                transaction.currency.toString()
            }
            TransactionProductCode.POS_PURCHASE.pCode, TransactionProductCode.ECOM.pCode -> {
                transaction.cardHolderBillingCurrency
                    ?: SessionManager.getDefaultCurrency()
            }
            else -> transaction.currency.toString()
        })
    } ?: return SessionManager.getDefaultCurrency()
}

fun Transaction?.getProductType(): TransactionProductType? {
    this?.productCode?.let { productCode ->
        return (when (productCode) {
            TransactionProductCode.MANUAL_ADJUSTMENT.pCode, TransactionProductCode.VIRTUAL_ISSUANCE_FEE.pCode, TransactionProductCode.FSS_FUNDS_WITHDRAWAL.pCode, TransactionProductCode.CARD_REORDER.pCode, TransactionProductCode.FEE_DEDUCT.pCode, TransactionProductCode.PHYSICAL_ISSUANCE_FEE.pCode, TransactionProductCode.BALANCE_INQUIRY.pCode, TransactionProductCode.PIN_CHANGE.pCode, TransactionProductCode.MINISTATEMENT.pCode, TransactionProductCode.ACCOUNT_STATUS_INQUIRY.pCode, TransactionProductCode.FSS_FEE_NOTIFICATION.pCode -> {
                TransactionProductType.IS_TRANSACTION_FEE
            }
            TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.PAYMENT_TRANSACTION.pCode, TransactionProductCode.MOTO.pCode, TransactionProductCode.ECOM.pCode -> {
                TransactionProductType.IS_BANK
            }
            TransactionProductCode.CASH_PAYOUT.pCode, TransactionProductCode.CASH_ADVANCE.pCode, TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.ATM_DEPOSIT.pCode, TransactionProductCode.FUND_WITHDRAWL.pCode, TransactionProductCode.FUNDS_WITHDRAWAL_BY_CHEQUE.pCode -> {
                TransactionProductType.IS_CASH
            }
            TransactionProductCode.REFUND_MASTER_CARD.pCode, TransactionProductCode.REVERSAL_MASTER_CARD.pCode, TransactionProductCode.REVERSAL_OF_TXN_ON_FAILURE.pCode -> {
                TransactionProductType.IS_REFUND
            }
            TransactionProductCode.FUND_LOAD.pCode, TransactionProductCode.LOCAL_INWARD_TRANSFER.pCode, TransactionProductCode.INWARD_REMITTANCE.pCode -> {
                TransactionProductType.IS_INCOMING
            }
            TransactionProductCode.Y2Y_TRANSFER.pCode, TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.CASH_PAYOUT.pCode -> {
                TransactionProductType.IS_SEND_MONEY
            }
            else -> null
        })
    } ?: return null
}

fun Transaction?.getFormattedDate(): String? {
    this?.creationDate?.let {
        val date = DateUtils.convertServerDateToLocalDate(it)
        date?.let { convertedDate ->
            val smsTime: Calendar = Calendar.getInstance()
            smsTime.timeInMillis = convertedDate.time
            //smsTime.timeZone = TimeZone.getDefault()

            val now: Calendar = Calendar.getInstance()
            val timeFormatString = "MMMM dd"
            val dateTimeFormatString = "EEEE, MMMM d"
            return when {
                now.get(Calendar.DATE) === smsTime.get(Calendar.DATE) -> {
                    "Today, " + DateFormat.format(timeFormatString, smsTime)
                }
                now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) === 1 -> {
                    "Yesterday, " + DateFormat.format(timeFormatString, smsTime)
                }
                now.get(Calendar.YEAR) === smsTime.get(Calendar.YEAR) -> {
                    DateFormat.format(dateTimeFormatString, smsTime).toString()
                }
                else -> {
                    DateFormat.format(timeFormatString, smsTime).toString()
                }
            }
        } ?: return null
    } ?: return null
}

fun Transaction.getTransactionTime(adapterType: TransactionAdapterType = TransactionAdapterType.TRANSACTION): String {
    //now we will show 12h format in whole app. Remove conditions after verifying at prod
    return when (adapterType) {
        TransactionAdapterType.ANALYTICS_DETAILS -> {
            getFormattedTime(DateUtils.FORMAT_TIME_12H)
        }
        TransactionAdapterType.TRANSACTION -> {
            getFormattedTime(DateUtils.FORMAT_TIME_12H)
        }
        else -> {
            getFormattedTime(DateUtils.FORMAT_TIME_12H)
        }
    }
}

fun Transaction?.getFormattedTime(outputFormat: String = DateUtils.FORMAT_TIME_24H): String {
    return (when {
        DateUtils.reformatStringDate(
            this?.updatedDate ?: "",
            SERVER_DATE_FORMAT,
            outputFormat
        ).isBlank() -> DateUtils.reformatStringDate(
            this?.creationDate ?: "",
            SERVER_DATE_FORMAT,
            outputFormat
        )
        else -> DateUtils.reformatStringDate(
            this?.creationDate ?: "",
            SERVER_DATE_FORMAT,
            outputFormat
        )
    })
}

fun Transaction?.getTransactionNoteDate(outputFormat: String = DateUtils.FORMAT_TIME_24H): String {
    return (DateUtils.reformatStringDate(
        if (this?.txnType == TxnType.DEBIT.type) this.transactionNoteDate
            ?: "" else this?.receiverTransactionNoteDate ?: "",
        SERVER_DATE_FORMAT,
        outputFormat
    ))
}

fun Transaction?.isTransactionRejected(): Boolean {
    return this?.status == TransactionStatus.CANCELLED.name || this?.status == TransactionStatus.FAILED.name
}

fun Transaction?.isTransactionInProgress(): Boolean {
    return (TransactionStatus.IN_PROGRESS.name == this?.status
            && (this.productCode == TransactionProductCode.SWIFT.pCode
            || this.productCode == TransactionProductCode.UAEFTS.pCode)
            && (this.txnState == TransactionState.FSS_START.name || this.txnState == TransactionState.FSS_NOTIFICATION_PENDING.name || this.txnState == TransactionState.RAK_CUT_OFF_TIME_HOLD.name || this.txnState == TransactionState.FSS_TIMEOUT.name || this.txnState == TransactionState.FSS_REVERSAL_PENDING.name)
            )
}

fun Transaction?.getTransactionAmountPrefix(): String {
    //return if (this?.status == TransactionStatus.PENDING.name || this?.status == TransactionStatus.IN_PROGRESS.name) ""
    //else
    return when (this?.txnType) {
        TxnType.DEBIT.type -> "-"
        TxnType.CREDIT.type -> "+"
        else -> ""
    }
}

fun Transaction?.getAmount(): Double {
    this?.let {
        return when {
            it.productCode == TransactionProductCode.SWIFT.pCode || it.productCode == TransactionProductCode.RMT.pCode || it.isNonAEDTransaction() || it.productCode == TransactionProductCode.REFUND_MASTER_CARD.pCode -> {
                if (it.productCode == TransactionProductCode.POS_PURCHASE.pCode || it.productCode == TransactionProductCode.ECOM.pCode || it.productCode == TransactionProductCode.REFUND_MASTER_CARD.pCode) it.cardHolderBillingTotalAmount
                    ?: 0.0 else it.amount ?: 0.0
            }
            it.productCode == TransactionProductCode.POS_PURCHASE.pCode || it.productCode == TransactionProductCode.ECOM.pCode -> it.cardHolderBillingTotalAmount
                ?: 0.0
            else -> if (it.txnType == TxnType.DEBIT.type) it.totalAmount ?: 0.00 else it.amount
                ?: 0.00
        }

    } ?: return 0.00
}

fun Transaction?.getFormattedTransactionAmount(): String? {
    return String.format(
        "%s %s", this?.getTransactionAmountPrefix(),
        //this?.getAmount().toString().toFormattedCurrency(
        this?.cardHolderBillingTotalAmount?.toString()?.toFormattedCurrency(
            showCurrency = false,
            currency = this.currency ?: SessionManager.getDefaultCurrency()
        ) ?: 0.0
    )
}

fun Transaction?.getFormattedTransactionAmountAnalytics() =
    this?.totalAmount.toString().toFormattedCurrency(
        showCurrency = false,
        currency = this?.currency ?: SessionManager.getDefaultCurrency()
    )

fun Transaction?.getTransactionAmountColor(): Int {
    if (this?.productCode == TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode || this?.productCode == TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode) {
        return R.color.colorPrimaryDark
    } else {
        (return when (this?.txnType) {
            TxnType.DEBIT.type -> R.color.colorPrimaryDark
            TxnType.CREDIT.type ->// {
                //if (!this.isTransactionInProgress() && this.status != TransactionStatus.FAILED.name)
                R.color.colorSecondaryGreen
            //else
            //R.color.colorPrimaryDark
            //}
            else -> R.color.colorPrimaryDark
        })
    }
}

fun Transaction?.showCutOffMsg(): Boolean {
    return (this?.productCode == TransactionProductCode.SWIFT.pCode && this.txnState == TransactionState.RAK_CUT_OFF_TIME_HOLD.name)
}


fun List<Transaction>?.getTotalAmount(): String {
    var total = 0.0
    this?.map {
        when (it.productCode) {
            TransactionProductCode.RMT.pCode, TransactionProductCode.SWIFT.pCode -> {
                if (it.txnType == TxnType.DEBIT.type) {
                    val totalFee = (it.postedFees ?: 0.00).plus(it.vatAmount ?: 0.0)
                    total -= ((it.settlementAmount ?: 0.00).plus(totalFee))
                } else total += (it.settlementAmount ?: 0.0)
            }
            else -> {
                if (it.txnType == TxnType.DEBIT.type) total -= (it.totalAmount
                    ?: 0.0) else total += (it.amount ?: 0.0)
            }
        }
    }

    var totalAmount: String
    when {
        total.toString().startsWith("-") -> {
            totalAmount =
                ((total * -1).toString().toFormattedCurrency(
                    showCurrency = false,
                    currency = SessionManager.getDefaultCurrency()
                ))
            totalAmount = "- ${SessionManager.getDefaultCurrency()} $totalAmount"
        }
        else -> {
            totalAmount = (total.toString()
                .toFormattedCurrency(false, currency = SessionManager.getDefaultCurrency()))
            totalAmount = "+ ${SessionManager.getDefaultCurrency()} $totalAmount"
        }
    }
    return totalAmount
}

fun Transaction?.isNonAEDTransaction(): Boolean {
    return (this?.productCode == TransactionProductCode.POS_PURCHASE.pCode || this?.productCode == TransactionProductCode.ATM_DEPOSIT.pCode || this?.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode || this?.productCode == TransactionProductCode.ECOM.pCode) && this.currency != SessionManager.getDefaultCurrency()
}

fun Transaction.getTransactionStatusMessage(context: Context): String {
    return when {
        this.isTransactionRejected() -> {
            context.getString(R.string.screen_transaction_detail_text_cancelled_reason)
        }
        this.showCutOffMsg() -> {
            context.getString(R.string.screen_transaction_detail_text_cut_off_msg)
        }
        else -> ""
    }
}

fun Transaction?.setTransactionImage(imageView: CoreCircularImageView) {
    this?.let { transaction ->
        when (TransactionProductCode.Y2Y_TRANSFER.pCode) {
            transaction.productCode ?: "" -> {
                ImageBinding.loadAvatar(
                    imageView,
                    if (TxnType.valueOf(
                            transaction.txnType ?: ""
                        ) == TxnType.DEBIT
                    ) transaction.receiverProfilePictureUrl else transaction.senderProfilePictureUrl,
                    if (transaction.txnType == TxnType.DEBIT.type) transaction.receiverName else transaction.senderName,
                    android.R.color.transparent,
                    R.dimen.text_size_h2
                )
            }
            else -> {
                val txnIconResId = transaction.getIcon()
                if (transaction.productCode == TransactionProductCode.ECOM.pCode) {
                    setInitialsAsTxnImage(transaction, imageView)
                } else if (transaction.productCode == TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode || transaction.productCode == TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode) {
                    setVirtualCardIcon(transaction, imageView)
                } else if (txnIconResId != -1) {
                    imageView.setImageResource(txnIconResId)
                    when (txnIconResId) {
                        R.drawable.ic_rounded_plus -> {
                            imageView.setBackgroundResource(R.drawable.bg_round_grey)
                        }
                        R.drawable.ic_grey_minus_transactions, R.drawable.ic_grey_plus_transactions -> {
                            imageView.setBackgroundResource(R.drawable.bg_round_disabled_transaction)
                        }
                    }
                } else
                    setInitialsAsTxnImage(transaction, imageView)
            }
        }
    }
}

private fun setInitialsAsTxnImage(transaction: Transaction, imageView: CoreCircularImageView) {
    ImageBinding.loadAvatar(
        imageView,
        "",
        transaction.merchantName ?: transaction.title,
        android.R.color.transparent,
        R.dimen.text_size_h2
    )

}

private fun setVirtualCardIcon(
    transaction: Transaction,
    imageView: ImageView
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

            imageView.background = null
            imageView.background = gd
            imageView.setImageResource(R.drawable.ic_virtual_card_yap_it)

        } catch (e: Exception) {
        }
    } ?: imageView.setImageResource(R.drawable.ic_virtual_card_yap_it)
}

fun Transaction?.isCategoryGeneral(): Boolean? = this?.let { transaction ->
    (transaction.productCode == TransactionProductCode.ECOM.pCode || transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode)
            && transaction.tapixCategory == null || transaction.tapixCategory?.isGeneral == true
}

fun setDescriptiveCategory(txn: Transaction, transactionType: TransactionAdapterType?): String {
    return when {
        txn.productCode == TransactionProductCode.POS_PURCHASE.pCode && transactionType == TransactionAdapterType.TRANSACTION -> {
            txn.tapixCategory?.let { category ->
                if (category.isGeneral) "In store shopping" else category.categoryName
            } ?: "In store shopping"
        }
        else -> {
            txn.tapixCategory?.let { category ->
                if (category.isGeneral) "Online shopping" else category.categoryName
            } ?: "Online shopping"
        }
    }
}

fun Transaction?.isInternationalTransaction(): Boolean {
    return (this?.productCode == TransactionProductCode.POS_PURCHASE.pCode || this?.productCode == TransactionProductCode.ATM_DEPOSIT.pCode ||
            this?.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode || this?.productCode == TransactionProductCode.ECOM.pCode ||
            this?.productCode == TransactionProductCode.SWIFT.pCode || this?.productCode == TransactionProductCode.RMT.pCode)
            && this.currency != SessionManager.getDefaultCurrency()
}

fun Transaction?.isEcomPosTransaction(): Boolean {
    return (this?.productCode == TransactionProductCode.POS_PURCHASE.pCode || this?.productCode == TransactionProductCode.ECOM.pCode)
}
