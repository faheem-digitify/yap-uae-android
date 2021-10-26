package co.yap.modules.kyc.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.kyc.viewmodels.InformationErrorViewModel
import co.yap.modules.onboarding.interfaces.IInformationError
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.managers.SessionManager

class InformationErrorFragment : KYCChildFragment<IInformationError.ViewModel>() {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_information_error
    override val viewModel: InformationErrorViewModel
        get() = ViewModelProviders.of(this).get(InformationErrorViewModel::class.java)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.state.errorTitle =
            arguments?.let { InformationErrorFragmentArgs.fromBundle(it).errorTitle }.toString()

        viewModel.state.errorGuide =
            arguments?.let { InformationErrorFragmentArgs.fromBundle(it).errorBody }.toString()

//        viewModel.countryName = Translator.getString(
//            requireContext(),
//            Strings.screen_kyc_information_error_display_text_title_from_usa, countryName
//        )

        viewModel.clickEvent.observe(this, Observer {
            if (viewModel.parentViewModel?.identity?.isDateOfBirthValid == true)
                trackEventWithScreenName(FirebaseEvent.KYC_UNDERAGED)
            else
                trackEventWithScreenName(if (viewModel.state.isUSACitizen.get() == true) FirebaseEvent.KYC_US else FirebaseEvent.KYC_SANCTIONED)

            // viewModel.parentViewModel?.finishKyc?.value = DocumentsResponse(false)
            SessionManager.doLogout(requireContext())

        })
    }

    override fun onDestroyView() {
        viewModel.clickEvent.removeObservers(this)
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean = true

}