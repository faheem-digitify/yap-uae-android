package co.yap.modules.dashboard.transaction.receipt.previewer

import android.net.Uri
import androidx.databinding.ObservableBoolean
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import java.io.File

interface IPreviewTransactionReceipt {
    interface View : IBase.View<ViewModel> {
        fun registerObserver()
        fun unRegisterObserver()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var transactionId: String
        fun handlePressOnView(id: Int)
        fun saveTransactionReceipt(file: File, success: () -> Unit)
        fun requestSavePicture(
            actualFile: File,
            success: () -> Unit
        )
    }

    interface State : IBase.State {
        var filePath: Uri?
        val showRedo: ObservableBoolean
    }
}
