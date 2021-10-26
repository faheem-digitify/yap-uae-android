package co.yap.sendmoney.y2y.main.states

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import co.yap.sendmoney.R
import co.yap.sendmoney.BR
import co.yap.sendmoney.y2y.main.interfaces.IY2Y
import co.yap.yapcore.BaseState

class Y2YState : BaseState(), IY2Y.State {

    @get:Bindable
    override var tootlBarVisibility: Int = 0x00000000
        set(value) {
            field = value
            notifyPropertyChanged(BR.tootlBarVisibility)

        }

    @get:Bindable
    override var rightButtonVisibility: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.rightButtonVisibility)
        }

    @get:Bindable
    override var leftButtonVisibility: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.leftButtonVisibility)
        }

    @get:Bindable
    override var rightIcon: Int = R.drawable.ic_gift_vector
        set(value) {
            field = value
            notifyPropertyChanged(BR.rightIcon)
        }

    override var fromQR: ObservableBoolean? = ObservableBoolean()
}