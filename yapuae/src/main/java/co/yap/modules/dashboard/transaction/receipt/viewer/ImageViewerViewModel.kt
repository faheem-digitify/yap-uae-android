package co.yap.modules.dashboard.transaction.receipt.viewer

import android.app.Application
import co.yap.modules.dashboard.transaction.receipt.viewer.adapter.ReceiptViewerAdapter
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent

class ImageViewerViewModel(application: Application) :
    BaseViewModel<IImageViewer.State>(application),
    IImageViewer.ViewModel {

    private val transactionsRepository: TransactionsRepository = TransactionsRepository
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override var transactionId: String = ""
    override var receiptId: String = ""
    override var isReceiptDeleted: Boolean = false
    override val imagesViewerAdapter: ReceiptViewerAdapter =
        ReceiptViewerAdapter(
            arrayListOf()
        )
    override val state: ImageViewerState =
        ImageViewerState()

    override fun handlePressOnView(id: Int) {
        clickEvent.postValue(id)
    }

    override fun deleteReceipt(success: () -> Unit) {
        launch {
            state.loading = true
            when (val response =
                transactionsRepository.deleteTransactionReceipt(transactionId, receiptId)) {
                is RetroApiResponse.Success -> {
                    isReceiptDeleted = true
                    state.loading = false
                    success()
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    showToast(response.error.message)
                }
            }
        }
    }

}