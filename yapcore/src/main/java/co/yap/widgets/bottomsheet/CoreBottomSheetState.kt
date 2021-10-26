package co.yap.widgets.bottomsheet

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import co.yap.yapcore.BaseState

class CoreBottomSheetState :BaseState(), ICoreBottomSheet.State {
    override var searchText: MutableLiveData<String> = MutableLiveData()
    override var searchBarVisibility: ObservableBoolean = ObservableBoolean()
    override var noItemFound: ObservableBoolean = ObservableBoolean()
    override val headerSeparatorVisibility: ObservableBoolean = ObservableBoolean()
}