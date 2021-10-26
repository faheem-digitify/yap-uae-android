package co.yap.repositories

import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.SendInviteFriendRequest
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.managers.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface IInviteAFriend {
    fun inviteAFriend(success: () -> Unit = {})
}

class InviteFriendRepository : IRepositoryHolder<CustomersRepository>, IInviteAFriend {
    override val repository: CustomersRepository = CustomersRepository

    override fun inviteAFriend(success: () -> Unit) {
        val request = SendInviteFriendRequest(
            inviterCustomerId = SessionManager.user?.currentCustomer?.customerId ?: "",
            referralDate = DateUtils.getCurrentDateWithFormat(DateUtils.SERVER_DATE_FULL_FORMAT)
        )
        GlobalScope.launch(Dispatchers.IO) {
            when (repository.sendInviteFriend(request)) {
                is RetroApiResponse.Success -> {
                    success.invoke()
                }
                is RetroApiResponse.Error -> {

                }
            }
        }
    }
}
