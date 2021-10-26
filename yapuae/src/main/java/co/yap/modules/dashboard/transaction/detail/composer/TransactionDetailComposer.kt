package co.yap.modules.dashboard.transaction.detail.composer

import co.yap.modules.dashboard.transaction.detail.models.ItemTransactionDetail
import co.yap.modules.dashboard.transaction.detail.models.TransactionDetail
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.yapcore.enums.TransactionDetailItem
import co.yap.yapcore.helpers.extentions.getTitle

interface TransactionDetailItemsComposer {
    fun compose(transaction: Transaction?): TransactionDetail?
}

class TransactionDetailComposer : TransactionDetailItemsComposer {
    private var transactionDetailFactory: TransactionDetailFactory? = null

    override fun compose(transaction: Transaction?): TransactionDetail? {
        transaction?.let {
            transactionDetailFactory = TransactionDetailFactory(transaction)
            return TransactionDetail(
                transactionTitle = transaction.getTitle(),
                noteValue = transactionDetailFactory?.getNote(),
                noteAddedDate = transactionDetailFactory?.getTransactionNoteDate(),
                categoryTitle = transactionDetailFactory?.getTransferCategoryTitle(),
                categoryIcon = transactionDetailFactory?.getTransferCategoryIcon(),
                totalAmount = transactionDetailFactory?.getTotalAmount(),
                locationValue = transactionDetailFactory?.getLocation(),
                transferType = transactionDetailFactory?.getStatusType(),
                statusIcon = transactionDetailFactory?.getTransactionStatusIcon(),
                coverImage = transactionDetailFactory?.getMapImage() ?: -1,
                transactionItem = listTransactionItem(),
                showTotalPurchase = transactionDetailFactory?.isTotalPurchaseAvailable(),
                showError = transactionDetailFactory?.isTransactionNotCompleted(),
                showReceipts = transactionDetailFactory?.isShowReceiptSection(),
                isAtmTransaction = transactionDetailFactory?.isAtmTransaction(),
                showCategory = transactionDetailFactory?.showTransactionCategory(),
                categoryType = transactionDetailFactory?.isCategoryGeneral(),
                categoryDescription = transactionDetailFactory?.getCategoryDescription(),
                tapixCategory = transactionDetailFactory?.getTapixCategory(),
                showFeedBack = transactionDetailFactory?.showFeedbackOption(),
                isYTYTransfer = transactionDetailFactory?.isYTYTransaction(),
                isDeclinedTransaction = transactionDetailFactory?.isDeclinedTransaction(),
                isMApVisible = transactionDetailFactory?.isMApVisible()
            )
        } ?: return null
    }


    private fun listTransactionItem(): List<ItemTransactionDetail> {
        return transactionDetailItemList().filter {
            it.visibility == true
        }
    }

    private fun transactionDetailItemList(): MutableList<ItemTransactionDetail> {
        return arrayListOf(
            makeTransactionDetailItem(TransactionDetailItem.CARD_NUMBER),
            makeTransactionDetailItem(TransactionDetailItem.TRANSFER_AMOUNT),
            makeTransactionDetailItem(TransactionDetailItem.EXCHANGE_RATE),
            makeTransactionDetailItem(TransactionDetailItem.SENDER),
            makeTransactionDetailItem(TransactionDetailItem.RECEIVER),
            makeTransactionDetailItem(TransactionDetailItem.SENT_RECEIVED),
            makeTransactionDetailItem(TransactionDetailItem.FEES),
            makeTransactionDetailItem(TransactionDetailItem.VAT),
            makeTransactionDetailItem(TransactionDetailItem.TOTAL),
            makeTransactionDetailItem(TransactionDetailItem.REFERENCE_NUMBER),
            makeTransactionDetailItem(TransactionDetailItem.REMARKS)
        )
    }

    private fun makeTransactionDetailItem(
        tag: TransactionDetailItem
    ): ItemTransactionDetail {
        return ItemTransactionDetail(
            label = transactionDetailFactory?.label(tag),
            value = transactionDetailFactory?.value(tag),
            visibility = transactionDetailFactory?.isShowItem(tag)
        )
    }
}