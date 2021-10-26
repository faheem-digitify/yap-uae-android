package co.yap.widgets.qrcode

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import co.yap.yapcore.BR
import co.yap.yapcore.BaseState

class QRCodeState : BaseState(), IQRCode.State {
    override var userNameImage: ObservableField<String>? = ObservableField("")

    @get:Bindable
    override var fullName: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.fullName)
        }

    @get:Bindable
    override var imageUri: Uri? = Uri.EMPTY
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUri)
        }

    override var profilePictureUrl: ObservableField<String> = ObservableField()

    @get:Bindable
    override var qrBitmap: Drawable? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.qrBitmap)
        }

}