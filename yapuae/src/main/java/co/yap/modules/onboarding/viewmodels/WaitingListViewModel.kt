package co.yap.modules.onboarding.viewmodels

import android.app.Application
import co.yap.modules.onboarding.interfaces.IWaitingList
import co.yap.modules.onboarding.states.WaitingListState
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.responsedtos.WaitingRankingResponse
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.helpers.extentions.parseToInt
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

class WaitingListViewModel(application: Application) :
    BaseViewModel<IWaitingList.State>(application), IWaitingList.ViewModel,
    IRepositoryHolder<CustomersRepository> {
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val state: WaitingListState = WaitingListState()
    override var inviteeDetails: ArrayList<WaitingRankingResponse.InviteeDetails>? = arrayListOf()

    override fun onPressView(id: Int) {
        clickEvent.setValue(id)
    }

    override val repository: CustomersRepository
        get() = CustomersRepository

    override fun requestWaitingRanking(success: (showNotification: Boolean) -> Unit) {
        launch {
            state.loading = true
            when (val response = repository.getWaitingRanking()) {
                is RetroApiResponse.Success -> {
                    state.waitingBehind?.set(response.data.data?.waitingBehind ?: "0")
                    state.rank?.set(response.data.data?.rank ?: "0")
                    state.jump?.set(response.data.data?.jump ?: "0")
                    val formattedRank = String.format("%07d", state.rank?.get()?.parseToInt()?.absoluteValue) // 0009
                    val digits = formattedRank.map { it.toString().parseToInt().absoluteValue }
                    state.rankList = digits as MutableList<Int>?
                    state.gainPoints?.set(response.data.data?.gainPoints ?: "0")
                    state.totalGainedPoints?.set(response.data.data?.totalGainedPoints ?: "0")
                    state.signedUpUsers?.set((response.data.data?.inviteeDetails?.size
                        ?: 0).toString())
                    inviteeDetails = response.data.data?.inviteeDetails

                    if (response.data.data?.viewable == true) {
                        stopRankingMsgRequest()
                        //Add delay because snack-bar needs some delay to be shown after api call.
                        delay(500)
                        success(true)
                    } else success(false)
                    state.loading = false
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = response.error.message
                }
            }
        }
    }

    override fun stopRankingMsgRequest() {
        launch(Dispatcher.Background) {
            when (repository.stopRankingMsgRequest()) {
                is RetroApiResponse.Success -> {
                }
                is RetroApiResponse.Error -> {
                }
            }
        }
    }
}
