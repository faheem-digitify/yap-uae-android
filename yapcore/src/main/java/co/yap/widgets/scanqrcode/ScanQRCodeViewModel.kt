package co.yap.widgets.scanqrcode

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.requestdtos.QRContactRequest
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.networking.models.RetroApiResponse
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleLiveEvent
import kotlinx.coroutines.delay

class ScanQRCodeViewModel(application: Application) :
    BaseViewModel<IScanQRCode.State>(application),
    IScanQRCode.ViewModel {
    override val state: IScanQRCode.State = ScanQRCodeState()
    private val customerRepository: CustomersRepository = CustomersRepository
    override var contactInfo: MutableLiveData<Beneficiary> = MutableLiveData()
    override val noContactFoundEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    override fun uploadQRCode(uuid: String?) {
        launch {
            state.loading=true
            delay(1000L)
            when (val response = customerRepository.getQRContact(
                QRContactRequest(uuid = uuid?:""))) {
                is RetroApiResponse.Success -> {
                    state.loading=false
                    response.data.qrContact.let {
                        contactInfo.value = Beneficiary(
                            beneficiaryUuid = it?.uuid,
                            mobileNo = it?.mobileNo,
                            firstName = it?.firstName,
                            lastName = it?.lastName,
                            beneficiaryPictureUrl = it?.profilePictureName,
                            country = it?.countryCode,
                            title = it?.fullName()
                        )
                    }
                }
                is RetroApiResponse.Error -> {
                    state.toast = response.error.message
                    state.loading=false
                    noContactFoundEvent.value = true
                }
            }
        }
    }
}