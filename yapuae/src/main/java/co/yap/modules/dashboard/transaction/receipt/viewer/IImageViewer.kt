package co.yap.modules.dashboard.transaction.receipt.viewer

import androidx.databinding.ObservableField
import co.yap.modules.dashboard.transaction.receipt.viewer.adapter.ReceiptViewerAdapter
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface IImageViewer {
    interface State : IBase.State {
        var imageUrl: ObservableField<String>?
        var imageReceiptTitle: ObservableField<String>?
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var transactionId: String
        var receiptId: String
        val imagesViewerAdapter: ReceiptViewerAdapter
        val isReceiptDeleted: Boolean
        fun handlePressOnView(id: Int)
        fun deleteReceipt(success: () -> Unit)
    }

    interface View : IBase.View<ViewModel> {
        val shareReceiptImageName: String get() = "YAP-Image"
        val shareReceiptTitle: String get() = "YAP Image"
    }
}