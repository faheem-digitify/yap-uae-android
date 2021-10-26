package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoStart
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoStartState
import co.yap.translation.Strings
import co.yap.yapcore.managers.SessionManager

class AdditionalInfoStartViewModel(application: Application) :
    AdditionalInfoBaseViewModel<IAdditionalInfoStart.State>(application = application),
    IAdditionalInfoStart.ViewModel {
    override val state: IAdditionalInfoStart.State = AdditionalInfoStartState(application)
    override fun onCreate() {
        super.onCreate()
        state.subTitle.set(getString(Strings.screen_additional_info_label_text_required_des))
        state.title.set(getString(Strings.common_text_hey) + " " + SessionManager.user?.currentCustomer?.firstName + ",")
    }
}