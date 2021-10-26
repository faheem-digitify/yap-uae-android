package co.yap.networking.transactions

import co.yap.networking.BaseRepository
import co.yap.networking.RetroNetwork
import co.yap.networking.models.ApiResponse
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.requestdtos.*
import co.yap.networking.transactions.responsedtos.*
import co.yap.networking.transactions.responsedtos.achievement.AchievementsResponseDTO
import co.yap.networking.transactions.responsedtos.purposepayment.PaymentPurposeResponseDTO
import co.yap.networking.transactions.responsedtos.topuptransactionsession.Check3DEnrollmentSessionResponse
import co.yap.networking.transactions.responsedtos.topuptransactionsession.CreateTransactionSessionResponseDTO
import co.yap.networking.transactions.responsedtos.transaction.*
import co.yap.networking.transactions.responsedtos.transactionreciept.TransactionReceiptResponse
import okhttp3.MultipartBody
import retrofit2.http.Body

object TransactionsRepository : BaseRepository(), TransactionsApi {

    const val URL_ADD_FUNDS = "/transactions/api/top-up"
    const val URL_REMOVE_FUNDS = "/transactions/api/withdraw"
    const val URL_FUND_TRANSFER_LIMITS = "/transactions/api/product/{product-code}/limits"
    const val URL_FUND_TRANSFER_DENOMINATIONS =
        "/transactions/api/product/{product-code}/denominations"
    const val URL_GET_CARD_FEE = "/transactions/api/fees/spare-card/subscription/{card-type}"
    const val URL_GET_DEBIT_CARD_FEE =
        "/transactions/api/fees/reorder/debit-card/subscription/physical"
    const val URL_GET_CARD_STATEMENTS = "/transactions/api/card-statements"
    const val URL_GET_ACCOUNT_STATEMENTS = "/transactions/api/account-statements"
    const val URL_Y2Y_FUNDS_TRANSFER = "/transactions/api/y2y"
    const val URL_ADD_EDIT_NOTE = "/transactions/api/transaction-note"
    const val URL_SEARCH_FILTER_AMOUNT = "/transactions/api/transactions/search-filter/amount"
    const val URL_GET_TRANSACTION_DETAILS =
        "/transactions/api/transaction/transactionId/{transactionId}"
    const val URL_GET_ACCOUNT_TRANSACTIONS =
        "/transactions/api/account-transactions/{number}/{size}/"
    const val URL_GET_CARD_TRANSACTIONS =
        "/transactions/api/cards-transactions/{number}/{size}/"
    const val URL_GET_FEE = "/transactions/api/fee"
    const val URL_CREATE_TRANSACTION_SESSION =
        "/transactions/api/mastercard/create-checkout-session"
    const val URL_CHECK_3Ds_ENROLLMENT_SESSION = "/transactions/api/mastercard/check-3ds-enrollment"
    const val URL_TOP_UP_TRANSACTION = "/transactions/api/mastercard/order-id/{order-id}"
    const val URL_SECURE_ID_POOLING =
        "/transactions/api/mastercard/retrieve-acs-results/3DSecureId/{secureId}"
    const val URL_GET_ANALYTICS_BY_MERCHANT_NAME =
        "/transactions/api/transaction/card/analytics-merchant-name"
    const val URL_GET_ANALYTICS_BY_CATEGORY_NAME =
        "/transactions/api/transaction/card/analytics-merchant-category"
    const val URL_GET_TRANSACTION_FEE_WITH_PRODUCT_CODE =
        "/transactions/api/product-codes/{product-code}/fees"

    const val URL_GET_INTERNATIONAL_TRANSACTION_REASON_LIST =
        "/transactions/api/product-codes/{product-code}/purpose-reasons"

    const val URL_GET_INTERNATIONAL_RX_RATE_LIST =
        "transactions/api/product-codes/{product-code}/fxRate"
    const val URL_CASH_PAYOUT_TRANSFER =
        "/transactions/api/cashpayout"
    const val URL_DOMESTIC_TRANSFER =
        "/transactions/api/yap-to-rak"
    const val URL_UAEFTS_TRANSFER =
        "/transactions/api/uaefts"
    const val URL_RMT_TRANSFER =
        "/transactions/api/rmt"
    const val URL_SWIFT_TRANSFER =
        "/transactions/api/swift"
    const val URL_GET_TRANSACTION_THRESHOLDS =
        "/transactions/api/transaction-thresholds"
    const val URL_GET_CUTT_OFF_TIME_CONFIGURATION =
        "/transactions/api/cut-off-time-configuration"
    const val URL_GET_ACHIEVEMENTS = "/transactions/api/yap-achievements"
    const val URL_GET_PURPOSE_OF_PAYMENT = "/transactions/api/purpose-of-payments/{product-code}"
    const val URL_CHECK_COOLING_PERIOD = "/transactions/api/check-cooling-period-limit"

