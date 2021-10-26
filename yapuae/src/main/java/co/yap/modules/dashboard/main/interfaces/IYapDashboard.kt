package co.yap.modules.dashboard.main.interfaces

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.modules.onboarding.enums.AccountType
import co.yap.networking.authentication.AuthRepository
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent


interface IYapDashboard {

    interface State : IBase.State {
        var accountType: AccountType
        var fullName: String
        var firstName: String?
        var accountNo: String
        var ibanNo: String
        var availableBalance: String
        var userNameImage: ObservableField<String>
        var appVersion: ObservableField<String>
        var isFounder: ObservableField<Boolean>
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnNavigationItem(id: Int)
        fun copyAccountInfoToClipboard()
        fun getAccountInfo(): String
        val showUnverifedscreen: MutableLiveData<Boolean>
        fun resendVerificationEmail(callBack: () -> Unit)
        fun logout()
        val authRepository: AuthRepository
        var EVENT_LOGOUT_SUCCESS: Int
        var isYapHomeFragmentVisible: MutableLiveData<Boolean>
        var isYapStoreFragmentVisible: MutableLiveData<Boolean>
        var isYapCardsFragmentVisible: MutableLiveData<Boolean>
        var isYapMoreFragmentVisible: MutableLiveData<Boolean>
        var isUnverifiedScreenNotVisible: MutableLiveData<Boolean>
        var isShowHomeTour: MutableLiveData<Boolean>
        var isKycCompelted: MutableLiveData<Boolean>
        fun populateState()
    }

    interface View : IBase.View<ViewModel> {
        fun closeDrawer()
        fun openDrawer()
        fun toggleDrawer()
        fun isDrawerOpen(): Boolean
        fun enableDrawerSwipe(enable: Boolean)
        val YAP_HOME_FRAGMENT: Int get() = 0
        val YAP_STORE_FRAGMENT: Int get() = 1
        val YAP_CARDS_FRAGMENT: Int get() = 2
        val YAP_MORE_FRAGMENT: Int get() = 3

    }
}