package co.yap.modules.dashboard.more.yapforyou.achievementdetail

import android.app.Application
import co.yap.modules.dashboard.more.yapforyou.viewmodels.YapForYouBaseViewModel
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.SendInviteFriendRequest
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent

class AchievementGoalDetailViewModel(application: Application) :
    YapForYouBaseViewModel<IAchievementGoalDetail.State>(application = application),
    IAchievementGoalDetail.ViewModel, IRepositoryHolder<CustomersRepository> {
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override val state: AchievementGoalDetailState = AchievementGoalDetailState()
    override val repository: CustomersRepository = CustomersRepository
    override fun handlePressOnButton(id: Int) {
        clickEvent.setValue(id)
    }


    fun inviteFriend(sendInviteFriendRequest: SendInviteFriendRequest) {
        launch(Dispatcher.Background) {
            val response = repository.sendInviteFriend(sendInviteFriendRequest)
            launch {
                when (response) {
                    is RetroApiResponse.Success -> {

                    }
                    is RetroApiResponse.Error -> {
                        state.viewState.value = response.error.message
                    }
                }
            }
        }
    }

}
