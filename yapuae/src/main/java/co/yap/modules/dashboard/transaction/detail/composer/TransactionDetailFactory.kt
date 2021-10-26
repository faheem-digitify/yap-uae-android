package co.yap.modules.dashboard.transaction.detail.composer

import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.yapcore.R
import co.yap.yapcore.enums.*
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.managers.SessionManager


class TransactionDetailFactory(private val transaction: Transaction) {
    fun label(forTag: TransactionDetailItem): String {
        return when (forTag) {
            TransactionDetailItem.CARD_NUMBER -> "Card"
            TransactionDetailItem.TRANSFER_AMOUNT -> if (transaction.isNonAEDTransaction()) "Spent amount" else "Transfer amount"
            TransactionDetailItem.EXCHANGE_RATE -> "Exchange rate"
            TransactionDetailItem.SENDER -> "Sender"
            TransactionDetailItem.RECEIVER -> "Receiver"
            TransactionDetailItem.SENT_RECEIVED -> if (transaction.isEcomPosTransaction()) "Spent in AED" else "Amount"
            TransactionDetailItem.FEES -> "Fee"
            TransactionDetailItem.VAT -> "VAT"
            TransactionDetailItem.TOTAL -> "Total amount"
            TransactionDetailItem.REFERENCE_NUMBER -> "Reference number"
            TransactionDetailItem.REMARKS -> "Remarks"
        }
    }

