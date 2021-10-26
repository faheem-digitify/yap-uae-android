package co.yap.modules.dashboard.store.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.yapuae.R
import co.yap.modules.dashboard.main.viewmodels.YapDashboardChildViewModel
import co.yap.modules.dashboard.store.interfaces.IYapStore
import co.yap.modules.dashboard.store.states.YapStoreState
import co.yap.networking.store.responsedtos.Store
import co.yap.translation.Strings
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import kotlinx.coroutines.delay

class YapStoreViewModel(application: Application) :
    YapDashboardChildViewModel<IYapStore.State>(application),
    IYapStore.ViewModel {

    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override val state: YapStoreState = YapStoreState()
    override val storesLiveData: MutableLiveData<MutableList<Store>> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()
        state.toolbarVisibility.set(true)
        state.rightIconVisibility.set(true)
        state.toolbarTitle = getString(Strings.screen_yap_store_display_text_title)
    }

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun getStoreList() {
        // need api in future
        val list = mutableListOf<Store>()
        state.loading = true
        launch {
            delay(1000)
            list.add(
                Store(
                    id = Constants.ITEM_STORE_CARD_PLANS,
                    name = getString(Strings.screen_yap_store_card_plans_label_text),
                    desc = getString(Strings.screen_yap_store_card_plans_description_text),
                    image = R.drawable.banner_card_plans,
                    storeIcon = R.drawable.ic_card_plans,
                    isComingSoon = false
                )
            )
            list.add(
                Store(
                    id = Constants.ITEM_STORE_YOUNG,
                    name = getString(Strings.screen_yap_store_young_label_text),
                    desc = getString(Strings.screen_yap_store_young_description_text),
                    image = R.drawable.ic_store_young,
                    storeIcon = R.drawable.ic_young_smile,
                    isComingSoon = true
                )
            )
            list.add(
                Store(
                    id = Constants.ITEM_STORE_HOUSE_HOLD,
                    name = getString(Strings.screen_yap_store_household_label_text),
                    desc = getString(Strings.screen_yap_store_household_description_text),
                    image = R.drawable.ic_store_household,
                    storeIcon = R.drawable.ic_young_household,
                    isComingSoon = true
                )
            )
            storesLiveData.value = list
            state.loading = false
        }
    }
}