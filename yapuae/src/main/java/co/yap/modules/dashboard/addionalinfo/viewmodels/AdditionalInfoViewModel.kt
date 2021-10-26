package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfo
import co.yap.modules.dashboard.addionalinfo.states.AdditionalInfoState
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.networking.customers.models.additionalinfo.AdditionalQuestion
import co.yap.networking.customers.requestdtos.UploadAdditionalInfo
import co.yap.networking.customers.responsedtos.additionalinfo.AdditionalInfo
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.enums.AdditionalInfoScreenType

class AdditionalInfoViewModel(application: Application) :
    BaseViewModel<IAdditionalInfo.State>(application = application),
    IAdditionalInfo.ViewModel, IRepositoryHolder<CustomersRepository> {
    override val repository: CustomersRepository = CustomersRepository
    override val stepCount: MutableLiveData<Int> = MutableLiveData(0)
    override val state: IAdditionalInfo.State = AdditionalInfoState()

    override fun getAdditionalInfo(success: (AdditionalInfo?) -> Unit) {
        launch {
            state.loading = true
            when (val response = repository.getAdditionalInfoRequired()) {
                is RetroApiResponse.Success -> {
                    state.loading = false
                    success(response.data.additionalInfo)
                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    showToast(response.error.message)
                }
            }
//            delay(5000)
//            state.loading = false
//            success( getData())
//            additionalInfoResponse.value = getMockData()
//            setSteps()
//
//

        }
    }

    override fun setSteps(additionalInfo: AdditionalInfo?, success: () -> Unit) {
        if (additionalInfo != null) {
            when {
                !additionalInfo.documentInfo.isNullOrEmpty() && !additionalInfo.textInfo.isNullOrEmpty() -> {
                    setAdditionalInfoData(2, AdditionalInfoScreenType.BOTH_SCREENS)
                    state.documentList.addAll(additionalInfo.documentInfo as ArrayList<AdditionalDocument>)
                    state.questionList.addAll(additionalInfo.textInfo as ArrayList<AdditionalQuestion>)
                }
                !additionalInfo.documentInfo.isNullOrEmpty() -> {
                    setAdditionalInfoData(1, AdditionalInfoScreenType.DOCUMENT_SCREEN)
                    state.documentList.addAll(additionalInfo.documentInfo as ArrayList<AdditionalDocument>)
                }
                !additionalInfo.textInfo.isNullOrEmpty() -> {
                    setAdditionalInfoData(1, AdditionalInfoScreenType.QUESTION_SCREEN)
                    state.questionList.addAll(additionalInfo.textInfo as ArrayList<AdditionalQuestion>)
                }
                else -> {
                    setAdditionalInfoData(-1, AdditionalInfoScreenType.SUCCESS_SCREEN)
                }
            }
            success.invoke()
        } else {
            setAdditionalInfoData(-1, AdditionalInfoScreenType.SUCCESS_SCREEN)
            success.invoke()
        }
    }

    private fun setAdditionalInfoData(noOfSteps: Int, screenType: AdditionalInfoScreenType) {
        state.steps.set(noOfSteps)
        state.screenType.set(screenType.name)
    }

//    private fun getMockData(): AdditionalInfo {
//        return AdditionalInfo(getData() , listOf())
//    }

    private fun getData(): AdditionalInfo {
        val documentList: ArrayList<AdditionalDocument> = arrayListOf()
        documentList.add(AdditionalDocument(0, "Passport Copy", false, status = "PENDING"))
        documentList.add(AdditionalDocument(0, "Visa Copy", false, status = "PENDING"))
//        list.add(AdditionalDocument(0, "Passport Copy", false))
        val questionList: ArrayList<AdditionalQuestion> = arrayListOf()
        questionList.add(
            AdditionalQuestion(
                0,
                false,
                questionFromCustomer = "Please tell us the name of the company you are currently employed with"
            )
        )
        return AdditionalInfo(documentList, questionList)
    }

    override fun submitAdditionalInfo(success: () -> Unit) {
        launch {
            state.loading = true
            when (val response = repository.submitAdditionalInfo(UploadAdditionalInfo())) {
                is RetroApiResponse.Success -> {
                    state.loading = false
                    success()
                }
                is RetroApiResponse.Error -> {
                    showToast(response.error.message)
                    state.loading = false
                }
            }
        }
    }
}