package co.yap.modules.dashboard.transaction.receipt.viewer

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class ImageViewerState : BaseState(),
    IImageViewer.State {

    override var imageUrl: ObservableField<String>? = ObservableField()

    override var imageReceiptTitle: ObservableField<String>? = ObservableField()

}