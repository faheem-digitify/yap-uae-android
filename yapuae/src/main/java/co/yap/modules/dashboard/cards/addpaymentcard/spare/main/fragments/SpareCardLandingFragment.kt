package co.yap.modules.dashboard.cards.addpaymentcard.spare.main.fragments


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.cards.addpaymentcard.main.fragments.AddPaymentChildFragment
import co.yap.modules.dashboard.cards.addpaymentcard.main.viewmodels.AddPaymentCardViewModel
import co.yap.modules.dashboard.cards.addpaymentcard.models.BenefitsModel
import co.yap.modules.dashboard.cards.addpaymentcard.spare.SpareCardsLandingAdapter
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.interfaces.ISpareCards
import co.yap.modules.dashboard.cards.addpaymentcard.spare.main.viewmodels.SpareCardLandingViewModel
import co.yap.yapcore.constants.Constants.KEY_AVAILABLE_BALANCE
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.helpers.extentions.dimen
import kotlinx.android.synthetic.main.fragment_spare_card_landing.*


class SpareCardLandingFragment : AddPaymentChildFragment<ISpareCards.ViewModel>(), ISpareCards.View,
    SpareCardsLandingAdapter.OnItemClickedListener {

    override fun onItemClick(benefitsModel: BenefitsModel) {}

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_spare_card_landing

    override val viewModel: SpareCardLandingViewModel
        get() = ViewModelProviders.of(this).get(SpareCardLandingViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getVirtualCardFee()
        viewModel.parentViewModel?.getVirtualCardDesigns {
            if (!viewModel.parentViewModel?.virtualCardDesignsList.isNullOrEmpty()) {
                addSpareCard.enableButton(true)
                viewModel.parentViewModel?.selectedVirtualCard =
                    viewModel.parentViewModel?.virtualCardDesignsList?.firstOrNull()
                viewModel.state.cardImageUrl =
                    viewModel.parentViewModel?.virtualCardDesignsList?.firstOrNull()?.frontSideDesignImage
                        ?: ""
            } else {
                addSpareCard.enableButton(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCardDimens()
        addBenefitRecyclerView()
        SharedPreferenceManager.getInstance(requireActivity()).removeValue(KEY_AVAILABLE_BALANCE)

        ViewModelProviders.of(requireActivity())
            .get(AddPaymentCardViewModel::class.java).state.tootlBarTitle =
            "Add a virtual spare card"

        setObservers()
    }

    private fun setCardDimens() {
        val params = linearLayout2.layoutParams
        params.width = linearLayout2.context.dimen(R.dimen._204sdp)
        params.height = linearLayout2.context.dimen(R.dimen._225sdp)
        linearLayout2.layoutParams = params
    }

    private fun gotoAddSpareVirtualCardConfirmScreen() {
        val action =
            SpareCardLandingFragmentDirections.actionSpareCardLandingFragmentToAddSpareCardFragment(
                getString(R.string.screen_spare_card_landing_display_text_virtual_card),
                "",
                "",
                "",
                "",
                false
            )
        navigate(action)
    }

    private fun addBenefitRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        rvBenefits.layoutManager = layoutManager
        rvBenefits.isNestedScrollingEnabled = false
        rvBenefits.adapter =
            SpareCardsLandingAdapter(
                viewModel.loadJSONDummyList(),
                null
            )
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.addSpareCard -> {
                    //gotoAddSpareVirtualCardConfirmScreen()
                    gotoAddVirtualCardScreen()
                }
                R.id.llAddVirtualCard -> {
                    gotoAddSpareVirtualCardConfirmScreen()
                }

                R.id.llAddPhysicalCard -> {

                    val action =
                        SpareCardLandingFragmentDirections.actionSpareCardLandingFragmentToAddSpareCardFragment(
                            getString(R.string.screen_spare_card_landing_display_text_physical_card),
                            "",
                            "",
                            "",
                            "",
                            false
                        )
                    findNavController().navigate(action)
                }

            }
        })
        viewModel.errorEvent.observe(this, Observer {
            requireActivity().finish()
        })
        viewModel.isFeeReceived.observe(viewLifecycleOwner, Observer {
            if (it) viewModel.updateFees("")
        })
        viewModel.updatedFee.observe(viewLifecycleOwner, Observer {
            viewModel.state.virtualCardFee = it
            viewModel.parentViewModel?.virtualCardFee = it
        })
    }

    private fun gotoAddVirtualCardScreen() {
        val action =
            SpareCardLandingFragmentDirections.actionSpareCardLandingFragmentToAddVirtualCardFragment()
        navigate(action)
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
        viewModel.errorEvent.removeObservers(this)
    }

    override fun onPause() {
        removeObservers()
        super.onPause()
    }

    override fun onDestroy() {
        removeObservers()
        super.onDestroy()
    }
}