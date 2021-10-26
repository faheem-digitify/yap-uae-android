package co.yap.modules.pdf

import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import co.yap.networking.transactions.responsedtos.CardStatement
import co.yap.yapcore.BR
import co.yap.yapcore.BaseState

open class PDFState : BaseState(), IPDFActivity.State {

    @get:Bindable
    override var hideCross: Boolean? = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.hideCross)
        }
    override var cardStatement: ObservableField<CardStatement>? = ObservableField()

    override var toolBarTitle: ObservableField<String>? = ObservableField()

}
