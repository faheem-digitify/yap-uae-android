package co.yap.modules.dashboard.transaction.receipt.previewer

import android.net.Uri
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import co.yap.yapuae.BR
import co.yap.yapcore.BaseState

class PreviewTransactionReceiptState : BaseState(), IPreviewTransactionReceipt.State {
    @get:Bindable
    override var filePath: Uri? = Uri.EMPTY
        set(value) {
            field = value
            notifyPropertyChanged(BR.filePath)
        }
    override val showRedo: ObservableBoolean = ObservableBoolean()
}
