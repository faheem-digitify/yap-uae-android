package co.yap.modules.location.kyc_additional_info.tax_info

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState


class TaxInfoState : BaseState(),
    ITaxInfo.State {
    override var valid: ObservableField<Boolean> = ObservableField(false)
    override var onSuccess: ObservableField<Boolean> = ObservableField(false)
    override var isAgreed: ObservableField<Boolean> = ObservableField(true)
}