    fun value(forTag: TransactionDetailItem): String {
        return when (forTag) {
            TransactionDetailItem.CARD_NUMBER -> {
                transaction.maskedCardNo?.split(" ")?.lastOrNull().let { maskCardNo ->
                    "*${maskCardNo?.takeLast(4)}"
                }
            }
            TransactionDetailItem.TRANSFER_AMOUNT -> {
                //   transaction.amount.toString().toFormattedCurrency(true, transaction.currency, true)
                getForeignAmount().toString().toFormattedCurrency(true, transaction.currency, true)
            }
            TransactionDetailItem.EXCHANGE_RATE -> {
                if (transaction.isNonAEDTransaction()) "${transaction.currency} 1.00 = AED ${
                getExchangeRateForInternationalPOS(
                    transaction
                )
                }" else "${transaction.currency} 1.00 = AED ${transaction.fxRate}"
            }
            TransactionDetailItem.SENDER, TransactionDetailItem.RECEIVER -> {
                if (transaction.txnType == TxnType.CREDIT.type) transaction.senderName
                    ?: "" else transaction.receiverName ?: ""
            }

            TransactionDetailItem.SENT_RECEIVED -> {
                getSpentAmount(transaction).toString()
                    .toFormattedCurrency(showCurrency = transaction.status != TransactionStatus.FAILED.name)
            }
            TransactionDetailItem.FEES -> {
                fee(transaction)
            }
            TransactionDetailItem.VAT -> {
                transaction.vatAmount?.toString()
                    ?.toFormattedCurrency(true, SessionManager.getDefaultCurrency(), true)
                    ?: "AED 0.00"
            }
            TransactionDetailItem.TOTAL -> {
                getCalculatedTotalAmount(transaction).toString().toFormattedCurrency()
            }
            TransactionDetailItem.REFERENCE_NUMBER -> {
                transaction.transactionId ?: ""
            }
            TransactionDetailItem.REMARKS -> {
                transaction.remarks ?: ""
            }
        }
    }

    fun isShowItem(tag: TransactionDetailItem): Boolean {
        return when (tag) {
            TransactionDetailItem.CARD_NUMBER -> {
                transaction.maskedCardNo?.let {
                    true
                } ?: false
            }
            TransactionDetailItem.TRANSFER_AMOUNT -> {
                transaction.isNonAEDTransaction() || (transaction.productCode == TransactionProductCode.SWIFT.pCode || transaction.productCode == TransactionProductCode.RMT.pCode)
            }
            TransactionDetailItem.SENDER -> {
                transaction.getProductType() == TransactionProductType.IS_SEND_MONEY && transaction.txnType == TxnType.CREDIT.type
            }
            TransactionDetailItem.RECEIVER -> {
                transaction.getProductType() == TransactionProductType.IS_SEND_MONEY && transaction.txnType == TxnType.DEBIT.type
            }
            TransactionDetailItem.SENT_RECEIVED -> {
                true
            }
            TransactionDetailItem.TOTAL -> {
                transaction.getProductType() == TransactionProductType.IS_TRANSACTION_FEE && transaction.productCode != TransactionProductCode.MANUAL_ADJUSTMENT.pCode
            }
            TransactionDetailItem.REFERENCE_NUMBER -> {
                true
            }
            TransactionDetailItem.REMARKS -> {
                !transaction.remarks.isNullOrEmpty()
            }
            TransactionDetailItem.FEES, TransactionDetailItem.VAT ->
                return !(transaction.productCode == TransactionProductCode.ECOM.pCode || transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode
                        || transaction.productCode == TransactionProductCode.ATM_DEPOSIT.pCode || transaction.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode)
            TransactionDetailItem.EXCHANGE_RATE -> {
                (transaction.productCode == TransactionProductCode.SWIFT.pCode || transaction.productCode == TransactionProductCode.RMT.pCode)
            }
        }
    }

    private fun isInternationalPOS(transaction: Transaction): Boolean {
        return (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode || transaction.productCode == TransactionProductCode.ECOM.pCode) && transaction.currency != SessionManager.getDefaultCurrency()
    }

    private fun getSpentAmount(transaction: Transaction): Double {
        transaction.let {
            return when {
                it.status == TransactionStatus.FAILED.name -> 0.00
                it.getProductType() == TransactionProductType.IS_TRANSACTION_FEE && it.productCode != TransactionProductCode.MANUAL_ADJUSTMENT.pCode -> {
                    0.00
                }
                it.productCode == TransactionProductCode.SWIFT.pCode || it.productCode == TransactionProductCode.RMT.pCode -> {
                    (it.settlementAmount ?: 0.00)
                }
                /*it.isNonAEDTransaction() -> {
                    it.cardHolderBillingAmount ?: 0.00
                }*/
                it.productCode == TransactionProductCode.POS_PURCHASE.pCode || it.productCode == TransactionProductCode.ATM_DEPOSIT.pCode || it.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode || it.productCode == TransactionProductCode.ECOM.pCode ->
                    (it.cardHolderBillingTotalAmount ?: 0.00)
                else -> it.amount ?: 0.00
            }
        }
    }

    private fun fee(forTransaction: Transaction): String {
        return when {
            transaction.isNonAEDTransaction() || transaction.productCode == TransactionProductCode.REFUND_MASTER_CARD.pCode || transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode || transaction.productCode == TransactionProductCode.ECOM.pCode -> {
                forTransaction.markupFees.toString()
                    .toFormattedCurrency(true, SessionManager.getDefaultCurrency(), true)
            }
            forTransaction.postedFees != null -> {
                forTransaction.postedFees.toString()
                    .toFormattedCurrency(true, SessionManager.getDefaultCurrency(), true)
            }
            else -> {
                "AED 0.00"
            }
        }
    }

    private fun getCalculatedTotalAmount(transaction: Transaction): Double {
        transaction.let {
            return when {
                it.productCode == TransactionProductCode.RMT.pCode || it.productCode == TransactionProductCode.SWIFT.pCode -> {
                    val totalFee = (it.postedFees ?: 0.00).plus(it.vatAmount ?: 0.0)
                    (it.settlementAmount ?: 0.00).plus(totalFee)
                }
                it.isNonAEDTransaction() || it.productCode == TransactionProductCode.REFUND_MASTER_CARD.pCode || it.productCode == TransactionProductCode.POS_PURCHASE.pCode || it.productCode == TransactionProductCode.ECOM.pCode -> {
                    (it.cardHolderBillingTotalAmount ?: 0.00)
                }
                else -> if (it.txnType == TxnType.DEBIT.type) it.totalAmount ?: 0.00 else it.amount
                    ?: 0.00
            }
        }
    }


    private fun getExchangeRateForInternationalPOS(transaction: Transaction): Double {
        val fxRate: Double? =
            transaction.amount?.let { transaction.cardHolderBillingAmount?.div(it) }
        return when {
            transaction.amount?.let { transaction.cardHolderBillingAmount?.compareTo(it) } == -1 -> {
                getDecimalFormatUpTo(
                    6,
                    amount = fxRate.toString(),
                    withComma = true
                ).toDouble()
            }
            else -> {
                getDecimalFormatUpTo(
                    3,
                    amount = fxRate.toString(),
                    withComma = true
                ).toDouble()
            }
        }
    }

    fun getTransactionStatusIcon(): Int {
        return if (transaction.isTransactionInProgress()) android.R.color.transparent
        else when (transaction.productCode) {
            TransactionProductCode.ATM_WITHDRAWL.pCode -> {
                R.drawable.ic_identifier_atm_withdrawl
            }
            TransactionProductCode.ATM_DEPOSIT.pCode -> {
                R.drawable.ic_identifier_atm_deposite
            }

            else -> android.R.color.transparent
        }
    }

    fun getMapImage(): Int {
        transaction?.let { transaction ->
            if (TransactionProductType.IS_TRANSACTION_FEE == transaction.getProductType()) {
                return R.drawable.ic_image_light_red_background
            }
            return (when (transaction.productCode) {
                TransactionProductCode.Y2Y_TRANSFER.pCode -> R.drawable.ic_image_blue_background
                TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode, TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode -> R.drawable.ic_image_brown_background
                TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.CASH_PAYOUT.pCode, TransactionProductCode.TOP_UP_VIA_CARD.pCode, TransactionProductCode.INWARD_REMITTANCE.pCode, TransactionProductCode.LOCAL_INWARD_TRANSFER.pCode -> R.drawable.ic_image_light_blue_background
                TransactionProductCode.CARD_REORDER.pCode -> R.drawable.ic_image_light_red_background
                //TransactionProductCode.POS_PURCHASE.pCode, TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode,TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode, TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode, TransactionProductCode.FUND_LOAD.pCode, TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.FUND_WITHDRAWL.pCode, TransactionProductCode.ATM_DEPOSIT.pCode, TransactionProductCode.ECOM.pCode -> R.drawable.ic_image_light_blue_background
                TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode, TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode,
                TransactionProductCode.FUND_LOAD.pCode, TransactionProductCode.FUND_WITHDRAWL.pCode -> R.drawable.ic_image_light_blue_background
                TransactionProductCode.ECOM.pCode, TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode, TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.POS_PURCHASE.pCode, TransactionProductCode.ATM_DEPOSIT.pCode -> R.drawable.image_map
                else -> R.drawable.ic_image_light_blue_background
            })
        } ?: return -1
    }

    fun getLocation(): String? {
        return when (transaction.productCode) {
            TransactionProductCode.FUND_LOAD.pCode -> transaction.otherBankName ?: ""
            else -> transaction.cardAcceptorLocation ?: ""
        }
    }

    fun getTotalAmount(): Double = getCalculatedTotalAmount(transaction)

    fun getStatusType(): String {
        return when {
            transaction.isTransactionRejected() -> "Transfer Rejected"
            transaction.isTransactionInProgress() -> "Transfer Pending"
            TransactionProductCode.Y2Y_TRANSFER.pCode == transaction.productCode -> "YTY transfer"
            else -> transaction.getTransferType()
        }
    }

    fun getTransferCategoryIcon(): Int {
        transaction?.let { transaction ->

            if (transaction.getProductType() == TransactionProductType.IS_TRANSACTION_FEE) {
                return R.drawable.ic_expense
            }
            return (when (transaction.productCode) {
                TransactionProductCode.Y2Y_TRANSFER.pCode -> R.drawable.ic_send_money
                TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode, TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode -> 0
                TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.CASH_PAYOUT.pCode -> {
                    R.drawable.ic_send_money
                }
                TransactionProductCode.CARD_REORDER.pCode -> R.drawable.ic_expense
                TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode, TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode, TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode, TransactionProductCode.INWARD_REMITTANCE.pCode, TransactionProductCode.LOCAL_INWARD_TRANSFER.pCode, TransactionProductCode.TOP_UP_VIA_CARD.pCode, TransactionProductCode.FUND_LOAD.pCode, TransactionProductCode.ATM_DEPOSIT.pCode -> {
                    R.drawable.ic_cash
                }
                TransactionProductCode.POS_PURCHASE.pCode -> if (transaction.merchantCategoryName.getMerchantCategoryIcon() == -1) R.drawable.ic_other_outgoing else transaction.merchantCategoryName.getMerchantCategoryIcon()

                else -> 0
            })
        }
    }

    fun getTransferCategoryTitle(): String {
        transaction?.let {
            transaction.productCode?.let { productCode ->
                if (TransactionProductType.IS_TRANSACTION_FEE == transaction.getProductType()) {
                    return "Fee"
                }
                return (when (productCode) {
                    TransactionProductCode.Y2Y_TRANSFER.pCode -> if (transaction.txnType == TxnType.DEBIT.type) "Outgoing Transfer" else "Incoming Transfer"
                    TransactionProductCode.TOP_UP_VIA_CARD.pCode, TransactionProductCode.CASH_DEPOSIT_AT_RAK.pCode, TransactionProductCode.CHEQUE_DEPOSIT_AT_RAK.pCode, TransactionProductCode.INWARD_REMITTANCE.pCode, TransactionProductCode.LOCAL_INWARD_TRANSFER.pCode -> "Incoming Transfer"
                    TransactionProductCode.TOP_UP_SUPPLEMENTARY_CARD.pCode, TransactionProductCode.WITHDRAW_SUPPLEMENTARY_CARD.pCode -> ""
                    TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode, TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.CASH_PAYOUT.pCode -> {
                        "Outgoing Transfer"
                    }
                    TransactionProductCode.CARD_REORDER.pCode -> "Fee"
                    TransactionProductCode.FUND_LOAD.pCode -> "Incoming Funds"
                    //TransactionProductCode.POS_PURCHASE.pCode -> transaction.merchantCategoryName ?: ""
                    TransactionProductCode.ATM_DEPOSIT.pCode -> "Cash deposit"
                    TransactionProductCode.ATM_WITHDRAWL.pCode, TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode -> {
                        if (transaction.category.equals(
                                "REVERSAL",
                                true
                            )
                        ) "Reversal" else "Cash withdraw"
                    }
                    else -> ""
                })
            } ?: return ""
        }
    }

    fun getNote(): String {
        return when (transaction.txnType) {
            TxnType.DEBIT.type -> transaction.transactionNote.decodeToUTF8()
            else -> {
                transaction.receiverTransactionNote.decodeToUTF8()
            }
        }
    }

    fun getTransactionNoteDate(): String {
        return when {
            transaction.getTransactionNoteDate(DateUtils.FORMAT_LONG_OUTPUT).isEmpty() -> {
                "Note added " + if (transaction.txnType == TxnType.DEBIT.type) transaction.transactionNoteDate else transaction.receiverTransactionNoteDate
            }
            else -> {
                "Note added " + transaction
                    .getTransactionNoteDate(DateUtils.FORMAT_LONG_OUTPUT)
            }
        }
    }

    fun getForeignAmount(): Double {
        return when {
            transaction.productCode == TransactionProductCode.RMT.pCode || transaction.productCode == TransactionProductCode.SWIFT.pCode || transaction.isNonAEDTransaction()
            -> {
                transaction.amount ?: 0.00
            }
            else -> 0.00
        }
    }

    fun isTotalPurchaseAvailable(): Boolean {
        return (transaction.productCode == TransactionProductCode.Y2Y_TRANSFER.pCode) ||
                (transaction.productCode == TransactionProductCode.UAEFTS.pCode && transaction.txnType == TxnType.DEBIT.type && !isTransactionNotCompleted()) ||
                (transaction.productCode == TransactionProductCode.SWIFT.pCode && transaction.txnType == TxnType.DEBIT.type && !isTransactionNotCompleted()) ||
                (transaction.productCode == TransactionProductCode.RMT.pCode && transaction.txnType == TxnType.DEBIT.type) ||
                (transaction.productCode == TransactionProductCode.DOMESTIC.pCode && transaction.txnType == TxnType.DEBIT.type) ||
                (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode) ||
                (transaction.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode) ||
                (transaction.productCode == TransactionProductCode.ECOM.pCode)
    }

    fun isTransactionNotCompleted(): Boolean =
        transaction.isTransactionInProgress() || transaction.isTransactionRejected()


    fun isShowReceiptSection(): Boolean =
        (transaction.productCode == TransactionProductCode.ATM_DEPOSIT.pCode) ||
                (transaction.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode) ||
                (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode)


    fun isAtmTransaction(): Boolean =
        (transaction.purposeCode == TransactionProductCode.ATM_DEPOSIT.pCode) || (transaction.purposeCode == TransactionProductCode.ATM_WITHDRAWL.pCode)

    fun isYTYTransaction(): Boolean =
        transaction.productCode == TransactionProductCode.Y2Y_TRANSFER.pCode

    fun showTransactionCategory(): Boolean =
        (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode || transaction.productCode == TransactionProductCode.ECOM.pCode)

    fun isCategoryGeneral(): Boolean =
        (transaction.productCode == TransactionProductCode.ECOM.pCode || transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode)
                && transaction.tapixCategory == null || transaction.tapixCategory?.isGeneral == true

    fun getCategoryDescription(): String {
        return when {
            transaction.tapixCategory == null || isCategoryGeneral() -> {
                "Check back later to see the category updated "
            }
            else -> {
                "Tap to change category"
            }
        }
    }


    fun showFeedbackOption(): Boolean =
        (transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode) ||
                (transaction.productCode == TransactionProductCode.ECOM.pCode) ||
                (transaction.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode) ||
                (transaction.productCode == TransactionProductCode.ATM_DEPOSIT.pCode)


    fun getTapixCategory(): TapixCategory? = if (isCategoryGeneral()) TapixCategory(
        id = 0,
        categoryName = "General",
        categoryIcon = "",
        analyticIcon = ""
    ) else transaction.tapixCategory

    fun isDeclinedTransaction(): Boolean = transaction.category.equals(
        "DECLINE_FEE",
        true
    )

    fun isMApVisible(): Boolean? = transaction.latitude?.let { lat ->
        transaction.longitude?.let { long ->
            (lat != 0.0 && long != 0.0) &&
                    (transaction.productCode == TransactionProductCode.ECOM.pCode ||
                            transaction.productCode == TransactionProductCode.MASTER_CARD_ATM_WITHDRAWAL.pCode ||
                            transaction.productCode == TransactionProductCode.ATM_WITHDRAWL.pCode ||
                            transaction.productCode == TransactionProductCode.POS_PURCHASE.pCode ||
                            transaction.productCode == TransactionProductCode.ATM_DEPOSIT.pCode)
        } ?: false
    } ?: false
}