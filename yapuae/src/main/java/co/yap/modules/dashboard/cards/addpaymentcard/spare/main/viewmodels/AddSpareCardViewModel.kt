package co.yap.modules.dashboard.cards.addpaymentcard.spare.main.viewmodels

import android.app.Application
import co.yap.modules.dashboard.cards.addpaymentcard.main.viewmodels.AddPaymentChildViewModel
import co.yap.modules.dashboard.cards.addpaymentcard.spare.helpers.virtual.AddSpareVirtualCardLogicHelper
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.interfaces.IAddSpareCard
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.states.AddSpareCardState
import co.yap.networking.cards.CardsRepository
import co.yap.networking.cards.requestdtos.AddVirtualSpareCardRequest
import co.yap.networking.cards.responsedtos.Card
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.helpers.extentions.toFormattedCurrency
import co.yap.yapcore.managers.SessionManager


class AddSpareCardViewModel(application: Application) :
    AddPaymentChildViewModel<IAddSpareCard.State>(application), IAddSpareCard.ViewModel,
    IRepositoryHolder<CardsRepository> {

    override val CONFIRM_VIRTUAL_PURCHASE: Int = 3000
    override var paymentCard: Card? = null
    override var cardName: String? = ""
    override val ADD_VIRTUAL_SPARE_SUCCESS_EVENT: Int = 5000
    override val repository: CardsRepository = CardsRepository
    override val addSpareVirtualCardLogicHelper: AddSpareVirtualCardLogicHelper =
        AddSpareVirtualCardLogicHelper(context, this)
    override var cardType: String = ""
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val state: AddSpareCardState =
        AddSpareCardState()

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        state.virtualCardFee =
            parentViewModel?.virtualCardFee?.toFormattedCurrency() ?: ""
    }

    override fun isEnoughBalance(): Boolean =
        (parentViewModel?.virtualCardFee?.toDouble() ?: 0.0 < SessionManager.cardBalance.value?.availableBalance?.toDouble() ?: 0.0)

    override fun onResume() {
        super.onResume()
        setToolBarTitle(getString(Strings.screen_spare_card_landing_display_tool_bar_title))
        toggleToolBarVisibility(true)
    }

    override fun requestAddSpareVirtualCard() {
        launch {
            state.loading = true
            when (val response = repository.addSpareVirtualCard(
                //   AddVirtualSpareCardRequest(SessionManager.user?.currentCustomer?.getFullName())
                AddVirtualSpareCardRequest(
                    cardName,
                    parentViewModel?.selectedVirtualCard?.designCode
                )
            )) {
                is RetroApiResponse.Success -> {
                    paymentCard = response.data.data
                    clickEvent.setValue(ADD_VIRTUAL_SPARE_SUCCESS_EVENT)
                    state.loading = false
                }

                is RetroApiResponse.Error -> {
                    state.toast = "${response.error.message}^${AlertType.DIALOG.name}"
                    state.loading = false
                }
            }
        }
    }
}