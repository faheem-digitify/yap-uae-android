package co.yap.modules.dashboard.cards.addpaymentcard.spare.virtual

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAddVirtualCardBinding
import co.yap.modules.dashboard.cards.addpaymentcard.main.fragments.AddPaymentChildFragment
import co.yap.widgets.CircleView
import co.yap.yapcore.helpers.extentions.dimen
import co.yap.yapcore.helpers.extentions.getColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_add_virtual_card.*

class AddVirtualCardFragment : AddPaymentChildFragment<IAddVirtualCard.ViewModel>(),
    TabLayout.OnTabSelectedListener, IAddVirtualCard.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_add_virtual_card
    override val viewModel: AddVirtualCardViewModel
        get() = ViewModelProviders.of(this).get(AddVirtualCardViewModel::class.java)
    var virtualCardAdapter: AddVirtualCardAdapter = AddVirtualCardAdapter(mutableListOf())
    private var tabViews = ArrayList<CircleView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateAdapter()
    }

    private fun initiateAdapter() {
        virtualCardAdapter.setList(viewModel.getCardThemesOption())
        viewModel.adapter.set(virtualCardAdapter)
        getBindings().viewPager.adapter = viewModel.adapter.get()
        setupPager()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun setupPager() {
        getBindings().viewPager?.apply {
            viewModel.state.cardDesigns?.observe(this@AddVirtualCardFragment, Observer {
                TabLayoutMediator(tabLayout, this,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        val view =
                            layoutInflater.inflate(R.layout.item_circle_view, null) as CircleView
                        view.layoutParams = ViewGroup.LayoutParams(
                            dimen(R.dimen._20sdp),
                            dimen(R.dimen._20sdp)
                        )
                        try {
                            view.circleColorStart =
                                Color.parseColor(it[position].designCodeColors?.firstOrNull()?.colorCode)
                            view.circleColorEnd =
                                Color.parseColor(it[position].designCodeColors?.get(1)?.colorCode)
                            view.circleColorDirection = CircleView.GradientDirection.TOP_TO_BOTTOM

                        } catch (e: Exception) {
                        }
                        getBindings().tabLayout.addOnTabSelectedListener(this@AddVirtualCardFragment)
                        tabViews.add(view)
                        onTabSelected(tabLayout.getTabAt(0))
                        viewModel.state.designCode?.value =
                            virtualCardAdapter.getDataList()[0].designCode
                        tab.customView = view
                    }).attach()
            })
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            viewModel.parentViewModel?.selectedVirtualCard =
                virtualCardAdapter.getDataList()[it.position]
            viewModel.state.designCode?.value =
                virtualCardAdapter.getDataList()[it.position].designCode
            tabViews[it.position].borderWidth = 6f
            tabViews[it.position].borderColor = requireContext().getColors(R.color.greyLight)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.let {
            tabViews[it.position].borderWidth = 0f
            tabViews[it.position].borderColor = requireContext().getColors(R.color.greyLight)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun getBindings(): FragmentAddVirtualCardBinding {
        return viewDataBinding as FragmentAddVirtualCardBinding
    }

    override fun addObservers() {
        viewModel.clickEvent.observe(this, clickObserver)
        viewModel.state.cardName.addOnPropertyChangedCallback(stateObserver)
    }

    private val stateObserver = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            virtualCardAdapter.cardName.set(viewModel.state.cardName.get())
            if (viewModel.observeCardNameLength(viewModel.state.cardName.get() ?: "")) {
                viewModel.clickEvent.call()
            }
        }
    }
    private val clickObserver = Observer<Int> { id ->
        when (id) {
            R.id.btnNext -> {
                val action =
                    AddVirtualCardFragmentDirections.actionAddVirtualCardFragmentToAddSpareCardFragment(
                        getString(R.string.screen_spare_card_landing_display_text_virtual_card),
                        "",
                        "",
                        "",
                        "",
                        false,
                        viewModel.state.cardName.get() ?: ""
                    )
                navigate(action)
            }
        }
    }

    override fun removeObservers() {
        viewModel.state.cardName.removeOnPropertyChangedCallback(stateObserver)
        viewModel.clickEvent.removeObserver(clickObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
    }
}
