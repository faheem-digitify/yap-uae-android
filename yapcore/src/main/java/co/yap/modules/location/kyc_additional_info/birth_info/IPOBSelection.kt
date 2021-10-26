package co.yap.modules.location.kyc_additional_info.birth_info

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.countryutils.country.Country
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.databinding.FragmentPlaceOfBirthSelectionBinding
import co.yap.yapcore.interfaces.OnItemClickListener


interface IPOBSelection {

    interface View : IBase.View<ViewModel> {
        fun addObservers()
        fun removeObservers()
        fun getBinding(): FragmentPlaceOfBirthSelectionBinding
    }

    interface ViewModel : IBase.ViewModel<State> {
        fun handleOnPressView(id: Int)
        fun saveDOBInfo(success: () -> Unit)
        var clickEvent: SingleClickEvent
        var populateSpinnerData: MutableLiveData<ArrayList<Country>>
        fun getAllCountries()
        val dualNatioanlitySpinnerItemClickListener: OnItemClickListener
        val dualNationalityQuestionOptions: ArrayList<String>
    }

    interface State : IBase.State {
        var cityOfBirth: String
        var valid: ObservableField<Boolean>
        var selectedCountry: ObservableField<Country?>
        var selectedSecondCountry: ObservableField<Country?>
        var eidNationality: ObservableField<String>
        var isDualNational: ObservableBoolean
        fun validate()
    }
}
