package co.yap.modules.kyc.states

import androidx.databinding.Bindable
import co.yap.yapuae.BR
import co.yap.modules.kyc.interfaces.IDocumentsDashboard
import co.yap.yapcore.BaseState

class DocumentsDashboardState : BaseState(), IDocumentsDashboard.State {

    @get:Bindable
    override var totalProgress: Int = 100
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalProgress)
        }

    @get:Bindable
    override var currentProgress: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentProgress)
        }
}