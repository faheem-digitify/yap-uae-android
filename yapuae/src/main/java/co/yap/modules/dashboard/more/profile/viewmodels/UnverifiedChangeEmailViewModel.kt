package co.yap.modules.dashboard.more.profile.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.models.RetroApiResponse
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.managers.SessionManager

class UnverifiedChangeEmailViewModel(application: Application) : ChangeEmailViewModel(application) {
    override val success: MutableLiveData<Boolean> = MutableLiveData()
    override val repository: CustomersRepository = CustomersRepository

    override fun onHandlePressOnNextButton() {
        if (state.newEmailValidation() && state.confirmEmailValidation()) {
            if (state.newEmail == state.newConfirmEMail) {
                launch {
                    state.loading = true
                    when (val response =
                        repository.validateEmail(state.newEmail)) {
                        is RetroApiResponse.Success -> {
                            changeUnverifiedEmailRequest()
                        }

                        is RetroApiResponse.Error -> {
                            state.loading = false
                            state.setErrors(response.error.message)

                        }
                    }
                }
            } else {
                state.setErrors(Translator.getString(context,
                    Strings.screen_change_email_display_text_email_match_error))
            }
        }
    }

    private fun changeUnverifiedEmailRequest() {
        launch {
            when (val response =
                repository.changeUnverifiedEmail(state.newEmail)) {
                is RetroApiResponse.Success -> {
                    SessionManager.user?.currentCustomer?.email = state.newEmail
                    SharedPreferenceManager.getInstance(context).saveUserNameWithEncryption(state.newEmail)
                    success.value = true
                }

                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.setErrors(response.error.message)

                }

            }
            state.loading = false
        }
    }
}