package co.yap.modules.dashboard.transaction.detail

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import co.yap.yapuae.R
import co.yap.modules.dashboard.transaction.detail.adaptor.TransactionDetailItemAdapter
import co.yap.modules.dashboard.transaction.detail.composer.TransactionDetailComposer
import co.yap.modules.dashboard.transaction.receipt.adapter.TransactionReceiptAdapter
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.requestdtos.TotalPurchaseRequest
import co.yap.networking.transactions.responsedtos.ReceiptModel
import co.yap.networking.transactions.responsedtos.TotalPurchases
import co.yap.networking.transactions.responsedtos.TotalPurchasesResponse
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.networking.transactions.responsedtos.transactionreciept.TransactionReceiptResponse
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.widgets.CoreCircularImageView
import co.yap.widgets.bottomsheet.BottomSheetItem
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.enums.TransactionProductCode
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.DateUtils.FORMAT_LONG_OUTPUT
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.managers.SessionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.*


class TransactionDetailsViewModel(application: Application) :
    BaseViewModel<ITransactionDetails.State>(application), ITransactionDetails.ViewModel {
    val repository: TransactionsRepository = TransactionsRepository
    override val state: TransactionDetailsState =
        TransactionDetailsState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var transaction: ObservableField<Transaction> = ObservableField()
    override var adapter: TransactionReceiptAdapter =
        TransactionReceiptAdapter(
            mutableListOf()
        )
    override var transactionAdapter: TransactionDetailItemAdapter =
        TransactionDetailItemAdapter(
            arrayListOf()
        )
    override var totalPurchase: ObservableField<TotalPurchases> = ObservableField()
    override var responseReciept: MutableLiveData<ArrayList<String>> = MutableLiveData()
    override var itemsComposer: TransactionDetailComposer = TransactionDetailComposer()
    override var gMap: GoogleMap? = null
        set(value) {
            field = value
            gMap?.setOnMapLoadedCallback {
                gMap?.run {
                    uiSettings.isZoomGesturesEnabled = false
                    uiSettings.isScrollGesturesEnabled = false
                    uiSettings.isMapToolbarEnabled = false
                    uiSettings.isCompassEnabled = false
                    uiSettings.isRotateGesturesEnabled = false
                }
            }
        }

    override fun onCreate() {
        super.onCreate()
        setStatesData()
    }

    override fun composeTransactionDetail(transaction: Transaction) {
        state.transactionData.set(itemsComposer.compose(transaction))
        state.transactionData.get()?.let {
            transactionAdapter.setList(it.transactionItem)
            state.txnNoteValue.set(it.noteValue)
            state.transactionNoteDate = it.noteAddedDate
            state.coverImage.set(it.coverImage)
            it.showTotalPurchase?.let { it1 -> state.showTotalPurchases.set(it1) }
            it.showError?.let { bool -> state.showErrorMessage.set(bool) }
            state.receiptVisibility.set(it.showReceipts ?: false)
            state.categoryDescription.set(it.categoryDescription)
            state.updatedCategory.set(it.tapixCategory)
        }
    }

    override fun setMerchantImage(view: CoreCircularImageView) {
        transaction.get()?.let { txns ->
            if (txns.productCode != TransactionProductCode.ATM_DEPOSIT.pCode && txns.productCode != TransactionProductCode.ATM_WITHDRAWL.pCode) {
                txns.merchantLogo?.let { logo ->
                    view.loadImage(logo)
                    if (txns.productCode == TransactionProductCode.ECOM.pCode || txns.productCode == TransactionProductCode.POS_PURCHASE.pCode)
                        view.setBackgroundColor(context.getColor(R.color.white))
                } ?: txns.setTransactionImage(view)
            } else {
                txns.setTransactionImage(view)
            }
        }
    }

    override fun getTotalPurchaseRequest(): TotalPurchaseRequest {
        transaction.get()?.let { data ->
            return when (data.productCode) {
                TransactionProductCode.Y2Y_TRANSFER.pCode -> {
                    if (data.txnType == TxnType.DEBIT.type) TotalPurchaseRequest(
                        txnType = data.txnType
                            ?: "",
                        productCode = data.productCode ?: "",
                        receiverCustomerId = data.customerId2 ?: ""
                    )
                    else
                        TotalPurchaseRequest(
                            txnType = data.txnType ?: "",
                            productCode = data.productCode ?: "",
                            senderCustomerId = data.customerId2 ?: ""
                        )
                }
                TransactionProductCode.SWIFT.pCode, TransactionProductCode.RMT.pCode, TransactionProductCode.UAEFTS.pCode, TransactionProductCode.DOMESTIC.pCode -> {
                    TotalPurchaseRequest(
                        txnType = data.txnType ?: "",
                        productCode = data.productCode ?: "",
                        beneficiaryId = data.beneficiaryId ?: ""
                    )
                }
                TransactionProductCode.ECOM.pCode, TransactionProductCode.POS_PURCHASE.pCode -> {
                    TotalPurchaseRequest(
                        txnType = data.txnType ?: "",
                        productCode = data.productCode ?: "", merchantName = data.merchantName
                    )
                }
                else -> TotalPurchaseRequest(
                    txnType = data.txnType ?: "",
                    productCode = data.productCode ?: ""
                )
            }

        }
        return TotalPurchaseRequest(
            txnType = transaction.get()?.txnType ?: "",
            productCode = transaction.get()?.productCode ?: ""
        )
    }

    override fun requestAllApis() {
        requestTransactionDetails { totalPurchasesResponse, receiptResponse ->
            launch(Dispatcher.Main) {
                when (totalPurchasesResponse) {
                    is RetroApiResponse.Success -> {
                        totalPurchasesResponse.data.data?.let { totalPurchases ->
                            totalPurchase.set(totalPurchases)
                        }
                    }
                    is RetroApiResponse.Error -> {
                        state.toast =
                            "${totalPurchasesResponse.error.message}^${AlertType.DIALOG.name}"
                    }
                }
                when (receiptResponse) {
                    is RetroApiResponse.Success -> {
                        responseReciept.value =
                            receiptResponse.data.trxnReceiptList as ArrayList<String>?
                    }
                    is RetroApiResponse.Error -> {
                        state.toast = receiptResponse.error.message
                    }
                }
                state.viewState.value = false
            }
        }

    }

    private fun setStatesData() {
        transaction.get()?.let { transaction ->
            requestAllApis()
            setTransactionNoteDate()
            state.toolbarTitle = transaction.getFormattedTime(FORMAT_LONG_OUTPUT)
        }
        state.isTransactionInProcessOrRejected.set(
            transaction.get()
                .isTransactionRejected() || transaction.get().isTransactionInProgress()
        )

    }

    private fun requestTransactionDetails(responses: (RetroApiResponse<TotalPurchasesResponse>?, RetroApiResponse<TransactionReceiptResponse>?) -> Unit) {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
            val totalPurchaseResponse = state.showTotalPurchases.get().let { showView ->
                if (showView) {
                    return@let launchAsync {
                        repository.getTotalPurchases(
                            getTotalPurchaseRequest()
                        )
                    }

                } else return@let null
            }

            val receiptsResponse = transaction.get()?.let { it ->
                if (state.receiptVisibility.get()) {
                    return@let launchAsync {
                        repository.getAllTransactionReceipts(
                            transactionId = it.transactionId ?: ""
                        )
                    }
                } else return@let null
            }
            responses(totalPurchaseResponse?.await(), receiptsResponse?.await())
        }
    }

    override fun getReceiptTitle(list: List<ReceiptModel>): String {
        return when {
            list.isNullOrEmpty() -> getString(Strings.screen_transaction_details_receipt_label)
            list.size == 1 ->
                getString(Strings.screen_transaction_details_single_added_receipt_label).format(
                    adapter.getDataList().size
                )
            list.size > 1 -> getString(Strings.screen_transaction_details_added_receipt_label).format(
                adapter.getDataList().size
            )
            else -> getString(Strings.screen_transaction_details_receipt_label)
        }

    }

    override fun getReceiptItems(receiptLis: List<String>): List<ReceiptModel> {
        val list: MutableList<ReceiptModel> = arrayListOf()
        for (i in 0..receiptLis.size.minus(1)) {
            list.add(ReceiptModel(receiptItemName(i), receiptLis[i]))
        }
        return list
    }

    override fun receiptItemName(index: Int): String {
        return "Receipt ${index.plus(1)}"
    }

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun deleteReceipt(position: Int) {
        adapter.removeItemAt(position)
    }

    override fun getAddReceiptOptions(): ArrayList<BottomSheetItem> {
        val list = arrayListOf<BottomSheetItem>()
        list.add(
            BottomSheetItem(
                icon = R.drawable.ic_camera,
                title = getString(Strings.screen_update_profile_photo_display_text_open_camera),
                tag = PhotoSelectionType.CAMERA.name
            )
        )
        list.add(
            BottomSheetItem(
                icon = R.drawable.ic_folder,
                title = getString(Strings.screen_transaction_details_display_sheet_text_upload_from_files),
                tag = PhotoSelectionType.GALLERY.name
            )
        )
        return list
    }

    override fun setAdapterList(receiptLis: List<String>) {
        adapter.setList(getReceiptItems(receiptLis))
        state.receiptTitle.set(getReceiptTitle(adapter.getDataList()))
    }

    private fun setTransactionNoteDate() {
        if (transaction.get().getTransactionNoteDate(FORMAT_LONG_OUTPUT).isEmpty()) {
            state.transactionNoteDate =
                "Note added " + if (transaction.get()?.txnType == TxnType.DEBIT.type) transaction.get()?.transactionNoteDate else transaction.get()?.receiverTransactionNoteDate

        } else {
            state.transactionNoteDate = "Note added " + transaction.get()
                .getTransactionNoteDate(FORMAT_LONG_OUTPUT)
        }
    }

    override fun setContentDataColor(
        transaction: Transaction?,
        tvTotalAmountValue: AppCompatTextView,
        tvCurrency: AppCompatTextView
    ) {
        transaction?.let {
            state.transactionData.get()?.isDeclinedTransaction?.let { isDeclinedTransaction ->
                if (isDeclinedTransaction || transaction.isTransactionRejected()) {
                    tvTotalAmountValue.text =
                        Translator.getString(context, R.string.screen_transaction_details_declined)
                    tvTotalAmountValue.setTextColor(
                        context.resources.getColor(co.yap.yapcore.R.color.colorSecondaryMagenta)
                    )
                    tvCurrency.visibility = View.INVISIBLE
                } else {
                    tvTotalAmountValue.text =
                        if (transaction.txnType.equals(TxnType.DEBIT.type))
                        //"- ${state.transactionData.get()?.totalAmount.toString()
                            "- ${transaction.cardHolderBillingTotalAmount.toString()
                                .toFormattedCurrency(
                                    false,
                                    SessionManager.getDefaultCurrency(),
                                    true
                                )}"
                        //else "+ ${state.transactionData.get()?.totalAmount.toString()
                        else "+ ${transaction.cardHolderBillingTotalAmount.toString()
                            .toFormattedCurrency(false, SessionManager.getDefaultCurrency(), true)}"
                }
            }
        }
    }

    override fun setMap() {
        val location = transaction.get()?.latitude?.let { lat ->
            transaction.get()?.longitude?.let { long ->
                LatLng(lat, long)
            }
        }
        gMap?.addMarker(
            location?.let {
                MarkerOptions()
                    .position(it)
                    .title("")
                    .icon(bitmapDescriptorFromVector(getApplication(), R.drawable.ic_location_pin))
            }
        )
        val cameraPosition: CameraPosition = CameraPosition.Builder()
            .target(location)
            .zoom(10f).build()
        gMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition)
        )
    }

    override fun setMapVisibility(ivMap: ImageView, map: Fragment, isShowMap: Boolean) {
        if (isShowMap) {
            ivMap.visibility = View.GONE
            map.view?.visibility = View.VISIBLE
        } else {
            ivMap.visibility = View.VISIBLE
            map.view?.visibility = View.GONE
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}