    const val URL_GET_MERCHANT_TRANSACTIONS = "/transactions/api/transaction-search/{merchant-type}"
    const val URL_GET_TRANSACTION_DETAILS_FOR_LEANPLUM =
        "/transactions/api/lean-plum/transaction-states"
    const val URL_TRANSACTIONS_RECEIPT = "/transactions/api/transaction-receipt/transaction-id"
    const val URL_TRANSACTIONS_RECEIPT_SAVE = "/transactions/api/transaction-receipt"
    const val URL_TRANSACTIONS_RECEIPT_DELETE = "/transactions/api/transaction-receipt"
    const val URL_TRANSACTIONS_TOTAL_PURCHASES = "/transactions/api/total-purchases"
    const val URL_TRANSACTIONS_VIEW_CATEGORIES = "/transactions/api/category"
    const val URL_TRANSACTIONS_UPDATE_CATEGORY =
        "/transactions/api/category/update-transaction-category"
    const val URL_SEND_EMAIL =
        "/transactions/api/email-me"

    // Household
    const val URL_HOUSEHOLD_CARD_FEE_PACKAGE = "/transactions/api/fees/subscriptions/{pkg-type}"

    private val api: TransactionsRetroService =
        RetroNetwork.createService(TransactionsRetroService::class.java)

    override suspend fun addFunds(addFundsRequest: AddFundsRequest): RetroApiResponse<AddRemoveFundsResponse> =
        executeSafely(call = { api.addFunds(addFundsRequest) })

    override suspend fun removeFunds(removeFundsResponse: RemoveFundsRequest): RetroApiResponse<AddRemoveFundsResponse> =
        executeSafely(call = { api.removeFunds(removeFundsResponse) })

    override suspend fun getFundTransferLimits(productCode: String?): RetroApiResponse<FundTransferLimitsResponse> =
        executeSafely(call = { api.getFundTransferLimits(productCode) })

    override suspend fun getFundTransferDenominations(productCode: String): RetroApiResponse<FundTransferDenominationsResponse> =
        executeSafely(call = { api.getFundTransferDenominations(productCode) })

    override suspend fun getCardFee(cardType: String): RetroApiResponse<RemittanceFeeResponse> =
        executeSafely(call = { api.getCardFee(cardType) })

    override suspend fun getDebitCardFee(): RetroApiResponse<RemittanceFeeResponse> =
        executeSafely(call = { api.getDebitCardFee() })

    override suspend fun getTransactionFeeWithProductCode(
        productCode: String?,
        mRemittanceFeeRequest: RemittanceFeeRequest?
    ): RetroApiResponse<RemittanceFeeResponse> =
        executeSafely(call = {
            api.getTransactionFeeWithProductCode(
                productCode,
                mRemittanceFeeRequest
            )
        })

    override suspend fun getTransactionInternationalReasonList(productCode: String?): RetroApiResponse<InternationalFundsTransferReasonList> =
        executeSafely(call = { api.getInternationalTransactionReasonList(productCode) })

    override suspend fun getTransactionInternationalRXList(
        productCode: String?,
        mRxListRequest: RxListRequest
    ): RetroApiResponse<FxRateResponse> =
        executeSafely(call = { api.getInternationalRXRateList(productCode, mRxListRequest) })

    override suspend fun getCardStatements(cardSerialNumber: String?): RetroApiResponse<CardStatementsResponse> =
        executeSafely(call = { api.getCardStatements(cardSerialNumber) })

    override suspend fun getAccountStatements(): RetroApiResponse<CardStatementsResponse> =
        executeSafely(call = { api.getAccountStatements() })

    override suspend fun y2yFundsTransferRequest(y2YFundsTransferRequest: Y2YFundsTransferRequest): RetroApiResponse<ApiResponse> =
        executeSafely(call = { api.y2yFundsTransferRequest(y2YFundsTransferRequest) })

    override suspend fun addEditNote(addEditNoteRequest: AddEditNoteRequest): RetroApiResponse<AddEditNoteResponse> =
        executeSafely(call = { api.addEditNote(addEditNoteRequest) })

