package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import co.yap.modules.dashboard.addionalinfo.adapters.AdditionalInfoEmploymentAdapter
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoEmployment
import co.yap.modules.dashboard.addionalinfo.model.AdditionalInfoEmployment
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoEmploymentState

class AdditionalInfoEmploymentViewModel(application: Application) :
    AdditionalInfoBaseViewModel<IAdditionalInfoEmployment.State>(application),
    IAdditionalInfoEmployment.ViewModel {
    override val additionalInfoEmploymentAdapter: AdditionalInfoEmploymentAdapter =
        AdditionalInfoEmploymentAdapter(context, mutableListOf())

    override fun moveToNext() {
        moveStep()
    }

    override fun onCreate() {
        super.onCreate()
//        setTitle("Additional Information")
//        setSubTitle("What is your employment status?")
        additionalInfoEmploymentAdapter.setList(getMockableList())
    }

    override val state: IAdditionalInfoEmployment.State = AdditionalInfoEmploymentState(application)

    fun getMockableList(): ArrayList<AdditionalInfoEmployment> {
        val list: ArrayList<AdditionalInfoEmployment> = arrayListOf()
        list.add(AdditionalInfoEmployment(0, "Employed", false))
        list.add(AdditionalInfoEmployment(0, "Self Employed", false))
//        list.add(AdditionalDocument(0, "Passport Copy", false))
        return list
    }
}