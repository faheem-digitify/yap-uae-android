package co.yap.modules.dashboard.store.cardplans.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentPrimeMetalCardBinding
import co.yap.modules.dashboard.store.cardplans.interfaces.IPrimeMetalCard
import co.yap.modules.dashboard.store.cardplans.viewmodels.PrimeMetalCardViewModel
import co.yap.yapcore.constants.Constants
import kotlinx.android.synthetic.main.fragment_prime_metal_card.*


class PrimeCardFragment : CardPlansBaseFragment<IPrimeMetalCard.ViewModel>(), IPrimeMetalCard.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_prime_metal_card

    override val viewModel: PrimeMetalCardViewModel
        get() = ViewModelProviders.of(this).get(PrimeMetalCardViewModel::class.java)

    override fun getBindings(): FragmentPrimeMetalCardBinding =
        viewDataBinding as FragmentPrimeMetalCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parentViewModel?.selectedPlan?.set(Constants.PRIME_CARD_PLAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVideoView()
    }

    override fun initVideoView() {
        planVideo.layoutParams =
            viewModel.parentViewModel?.setViewDimensions(50, getBindings().planVideo)
        planVideo.setVideoURI(Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.video_prime_card_plan))
        planVideo.start()
    }

    override fun onResume() {
        super.onResume()
        getBindings().planVideo.start()
    }
}