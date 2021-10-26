package co.yap.modules.location.states

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import co.yap.modules.location.interfaces.ILocation
import co.yap.yapcore.BR
import co.yap.yapcore.BaseState

class LocationState : BaseState(), ILocation.State {

    override var rightIcon: ObservableBoolean = ObservableBoolean(false)
    override var leftIcon: ObservableBoolean = ObservableBoolean(false)
    override var toolbarVisibility: ObservableBoolean = ObservableBoolean(false)

    @get:Bindable
    override var totalProgress: Int = 100
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalProgress)
        }

    @get:Bindable
    override var currentProgress: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentProgress)
        }
}