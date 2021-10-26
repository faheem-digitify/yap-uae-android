package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoComplete
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoCompleteState
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.translation.Strings
import co.yap.yapcore.managers.SessionManager

class AdditionalInfoCompleteViewModel(application: Application) :
    AdditionalInfoBaseViewModel<IAdditionalInfoComplete.State>(application = application),
    IAdditionalInfoComplete.ViewModel, IRepositoryHolder<CustomersRepository> {
    override val repository: CustomersRepository = CustomersRepository
    override val state: IAdditionalInfoComplete.State = AdditionalInfoCompleteState(application)

    override fun onCreate() {
        super.onCreate()
        showHeader(false)
        state.subTitle.set(getString(Strings.screen_additional_info_label_text_complete))
        state.title.set(getString(Strings.common_text_thanks) + " " + SessionManager.user?.currentCustomer?.firstName)
    }


}