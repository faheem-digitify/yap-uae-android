package co.yap.yapcore.yapcore.helpers.extensions

import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.yapcore.R
import co.yap.yapcore.enums.TransactionProductCode
import co.yap.yapcore.enums.TransactionProductType
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.yapcore.base.BaseTestCase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class TransactionsTests : BaseTestCase() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    data class TransactionTest(
        val transaction: Transaction,
        val expectation: TransactionExpectation
    )

    data class TransactionExpectation(
        val title: String,
        val time: String,
        val transferType: String,
        val remarks: String,
        val status: String,
        val currency: String,
        val amount: Double
    )

    @TestFactory
    fun test_transaction(): Collection<DynamicTest>? {
        val tests: MutableSet<DynamicTest> = LinkedHashSet()
        getTransactions().forEach {
            tests.add(addNewTest(it.transaction, it.expectation))
        }

        return tests
    }

    private fun addNewTest(
        transaction: Transaction,
        expectation: TransactionExpectation
    ): DynamicTest {
        val displayName: String = java.lang.String.format(
            Locale.getDefault(),
            "test_transaction_for_product_code_%s",
            transaction.productCode
        )
        return dynamicTest(displayName) {
            assertEquals(expectation.title, transaction.getTitle())
            assertEquals(expectation.time, transaction.getTransactionTime())
            assertEquals(expectation.transferType, transaction.getTransferType())
            assertEquals(expectation.remarks, transaction.transactionNote)
            assertEquals(expectation.status, transaction.getStatus())
            assertEquals(expectation.amount, transaction.getAmount(), 0.2)
            assertEquals(expectation.currency, transaction.getCurrency())
            assertEquals(getExpectedIcon(transaction), transaction.getIcon())
            assertEquals(getExpectedStatusIcon(transaction), transaction.getStatusIcon())
        }
    }

    private fun getExpectedIcon(transaction: Transaction): Int {
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
    }

    private fun getExpectedStatusIcon(transaction: Transaction): Int {
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
    }

    private fun getTransactions(): List<TransactionTest> {
        val gson = GsonBuilder().create();
        val itemType = object : TypeToken<List<TransactionTest>>() {}.type

        return gson.fromJson<List<TransactionTest>>(readJsonFile(), itemType)
    }

    @Throws(IOException::class)
    private fun readJsonFile(): String? {
        val br =
            BufferedReader(InputStreamReader(FileInputStream("../yapcore/src/main/assets/jsons/transaction.json")))
        val sb = StringBuilder()
        var line: String? = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }
}