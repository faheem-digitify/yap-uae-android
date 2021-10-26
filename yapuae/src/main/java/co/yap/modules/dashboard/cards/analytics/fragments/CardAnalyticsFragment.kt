package co.yap.modules.dashboard.cards.analytics.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentCardAnalyticsBinding
import co.yap.modules.dashboard.cards.analytics.adaptors.CATEGORY_ANALYTICS
import co.yap.modules.dashboard.cards.analytics.adaptors.CardAnalyticsLandingAdaptor
import co.yap.modules.dashboard.cards.analytics.adaptors.MERCHANT_ANALYTICS
import co.yap.modules.dashboard.cards.analytics.interfaces.ICardAnalytics
import co.yap.modules.dashboard.cards.analytics.main.fragments.CardAnalyticsBaseFragment
import co.yap.modules.dashboard.cards.analytics.viewmodels.CardAnalyticsViewModel
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.widgets.pieview.*
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.extentions.toFormattedCurrency
import co.yap.yapcore.helpers.spannables.color
import co.yap.yapcore.helpers.spannables.getText
import co.yap.yapcore.leanplum.AnalyticsEvents
import co.yap.yapcore.leanplum.trackEvent
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*
import kotlin.collections.ArrayList


class CardAnalyticsFragment : CardAnalyticsBaseFragment<ICardAnalytics.ViewModel>(),
    ICardAnalytics.View, OnChartValueSelectedListener {

    lateinit var chart: PieChart
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_card_analytics

    override val viewModel: CardAnalyticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackEvent(AnalyticsEvents.ANALYTICS_OPEN.type)
        viewModel.fetchCardCategoryAnalytics(
            DateUtils.dateToString(
                Calendar.getInstance().time,
                "yyyy-MM-dd", DateUtils.TIME_ZONE_Default
            )
        )
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindings()
        setupAdaptor()
        setupTabs()
    }

    private fun setupBindings() {
        viewModel.type.set(Constants.MERCHANT_TYPE)
        getBindingView().rlDetails.setOnClickListener { }
        getBindingView().tabLayout.addOnTabSelectedListener(onTabSelectedListener)
        //viewModel.setPieChartIcon(getBindingView().ivPieView)
        setTextColour()
    }

    /*
    * In this function set Pie View.
    * */

    private fun setPieView(data: ArrayList<TxnAnalytic>?) {
        chart = getBindingView().chart1
        if (::chart.isInitialized) {
            chart.setUsePercentValues(false)
            chart.description.isEnabled = false
            chart.dragDecelerationFrictionCoef = 0.95f
            chart.isDrawHoleEnabled = true
            chart.setHoleColor(Color.TRANSPARENT)
            chart.setTransparentCircleColor(Color.WHITE)
            chart.setTransparentCircleAlpha(200)
//            chart.holeRadius = // 78f  For Rounded corner graph with spaces
            chart.holeRadius = 70f
            chart.transparentCircleRadius = 68f
            chart.setDrawCenterText(true)
            chart.rotationAngle = -90f
            chart.isRotationEnabled = false
            chart.setOnChartValueSelectedListener(this)
            chart.animateY(1400, Easing.EaseInOutQuad)
            chart.legend.isEnabled = false // Hide the legend
            chart.setEntryLabelColor(Color.WHITE)
            chart.setEntryLabelTextSize(0f)
//            chart.setDrawRoundedSlices(true)  // For Rounded corner graph with spaces
            setData(data)
        }
    }

    /*
    * In this set Data in Pie View.
    * */

    @SuppressLint("NewApi")
    private fun setData(txnAnalytics: List<TxnAnalytic>?) {
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors = ArrayList<Int>()
        if (txnAnalytics.isNullOrEmpty()) {
            chart.isHighlightPerTapEnabled = false
            entries.add(PieEntry(100f))
            colors.add(ColorTemplate.getEmptyColor())
        } else {
            //chart.isHighlightPerTapEnabled = true
            //graph click will not be handled in merchant tab
            chart.isHighlightPerTapEnabled =
                getBindingView().tabLayout.selectedTabPosition == CATEGORY_ANALYTICS
            for (item in txnAnalytics.iterator())
                item.totalSpendingInPercentage?.toFloat()?.let { PieEntry(it) }?.let {
                    entries.add(it)
                }
        }
        colors.addAll(resources.getIntArray(co.yap.yapcore.R.array.analyticsColors).toTypedArray())
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 0f
//        dataSet.sliceSpace = 5f   // For Rounded corner graph with spaces
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 12f
        dataSet.setDrawValues(false)
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        chart.data = data
        if (!txnAnalytics.isNullOrEmpty())
            chart.highlightValue(0f, 0)
        else
            chart.highlightValue(0f, -1)

        chart.invalidate()
        viewModel.setPieChartIcon(getBindingView().ivPieView)
        setTextColour()
    }


    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickEventObserver)
        viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.observe(this, Observer {
            if (it != null && it.isNullOrEmpty())
                getBindingView().rlDetails.visibility = View.INVISIBLE
            else
                getBindingView().rlDetails.visibility = View.VISIBLE

            val selectedTabPos = getBindingView().tabLayout.selectedTabPosition
            viewModel.state.selectedItemPosition.set(selectedTabPos)
            setupPieChart(selectedTabPos)
            setSelectedTabData(selectedTabPos, 0)
            viewModel.parentViewModel?.state?.isNoDataFound?.set(
                viewModel.isDataAvailableForSelectedMonth(
                    1
                )
            )

        })

        viewModel.parentViewModel?.selectedItemPosition?.observe(this, Observer {
            when (getBindingView().tabLayout.selectedTabPosition) {
                CATEGORY_ANALYTICS -> {
                    Constants.MERCHANT_TYPE = "merchant-category-id"
                    /*viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value?.let { list ->
                        viewModel.state.selectedTxnAnalyticsItem.set(list[it])
                        updatePieChartInnerData(list[it])
                        setState(list[it])

                    }*/
                    viewModel.state.selectedItemPosition.set(it)
                    // showPieView(it)
                }
                MERCHANT_ANALYTICS -> {
                    Constants.MERCHANT_TYPE = "merchant-name"
                    /*viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.value?.let { list ->
                        viewModel.state.selectedTxnAnalyticsItem.set(list[it])
                        updatePieChartInnerData(list[it])
                        setState(list[it])
                    }*/
                    viewModel.state.selectedItemPosition.set(it)
                    // showPieView(it)
                }
            }
        }
        )
        viewModel.type.set(Constants.MERCHANT_TYPE)
        viewModel.parentViewModel
    }

    /*
    * In this function show PieView.
    * */

    private fun showPieView(indexValue: Int) {
        when (indexValue) {
            0 -> {
                chart.highlightValue(0f, 0, true)
            }
            1 -> {
                chart.highlightValue(1f, 0, true)
            }
            2 -> {
                chart.highlightValue(2f, 0, true)
            }
            3 -> {
                chart.highlightValue(3f, 0, true)
            }
            4 -> {
                chart.highlightValue(4f, 0, true)
            }
        }
    }

    private val clickEventObserver = Observer<Int> {
        when (it) {
            R.id.ivPrevious -> {
                getBindingView().ivPieView.cropImage = false
                viewModel.setPieChartIcon(getBindingView().ivPieView)
                setTextColour()
            }
            Constants.CATEGORY_AVERAGE_AMOUNT_VALUE -> {
                getBindingView().tvMonthlyAverage.text = requireContext().resources.getText(
                    getString(Strings.screen_card_analytics_display_month_average_text),
                    requireContext().color(
                        R.color.colorPrimaryDark,
                        viewModel.state.monthlyCategoryAvgAmount.toString()
                            .toFormattedCurrency(true)
                    )
                )
            }
        }
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let { tabs ->
                setSelectedTabData(tabs.position, 0)
                viewModel.state.selectedTab.set(tabs.position)
                setupPieChart(tabs.position)
                viewModel.parentViewModel?.state?.isNoDataFound?.set(
                    viewModel.isDataAvailableForSelectedMonth(
                        tab.position
                    )
                )
            }
        }
    }

    private fun setupAdaptor() {
        val adaptor = CardAnalyticsLandingAdaptor(this)
        getBindingView().viewPager.adapter = adaptor
    }

    private fun setupTabs() {
        TabLayoutMediator(
            getBindingView().tabLayout, getBindingView().viewPager
        ) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        getBindingView().viewPager.isUserInputEnabled = false
        getBindingView().viewPager.offscreenPageLimit = 1
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            CATEGORY_ANALYTICS -> Translator.getString(
                requireContext(),
                Strings.screen_card_analytics_display_tab_title_category
            )
            MERCHANT_ANALYTICS -> Translator.getString(
                requireContext(),
                Strings.screen_card_analytics_display_tab_title_merchant
            )
            else -> null
        }
    }

    private fun updatePieChartInnerData(item: TxnAnalytic?) {
        item?.let { txnAnalytics ->
            viewModel.state.selectedItemName = txnAnalytics.title
            viewModel.state.selectedItemPercentage = "${txnAnalytics.totalSpendingInPercentage}%"
            viewModel.state.selectedItemSpentValue = txnAnalytics.totalSpending.toFormattedCurrency(
                true,
                currency = viewModel.state.currencyType
            )
        }
        setTextColour()
    }

    private fun reSetPieChartInnerData(item: TxnAnalytic?) {
        item?.let { _ ->
            viewModel.state.selectedItemName = ""
            viewModel.state.selectedItemPercentage = ""
            viewModel.state.selectedItemSpentValue = ""
        }
    }

    private fun getBindingView(): FragmentCardAnalyticsBinding {
        return (viewDataBinding as FragmentCardAnalyticsBinding)
    }

    override fun onNothingSelected() {

    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val selectedItem = getBindingView().tabLayout.selectedTabPosition
        h?.let { highlight ->
            setSelectedTabData(selectedItem, highlight.x.toInt())
            viewModel.parentViewModel?.selectedItemPositionParent?.value = highlight.x.toInt()
        }
    }

    private fun setSelectedTabData(TabPosition: Int, contentPos: Int) {
        when (TabPosition) {
            CATEGORY_ANALYTICS -> {
                //getBindingView().ivPieView.cropImage = false
                Constants.MERCHANT_TYPE = "merchant-category-id"
                trackEventWithScreenName(FirebaseEvent.CLICK_CATEGORY_VIEW)
                /*if (!viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value.isNullOrEmpty()) {
                    getBindingView().ivPieView.visibility = View.VISIBLE
                    val txnItem =
                        viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value?.get(
                            contentPos
                        )
                    updatePieChartInnerData(txnItem)
                    setState(txnItem)
                } else {
                    getBindingView().ivPieView.visibility = View.GONE
                    reSetPieChartInnerData(TxnAnalytic())
                    setState(TxnAnalytic())

                }*/
            }
            MERCHANT_ANALYTICS -> {
                //getBindingView().ivPieView.cropImage = true
                Constants.MERCHANT_TYPE = "merchant-name"
                trackEventWithScreenName(FirebaseEvent.CLICK_MERCHANT_VIEW)
                /*if (!viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.value.isNullOrEmpty()) {
                    getBindingView().ivPieView.visibility = View.VISIBLE
                    val txnItem =
                        viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.value?.get(
                            contentPos
                        )
                    updatePieChartInnerData(txnItem)
                    setState(txnItem)
                } else {
                    getBindingView().ivPieView.visibility = View.GONE
                    reSetPieChartInnerData(TxnAnalytic())
                    setState(TxnAnalytic())
                }*/
            }
        }
        getBindingView().ivPieView.cropImage = false
        if (!viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value.isNullOrEmpty()) {
            getBindingView().ivPieView.visibility = View.VISIBLE
            val txnItem =
                viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value?.get(
                    contentPos
                )
            updatePieChartInnerData(txnItem)
            setState(txnItem)
        } else {
            getBindingView().ivPieView.visibility = View.GONE
            reSetPieChartInnerData(TxnAnalytic())
            setState(TxnAnalytic())
        }
        viewModel.state.selectedItemPosition.set(contentPos)
        viewModel.type.set(Constants.MERCHANT_TYPE)
        viewModel.setPieChartIcon(getBindingView().ivPieView)
        setTextColour()
    }

    private fun setupPieChart(TabPosition: Int) {
        /*when (TabPosition) {
            CATEGORY_ANALYTICS -> {
                viewModel.type.set("merchant-category-id")
                setPieView(viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value)
                viewModel.state.totalSpent = viewModel.state.totalCategorySpent
                getBindingView().tvMonthlyAverage.text = requireContext().resources.getText(
                    getString(Strings.screen_card_analytics_display_month_average_text),
                    requireContext().color(
                        R.color.colorPrimaryDark,
                        viewModel.state.monthlyCategoryAvgAmount.toString()
                            .toFormattedCurrency(true)
                    )
                )
                viewModel.setPieChartIcon(getBindingView().ivPieView)
            }
            MERCHANT_ANALYTICS -> {
                viewModel.type.set("merchant-name")
                setPieView(viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.value)
                viewModel.state.totalSpent = viewModel.state.totalMerchantSpent
                getBindingView().tvMonthlyAverage.text = requireContext().resources.getText(
                    getString(Strings.screen_card_analytics_display_month_average_text),
                    requireContext().color(
                        R.color.colorPrimaryDark,
                        viewModel.state.monthlyMerchantAvgAmount.toString()
                            .toFormattedCurrency(true)
                    )
                )
                viewModel.setPieChartIcon(getBindingView().ivPieView)
            }
        }*/
        viewModel.type.set("merchant-category-id")
        setPieView(viewModel.parentViewModel?.categoryAnalyticsItemLiveData?.value)
        viewModel.state.totalSpent = viewModel.state.totalCategorySpent
        getBindingView().tvMonthlyAverage.text = requireContext().resources.getText(
            getString(Strings.screen_card_analytics_display_month_average_text),
            requireContext().color(
                R.color.colorPrimaryDark,
                viewModel.state.monthlyCategoryAvgAmount.toString()
                    .toFormattedCurrency(true)
            )
        )
        viewModel.setPieChartIcon(getBindingView().ivPieView)
        setTextColour()
    }

    private fun setState(txnAnalytic: TxnAnalytic?) {
        viewModel.state.selectedTxnAnalyticsItem.set(txnAnalytic)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.parentViewModel?.merchantAnalyticsItemLiveData?.removeObservers(this)
        viewModel.clickEvent.removeObservers(this)
        viewModel.parentViewModel?.selectedItemPosition?.removeObservers(this)
    }

    private fun setTextColour() {
        if (viewModel.state.selectedItemPosition.get() == -1) return
        try {
            context?.let {
                val colors = it.resources.getIntArray(co.yap.yapcore.R.array.analyticsColors)
                getBindingView().tvPieViewTitle.setTextColor(colors[viewModel.state.selectedItemPosition.get() % colors.size])
            }

        } catch (ex: Exception) {

        }
    }
}
