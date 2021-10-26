package co.yap.modules.dashboard.transaction.totalpurchases

import androidx.databinding.ObservableField
import co.yap.yapcore.BaseState

class TotalPurchaseState : BaseState(), ITotalPurchases.State {

    override var countWithDate: ObservableField<String> = ObservableField()
    override var totalSpendings: ObservableField<String> = ObservableField("Spendings")
    override var merchantName: ObservableField<String> = ObservableField()
}