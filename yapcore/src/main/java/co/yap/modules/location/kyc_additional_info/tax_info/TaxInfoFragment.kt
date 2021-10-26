package co.yap.modules.location.kyc_additional_info.tax_info

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import co.yap.countryutils.country.Country
import co.yap.modules.location.fragments.LocationChildFragment
import co.yap.modules.pdf.PDFActivity
import co.yap.yapcore.BR
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.databinding.FragmentTaxInfoBinding
import co.yap.yapcore.enums.AccountStatus
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.extentions.launchBottomSheet
import co.yap.yapcore.helpers.extentions.makeLinks
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager

class TaxInfoFragment : LocationChildFragment<ITaxInfo.ViewModel>(),
    ITaxInfo.View {

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_tax_info
    override val viewModel: TaxInfoViewModel
        get() = ViewModelProviders.of(this).get(TaxInfoViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (SessionManager.user?.notificationStatuses) {
            AccountStatus.FATCA_GENERATED.name -> {
                skipTaxInfoSelectionFragment()
            }
            else -> {
                addObservers()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBinding().tvTermsConditions.makeLinks(
            Pair("Individual Self Certification Form for CRS & FATCA.", View.OnClickListener {
                if (viewModel.state.valid.get() == true) {
                    viewModel.saveInfoDetails(false) { pdf ->
                        trackEventWithScreenName(FirebaseEvent.FATCA_KNOW_MORE)
                        startActivity(
                            PDFActivity.newIntent(view.context, pdf ?: "", true)
                        )
                    }
                }
            })
        )
    }

    override fun addObservers() {
        viewModel.clickEvent.observe(this, clickObserver)
        viewModel.state.viewState.observe(this, Observer {
            it?.let {
                when (it) {
                    is ITaxInfo.CountryPicker -> {
                        requireActivity().launchBottomSheet(
                            itemClickListener = object : OnItemClickListener {
                                override fun onItemClick(view: View, data: Any, pos: Int) {
                                    viewModel.onCountryPicked(
                                        it.view,
                                        data as Country,
                                        it.data as TaxModel,
                                        it.pos
                                    )
                                }
                            },
                            label = "Select Country",
                            viewType = Constants.VIEW_WITH_FLAG,
                            countriesList = viewModel.parentViewModel?.countries?.filter { country -> country.isoCountryCode2Digit != "AE" }
                        )
                    }
                    else -> {

                    }
                }
            }
        })
    }

    private val clickObserver = Observer<Int> {
        when (it) {
            R.id.nextButton -> {
                viewModel.saveInfoDetails(true) {
                    trackEventWithScreenName(FirebaseEvent.TAX_RESIDENCE_SUBMIT)
                    navigate(
                        R.id.action_taxInfoFragment_to_employmentStatusSelectionFragment
                    )
                }
            }
        }
    }

    private fun skipTaxInfoSelectionFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.taxInfoFragment, true) // starting destination skipped
            .build()

        findNavController().navigate(
            R.id.action_taxInfoFragment_to_employmentStatusSelectionFragment,
            null,
            navOptions
        )
    }

    override fun onBackPressed(): Boolean = false

    override fun getBinding(): FragmentTaxInfoBinding {
        return (viewDataBinding as FragmentTaxInfoBinding)
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObserver(clickObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }
}
