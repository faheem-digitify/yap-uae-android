package co.yap.modules.dashboard.addionalinfo.interfaces

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.networking.customers.requestdtos.UploadAdditionalInfo
import co.yap.yapcore.IBase

interface IAdditionalInfoQuestion {
    interface View : IBase.View<ViewModel>

    interface ViewModel : IBase.ViewModel<State> {
        fun moveToNext()
        fun uploadAnswer(uploadAdditionalInfo: UploadAdditionalInfo, success: () -> Unit)
    }

    interface State : IBase.State {
        val questionTitle: ObservableField<String>
        val question: ObservableField<String>
        val answer: ObservableField<String>
        val valid: ObservableBoolean
    }
}