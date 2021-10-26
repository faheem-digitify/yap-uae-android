package co.yap.sendmoney.y2y.home.yapcontacts

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.sendmoney.R
import co.yap.sendmoney.y2y.home.fragments.Y2YSearchContactsFragment
import co.yap.sendmoney.y2y.home.fragments.YapToYapFragment
import co.yap.sendmoney.y2y.main.viewmodels.Y2YBaseViewModel
import co.yap.yapcore.SingleClickEvent

class YapContactViewModel(application: Application) :
    Y2YBaseViewModel<IYapContact.State>(application),
    IYapContact.ViewModel {

    override val state: IYapContact.State = YapContactState()
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    override var contactsAdapter: YapContactsAdaptor = YapContactsAdaptor(mutableListOf())

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun getBundle(data: IBeneficiary, pos: Int): Bundle {
        val result = Bundle()
        result.putString("imagePath", data.imgUrl)
        result.putString("receiverUUID", data.accountUUID)
        result.putString("beneficiaryName", data.fullName)
        result.putInt("position", pos)
        result.putString("beneficiaryCreationDate", data.creationDateOfBeneficiary)
        return result
    }

    override fun getActionId(fragment: Fragment?): Int {
        return when (fragment) {
            is YapToYapFragment -> {
                R.id.action_yapToYapHome_to_y2YTransferFragment
            }
            is Y2YSearchContactsFragment -> {
                R.id.action_y2YSearchContactsFragment_to_y2YTransferFragment
            }
            else -> {
                -1
            }
        }
    }
}