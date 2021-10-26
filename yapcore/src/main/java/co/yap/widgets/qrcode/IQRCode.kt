package co.yap.widgets.qrcode

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.databinding.ObservableField
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.managers.SessionManager

interface IQRCode {
    interface State : IBase.State {
        var fullName: String?
        var userNameImage: ObservableField<String>?
        var profilePictureUrl: ObservableField<String>
        var imageUri: Uri?
        var qrBitmap: Drawable?
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnView(id: Int)
        fun populateState()
    }

    interface View : IBase.View<ViewModel> {
        val shareQRImageName: String get() = "YAP-qrCode"
        val shareQRTitle: String get() = "YAP QR code"
        val shareQRText: String get() = "Hi! its ${SessionManager.user?.currentCustomer?.getFullName() ?: "YAP User"}  \nHere is my YAP QR Code, please scan it for money transactions."
    }
}