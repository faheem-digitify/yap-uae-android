package co.yap.modules.pdf

import androidx.databinding.ObservableField
import co.yap.networking.transactions.responsedtos.CardStatement
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import java.io.File

interface IPDFActivity {

    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var file: File?
        fun handlePressView(id: Int)
        fun downloadFile(filePath: String, success: (file: File?) -> Unit)
        fun requestSendEmail(cardStatement: CardStatement?)

    }

    interface State : IBase.State {
        var hideCross: Boolean?
        var cardStatement: ObservableField<CardStatement>?
        var toolBarTitle: ObservableField<String>?
    }
}