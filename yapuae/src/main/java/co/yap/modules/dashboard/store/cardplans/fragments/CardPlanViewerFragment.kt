package co.yap.modules.dashboard.store.cardplans.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentViewerCardPlansBinding
import co.yap.modules.dashboard.store.cardplans.adaptors.CardsFragmentAdapter
import co.yap.modules.dashboard.store.cardplans.interfaces.ICardViewer
import co.yap.modules.dashboard.store.cardplans.viewmodels.CardPlanViewerViewModel

class CardPlanViewerFragment : CardPlansBaseFragment<ICardViewer.ViewModel>(), ICardViewer.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_viewer_card_plans

    override val viewModel: CardPlanViewerViewModel
        get() = ViewModelProviders.of(this).get(CardPlanViewerViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdaptor()
        initArguments()
    }

    private fun setupAdaptor() {
        val adaptor = CardsFragmentAdapter(this)
        getBindings().cardViewPager.adapter = adaptor
    }

    override fun initArguments() {
        arguments?.let { bundle ->
            val fragmentId = bundle.getString(viewModel.parentViewModel?.cardTag)
            val position = viewModel.getFragmentToDisplay(fragmentId)
            getBindings().cardViewPager.currentItem = position
            getBindings().cardViewPager.setCurrentItem(position,
                false)
            viewModel.parentViewModel?.selectedPlan?.set(fragmentId)
        }
    }

    override fun getBindings(): FragmentViewerCardPlansBinding =
        viewDataBinding as FragmentViewerCardPlansBinding
}