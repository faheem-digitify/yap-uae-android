package co.yap.modules.dashboard.addionalinfo.states

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfo
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.networking.customers.models.additionalinfo.AdditionalQuestion
import co.yap.yapcore.BaseState

class AdditionalInfoState : BaseState(), IAdditionalInfo.State {
    override val steps: ObservableField<Int> = ObservableField()
    override val showHeader: ObservableBoolean = ObservableBoolean(false)
    override var documentList: ArrayList<AdditionalDocument> = arrayListOf()
    override var questionList: ArrayList<AdditionalQuestion> = arrayListOf()
    override var screenType: ObservableField<String> = ObservableField("")
    override var buttonTitle: ObservableField<String> = ObservableField("")
}