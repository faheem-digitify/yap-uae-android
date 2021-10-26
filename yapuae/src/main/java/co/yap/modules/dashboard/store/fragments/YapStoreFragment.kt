package co.yap.modules.dashboard.store.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentYapStoreBinding
import co.yap.modules.dashboard.main.fragments.YapDashboardChildFragment
import co.yap.modules.dashboard.store.adaptor.YapStoreAdaptor
import co.yap.modules.dashboard.store.cardplans.activities.CardPlansActivity
import co.yap.modules.dashboard.store.interfaces.IYapStore
import co.yap.modules.dashboard.store.viewmodels.YapStoreViewModel
import co.yap.networking.store.responsedtos.Store
import co.yap.widgets.guidedtour.OnTourItemClickListener
import co.yap.widgets.guidedtour.TourSetup
import co.yap.widgets.guidedtour.models.GuidedTourViewDetail
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.helpers.TourGuideManager
import co.yap.yapcore.helpers.TourGuideType
import co.yap.yapcore.helpers.extentions.ExtraType
import co.yap.yapcore.helpers.extentions.getValue
import co.yap.yapcore.helpers.extentions.launchActivity
import co.yap.yapcore.helpers.extentions.launchTourGuide
import co.yap.yapcore.interfaces.OnItemClickListener
import com.liveperson.infra.configuration.Configuration.getDimension
import kotlinx.android.synthetic.main.fragment_yap_store.*

class YapStoreFragment : YapDashboardChildFragment<IYapStore.ViewModel>(), IYapStore.View {

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_yap_store
    private var tourStep: TourSetup? = null

    override val viewModel: YapStoreViewModel
        get() = ViewModelProviders.of(this).get(YapStoreViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStoreList()
        initComponents()
        setObservers()
    }

    private fun initComponents() {
        recycler_stores.adapter = YapStoreAdaptor(mutableListOf())
        (recycler_stores.adapter as YapStoreAdaptor).allowFullItemClickListener = true
        (recycler_stores.adapter as YapStoreAdaptor).setItemListener(listener)

    }

    private fun setObservers() {
        viewModel.storesLiveData.observe(this, Observer {
            (recycler_stores.adapter as YapStoreAdaptor).setList(it)
        })
        viewModel.parentViewModel?.isYapStoreFragmentVisible?.observe(
            this,
            Observer { isStoreFragmentVisible ->
                if (isStoreFragmentVisible) {
                    tourStep = requireActivity().launchTourGuide(TourGuideType.STORE_SCREEN) {
                        this.addAll(setViewsArray())
                    }
                } else {
                    tourStep?.let {
                        if (it.isShowing)
                            it.dismiss()
                    }
                }
            })
    }

    val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is Store) {
                when (data.id) {
                    Constants.ITEM_STORE_CARD_PLANS -> {
                        launchActivity<CardPlansActivity> { }
                    }
                    /*Constants.ITEM_STORE_HOUSE_H
                    OLD -> startActivityForResult(
                        HouseHoldLandingActivity.newIntent(requireContext()),
                        RequestCodes.REQUEST_ADD_HOUSE_HOLD)*/
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodes.REQUEST_ADD_HOUSE_HOLD) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val finishScreen =
                        data.getValue(
                            RequestCodes.REQUEST_CODE_FINISH,
                            ExtraType.BOOLEAN.name
                        ) as? Boolean
                    finishScreen?.let { it ->
                        if (it) {
                            //finish()  // Transaction fragment
                        } else {
                            // other things?
                        }
                    }
                }
            }
        }
    }


    private fun getRecycleViewAdaptor(): YapStoreAdaptor? {
        return if (recycler_stores.adapter is YapStoreAdaptor) {
            (recycler_stores.adapter as YapStoreAdaptor)
        } else {
            null
        }
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivRightIcon -> {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
//                val tour = TourSetup(requireActivity(), setViewsArray())
//                tour.startTour()
            }
        }
    }


    private fun setViewsArray(): ArrayList<GuidedTourViewDetail> {
        val list = ArrayList<GuidedTourViewDetail>()
        list.add(
            GuidedTourViewDetail(
                getBindings().toolbar.findViewById(R.id.ivRightIcon),
                getString(R.string.screen_dashboard_tour_guide_display_text_packages),
                getString(R.string.screen_dashboard_tour_guide_display_text_packages_des),
                padding = 0f,
                circleRadius = getDimension(R.dimen._60sdp),
                showSkip = false,
                showPageNo = false,
                btnText = getString(R.string.screen_dashboard_tour_guide_display_text_got_it),
                callBackListener = tourItemListener
            )
        )
        return list
    }

    private val tourItemListener = object : OnTourItemClickListener {
        override fun onTourCompleted(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.STORE_SCREEN,
                completed = true
            )
        }

        override fun onTourSkipped(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.STORE_SCREEN,
                skipped = true
            )
        }
    }

    private fun getBindings(): FragmentYapStoreBinding {
        return viewDataBinding as FragmentYapStoreBinding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.parentViewModel?.isYapStoreFragmentVisible?.removeObservers(this)
    }
}