    override suspend fun getAccountTransactions(homeTransactionsRequest: HomeTransactionsRequest): RetroApiResponse<HomeTransactionsResponse> =
        executeSafely(call = {
            api.getAccountTransactions(
                homeTransactionsRequest.number,
                homeTransactionsRequest.size,
                homeTransactionsRequest.amountStartRange,
                homeTransactionsRequest.amountEndRange,
                homeTransactionsRequest.txnType,
                homeTransactionsRequest.title,
                homeTransactionsRequest.categories,
                homeTransactionsRequest.statues,
                homeTransactionsRequest.cardDetailsRequired
            )
        })

    override suspend fun searchTransactions(homeTransactionsRequest: HomeTransactionsRequest?): RetroApiResponse<HomeTransactionsResponse> {
        return executeSafely(call = {
            api.searchTransactions(
                homeTransactionsRequest?.number,
                homeTransactionsRequest?.size,
                homeTransactionsRequest?.searchField,
                homeTransactionsRequest?.cardDetailsRequired ?: true
            )
        })
    }

    override suspend fun getSearchFilterAmount(): RetroApiResponse<SearchFilterAmountResponse> =
        executeSafely(call = { api.getSearchFilterAmount() })

    override suspend fun getTransactionDetails(transactionId: String?): RetroApiResponse<TransactionDetailsResponse> =
        executeSafely(call = { api.getTransactionDetails(transactionId) })

    override suspend fun getCardTransactions(cardTransactionRequest: CardTransactionRequest): RetroApiResponse<HomeTransactionsResponse> =
        executeSafely(call = {
            api.getCardTransactions(
                cardTransactionRequest.number,
                cardTransactionRequest.size,
                cardTransactionRequest.serialNumber,
                cardTransactionRequest.amountStartRange,
                cardTransactionRequest.amountEndRange,
                cardTransactionRequest.txnType,
                cardTransactionRequest.title,
                cardTransactionRequest.categories,
                cardTransactionRequest.statues,
                cardTransactionRequest.cardDetailsRequired,
                cardTransactionRequest.debitSearch
            )
        })


    override suspend fun getTransactionFee(productCode: String): RetroApiResponse<TransactionFeeResponseDTO> =
        executeSafely(call = { api.getTransactionFee(productCode) })

    override suspend fun createTransactionSession(createSessionRequest: CreateSessionRequest): RetroApiResponse<CreateTransactionSessionResponseDTO> =
        executeSafely(call = { api.createTransactionSession(createSessionRequest) })

    override suspend fun check3DEnrollmentSession(check3DEnrollmentSessionRequest: Check3DEnrollmentSessionRequest): RetroApiResponse<Check3DEnrollmentSessionResponse> =
        executeSafely(call = { api.check3DEnrollmentSession(check3DEnrollmentSessionRequest) })

    override suspend fun secureIdPooling(
        secureId: String?
    ): RetroApiResponse<StringDataResponseDTO> =
        executeSafely(call = { api.secureIdPooling(secureId) })

    override suspend fun cardTopUpTransactionRequest(
        orderId: String,
        topUpTransactionRequest: TopUpTransactionRequest
    ): RetroApiResponse<ApiResponse> =
        executeSafely(call = { api.cardTopUpTransactionRequest(orderId, topUpTransactionRequest) })

    override suspend fun getAnalyticsByMerchantName(
        date: String?
    ): RetroApiResponse<AnalyticsResponseDTO> =
        executeSafely(call = { api.getAnalyticsByMerchantName(date) })

    override suspend fun getAnalyticsByCategoryName(
        date: String?
    ): RetroApiResponse<AnalyticsResponseDTO> =
        executeSafely(call = { api.getAnalyticsByCategoryName(date) })

    override suspend fun cashPayoutTransferRequest(sendMoneyTransferRequest: SendMoneyTransferRequest): RetroApiResponse<SendMoneyTransactionResponseDTO> =
        executeSafely(call = { api.cashPayoutTransferRequest(sendMoneyTransferRequest) })

    override suspend fun domesticTransferRequest(sendMoneyTransferRequest: SendMoneyTransferRequest): RetroApiResponse<SendMoneyTransactionResponseDTO> =
        executeSafely(call = { api.domesticTransferRequest(sendMoneyTransferRequest) })

    override suspend fun uaeftsTransferRequest(sendMoneyTransferRequest: SendMoneyTransferRequest): RetroApiResponse<SendMoneyTransactionResponseDTO> =
        executeSafely(call = { api.uaeftsTransferRequest(sendMoneyTransferRequest) })

    override suspend fun rmtTransferRequest(sendMoneyTransferRequest: SendMoneyTransferRequest): RetroApiResponse<SendMoneyTransactionResponseDTO> =
        executeSafely(call = { api.rmtTransferRequest(sendMoneyTransferRequest) })

    override suspend fun swiftTransferRequest(sendMoneyTransferRequest: SendMoneyTransferRequest): RetroApiResponse<SendMoneyTransactionResponseDTO> =
        executeSafely(call = { api.swiftTransferRequest(sendMoneyTransferRequest) })

    override suspend fun getHousholdFeePackage(packageType: String): RetroApiResponse<RemittanceFeeResponse> =
        executeSafely(call = { api.getHousholdFeePackage(packageType) })

    override suspend fun getTransactionThresholds(): RetroApiResponse<TransactionThresholdResponseDTO> =
        executeSafely(call = { api.getTransactionThresholds() })

    override suspend fun getAchievements(): RetroApiResponse<AchievementsResponseDTO> =
        executeSafely(call = { api.getAchievements() })

    override suspend fun getCutOffTimeConfiguration(
        productCode: String?,
        currency: String?,
        amount: String?,
        isCbwsi: Boolean?
    ) =
        executeSafely(call = {
            api.getCutOffTimeConfiguration(
                productCode,
                currency,
                amount,
                isCbwsi
            )
        })

    override suspend fun getPurposeOfPayment(productCode: String): RetroApiResponse<PaymentPurposeResponseDTO> =
        executeSafely(call = { api.getPurposeOfPayment(productCode) })

    override suspend fun checkCoolingPeriodRequest(
        beneficiaryId: String?,
        beneficiaryCreationDate: String?,
        beneficiaryName: String?,
        amount: String?
    ): RetroApiResponse<ApiResponse> =
        executeSafely(call = {
            api.checkCoolingPeriodRequest(
                beneficiaryId,
                beneficiaryCreationDate,
                beneficiaryName,
                amount
            )
        })

    override suspend fun getTransactionsOfMerchant(
        merchantType: String,
        cardSerialNo: String?,
        date: String?,
        merchantName: ArrayList<Any>?
    ): RetroApiResponse<AnalyticsDetailResponseDTO> =
        executeSafely(call = {
            api.getTransactionsOfMerchant(
                merchantType,
                cardSerialNo,
                date,
                merchantName
            )
        })

    override suspend fun getTransDetailForLeanplum(): RetroApiResponse<TransactionDataResponseForLeanplum> =
        executeSafely(call = { api.getTransactionDetailForLeanplum() })


    override suspend fun getAllTransactionReceipts(transactionId: String): RetroApiResponse<TransactionReceiptResponse> =
        executeSafely(call = {
            api.getAllTransactionReceipts(transactionId)
        })

    override suspend fun addTransactionReceipt(
        transactionId: String,
        transactionReceipt: MultipartBody.Part
    ): RetroApiResponse<ApiResponse> = executeSafely(call = {
        api.addTransactionReceipt(transactionId, transactionReceipt)
    })

    override suspend fun updateTransactionReceipt(transactionId: String): RetroApiResponse<ApiResponse> =
        executeSafely(call = {
            api.updateTransactionReceipt(transactionId)
        })

    override suspend fun deleteTransactionReceipt(
        transactionId: String,
        receipt: String
    ): RetroApiResponse<ApiResponse> = executeSafely(call = {
        api.deleteTransactionReceipt(receipt, transactionId)
    })

    override suspend fun getTotalPurchases(totalPurchaseRequest: TotalPurchaseRequest): RetroApiResponse<TotalPurchasesResponse> =
        executeSafely(call = {
            api.getTotalPurchases(
                txnType = totalPurchaseRequest.txnType,
                beneficiaryId = totalPurchaseRequest.beneficiaryId,
                receiverCustomerId = totalPurchaseRequest.receiverCustomerId,
                senderCustomerId = totalPurchaseRequest.senderCustomerId,
                productCode = totalPurchaseRequest.productCode,
                merchantName = totalPurchaseRequest.merchantName
            )
        })

    override suspend fun getAllTransactionCategories(): RetroApiResponse<TransactionCategoryResponse> =
        executeSafely(call = {
            api.getAllTransactionCategories()
        })

    override suspend fun updateTransactionCategory(
        categoryId: String,
        transactionId: String
    ): RetroApiResponse<ApiResponse> = executeSafely(call = {
        api.updateTransactionCategory(categoryId, transactionId)
    })

    override suspend fun requestSendEmail(
        sendEmailRequestModel: SendEmailRequest
    ): RetroApiResponse<ApiResponse> =
        executeSafely(call = {
            api.requestSendEmail(sendEmailRequestModel)
        })

}

