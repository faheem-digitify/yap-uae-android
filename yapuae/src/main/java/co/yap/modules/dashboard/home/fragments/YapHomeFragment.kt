package co.yap.modules.dashboard.home.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.app.YAPApplication
import co.yap.app.YAPApplication.Companion.homeTransactionsRequest
import co.yap.yapuae.databinding.ActivityYapDashboardBinding
import co.yap.yapuae.databinding.FragmentYapHomeBinding
import co.yap.modules.dashboard.cards.analytics.main.activities.CardAnalyticsActivity
import co.yap.modules.dashboard.home.adaptor.GraphBarsAdapter
import co.yap.modules.dashboard.home.adaptor.NotificationAdapter
import co.yap.modules.dashboard.home.adaptor.TransactionsHeaderAdapter
import co.yap.modules.dashboard.home.filters.activities.TransactionFiltersActivity
import co.yap.modules.dashboard.home.filters.models.TransactionFilters
import co.yap.modules.dashboard.home.helpers.AppBarStateChangeListener
import co.yap.modules.dashboard.home.helpers.transaction.TransactionsViewHelper
import co.yap.modules.dashboard.home.interfaces.IYapHome
import co.yap.modules.dashboard.home.interfaces.NotificationItemClickListener
import co.yap.modules.dashboard.home.status.DashboardNotificationStatusHelper
import co.yap.modules.dashboard.home.viewmodels.YapHomeViewModel
import co.yap.modules.dashboard.main.activities.YapDashboardActivity
import co.yap.modules.dashboard.main.fragments.YapDashboardChildFragment
import co.yap.modules.dashboard.main.viewmodels.YapDashBoardViewModel
import co.yap.modules.dashboard.more.yapforyou.activities.YAPForYouActivity
import co.yap.modules.dashboard.transaction.detail.TransactionDetailsActivity
import co.yap.modules.dashboard.transaction.search.TransactionSearchFragment
import co.yap.modules.dashboard.yapit.addmoney.main.AddMoneyActivity
import co.yap.modules.kyc.activities.DocumentsDashboardActivity
import co.yap.modules.location.activities.LocationSelectionActivity
import co.yap.modules.others.fragmentpresenter.activities.FragmentPresenterActivity
import co.yap.modules.setcardpin.activities.SetCardPinWelcomeActivity
import co.yap.networking.cards.responsedtos.Address
import co.yap.networking.cards.responsedtos.Card
import co.yap.networking.customers.responsedtos.AccountInfo
import co.yap.networking.customers.responsedtos.documents.GetMoreDocumentsResponse
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.networking.notification.responsedtos.NotificationAction
import co.yap.networking.transactions.responsedtos.transaction.HomeTransactionListData
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.translation.Strings
import co.yap.widgets.MultiStateView
import co.yap.widgets.State
import co.yap.widgets.Status
import co.yap.widgets.guidedtour.OnTourItemClickListener
import co.yap.widgets.guidedtour.TourSetup
import co.yap.widgets.guidedtour.models.GuidedTourViewDetail
import co.yap.widgets.skeletonlayout.Skeleton
import co.yap.widgets.skeletonlayout.applySkeleton
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.ADDRESS_SUCCESS
import co.yap.yapcore.constants.Constants.BROADCAST_UPDATE_TRANSACTION
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.EIDStatus
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.PartnerBankStatus
import co.yap.yapcore.enums.TransactionStatus
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.NotificationHelper
import co.yap.yapcore.helpers.TourGuideManager
import co.yap.yapcore.helpers.TourGuideType
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager
import com.google.android.material.appbar.AppBarLayout
import com.liveperson.infra.configuration.Configuration.getDimension
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.view_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class YapHomeFragment : YapDashboardChildFragment<IYapHome.ViewModel>(), IYapHome.View,
    NotificationItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var mAdapter: NotificationAdapter? = null
    private var parentViewModel: YapDashBoardViewModel? = null
    override var transactionViewHelper: TransactionsViewHelper? = null
    private var dashboardNotificationStatusHelper: DashboardNotificationStatusHelper? = null
    private lateinit var skeleton: Skeleton
    private var tourStep: TourSetup? = null

    override val viewModel: YapHomeViewModel
        get() = ViewModelProviders.of(this).get(YapHomeViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_yap_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.parentViewModel?.isYapHomeFragmentVisible?.value = true
        parentViewModel =
            activity?.let { ViewModelProviders.of(it).get(YapDashBoardViewModel::class.java) }
    }

    private fun startFlowForSetPin(card: Card?) {
        card?.let {
            if (viewModel.shouldShowSetPin(it)) {
                if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
                    viewModel.clickEvent.setValue(viewModel.EVENT_SET_CARD_PIN)
                }
            }
        } ?: toast("Invalid card found")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerTransactionBroadcast()
        initComponents()
        setObservers()
        setClickOnWelcomeYapItem()
        setAvailableBalance(viewModel.state.availableBalance)
    }

    private fun setClickOnWelcomeYapItem() {
        getBindings().lyInclude.multiStateView.getView(MultiStateView.ViewState.EMPTY)
            ?.setOnClickListener { openYapForYou() }
    }

    private fun openYapForYou() {
        launchActivity<YAPForYouActivity>(type = FeatureSet.YAP_FOR_YOU)
    }

    private fun initComponents() {
        getBindings().lyInclude.rvTransaction.layoutManager = LinearLayoutManager(context)
        getBindings().lyInclude.rvTransaction.adapter =
            TransactionsHeaderAdapter(mutableListOf(), transactionClickListener)
        getRecycleViewAdaptor()?.allowFullItemClickListener = true
        skeleton = getBindings().lyInclude.rvTransaction.applySkeleton(
            R.layout.item_transaction_list_shimmer,
            5
        )
        viewModel.state.showTxnShimmer.observe(this, Observer { handleShimmerState(it) })
        getBindings().refreshLayout.setOnRefreshListener(this)
        //rvTransactionsBarChart.updatePadding(right = getScreenWidth()/2)
        rvTransactionsBarChart.adapter = GraphBarsAdapter(mutableListOf(), viewModel)

        getBindings().lyInclude.rvTransaction.apply {
            fixSwipeToRefresh(getBindings().refreshLayout)
        }

        getBindings().appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val pram = getBindings().lyInclude.lyHomeAction.layoutParams
            if (abs(verticalOffset) <= 5) {
                getBindings().lyInclude.lyHomeAction.alpha = 1f
                pram.height = appBarLayout.totalScrollRange
                getBindings().lyInclude.lyHomeAction.layoutParams = pram
            } else {
                if (Math.abs(verticalOffset) > 0)
                    getBindings().lyInclude.lyHomeAction.alpha =
                        10 / abs(verticalOffset).toFloat()
                pram.height = appBarLayout?.totalScrollRange?.plus(verticalOffset)!!
                getBindings().lyInclude.lyHomeAction.layoutParams = pram
            }
        })
    }

    private fun handleShimmerState(state: State?) {
        when (state?.status) {
            Status.LOADING -> skeleton.showSkeleton()
            else -> skeleton.showOriginal()
        }
    }

    override var drawerButtonEnabled: Boolean = true

    override fun onRefresh() {
        if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
            viewModel.isRefreshing.value = true
            homeTransactionsRequest.number = 0
            viewModel.requestAccountTransactions()
            getBindings().refreshLayout.isRefreshing = false
            getBindings().appbar.setExpanded(true)
        } else {
            getBindings().refreshLayout.isRefreshing = false
        }
    }

    private val transactionClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            viewModel.clickEvent.setPayload(
                SingleClickEvent.AdaptorPayLoadHolder(
                    view,
                    data,
                    pos
                )
            )
            viewModel.clickEvent.setValue(view.id)
        }
    }

    private fun openTransactionFilters() {
        if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
            trackEventWithScreenName(FirebaseEvent.CLICK_FILTER_TRANSACTIONS)
            startActivityForResult(
                TransactionFiltersActivity.newIntent(
                    requireContext(),
                    viewModel.txnFilters
                ),
                RequestCodes.REQUEST_TXN_FILTER
            )
        }
    }

    override fun setObservers() {
        SessionManager.onAccountInfoSuccess.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                checkUserStatus()
                viewModel.state.isPartnerBankStatusActivated.set(PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus)
            }
        })
        getBindings().ivSearch.setOnLongClickListener {
            return@setOnLongClickListener activity?.let {
                //val tour = TourSetup(it, setViewsArray())
                //tour.startTour()
                //showToast("YAP Signature Info${YAPApplication.configManager?.toString()}" + "^" + AlertType.DIALOG)
                true
            } ?: false
        }

        listenForToolbarExpansion()
        viewModel.clickEvent.observe(this, Observer {
            if (drawerButtonEnabled)
                when (it) {
                    R.id.ivSearch -> startFragment(TransactionSearchFragment::class.java.name)
                    R.id.lyTransaction -> {
                        viewModel.clickEvent.getPayload()?.let {
                            val childPosition = it.position
                            val groupPosition = it.itemData as Int
                            val transaction: Transaction? =
                                getRecycleViewAdaptor()?.getDataForPosition(groupPosition)?.transaction?.get(
                                    childPosition
                                )
                            launchActivity<TransactionDetailsActivity>(requestCode = RequestCodes.REQUEST_FOR_TRANSACTION_NOTE_ADD_EDIT) {
                                putExtra(
                                    ExtraKeys.TRANSACTION_OBJECT_STRING.name,
                                    transaction
                                )
                                putExtra(
                                    ExtraKeys.TRANSACTION_OBJECT_GROUP_POSITION.name,
                                    groupPosition
                                )
                                putExtra(
                                    ExtraKeys.TRANSACTION_OBJECT_CHILD_POSITION.name,
                                    childPosition
                                )
                            }
                        }
                        viewModel.clickEvent.setPayload(null)
                    }
                    viewModel.EVENT_SET_CARD_PIN -> {
                        SessionManager.getPrimaryCard()?.let { card ->
                            startActivityForResult(
                                SetCardPinWelcomeActivity.newIntent(
                                    requireContext(),
                                    card
                                ), RequestCodes.REQUEST_FOR_SET_PIN
                            )
                        } ?: showToast("Debit card not found.")
                    }
                    viewModel.ON_ADD_NEW_ADDRESS_EVENT -> {//This Fragment is attached in Location Selection Activity 's CardOnTheWayFragment. We have commented this line because we have implemented new
//                        startActivityForResult(
//                            FragmentPresenterActivity.getIntent(          //Design on completion of Onboarding in the above mentioned fragment.
//                                requireContext(),
//                                MODE_MEETING_CONFORMATION,
//                                null
//                            ), RequestCodes.REQUEST_MEETING_CONFIRMED
//                        )☻
                        SessionManager.getAccountInfo {
                            GlobalScope.launch(Main) {
                                setUpDashBoardNotificationsView()
                            }
                        }
                    }
                    R.id.ivMenu -> {

                        parentView?.toggleDrawer()
                    }
                    R.id.rlFilter -> {

                        if (viewModel.state.isTransEmpty.get() == false) {
                            openTransactionFilters()
                        } else {
                            if (homeTransactionsRequest.totalAppliedFilter > 0) {
                                openTransactionFilters()
                            } else {
                                return@Observer
                            }
                        }
                    }
                    R.id.lyAnalytics -> {
                        launchActivity<CardAnalyticsActivity>(type = FeatureSet.ANALYTICS)
                    }
                    R.id.lyAdd -> {
                        openTopUpScreen()
                    }
                }
        })

        SessionManager.card.value?.let {
            startFlowForSetPin(it)
            SessionManager.card.value?.let {
                viewModel.fetchTransactionDetailsForLeanplum(it.status)
            }
        } ?: SessionManager.getDebitCard { card ->
            startFlowForSetPin(card)
        }

        SessionManager.card.observe(this, Observer { primaryCard ->
            primaryCard?.let {
                checkUserStatus()
            }
        })

        SessionManager.cardBalance.observe(this, Observer { value ->
            setAvailableBalance(value.availableBalance.toString())
        })

        viewModel.transactionsLiveData.observe(this, Observer { it ->
            if (true == viewModel.isLoadMore.value) {
                if (getRecycleViewAdaptor()?.itemCount == 0) getBindings().appbar.setExpanded(true)

                getRecycleViewAdaptor()?.itemCount?.let { itemCount ->
                    if (itemCount > 0) {
                        getRecycleViewAdaptor()?.removeItemAt(position = itemCount - 1)
                    }
                }

                val listToAppend: MutableList<HomeTransactionListData> = mutableListOf()
                getGraphRecycleViewAdapter()?.getDataList()?.let { oldData ->
                    for (parentItem in it) {
                        var shouldAppend = false
                        for (i in 0 until oldData.size) {
                            if (parentItem.date == oldData[i].date) {
                                if (parentItem.transaction.size != oldData[i].transaction.size) {
                                    shouldAppend = true
                                    parentItem.isNewItem = false
                                    break
                                }
                                parentItem.isNewItem = true
                                shouldAppend = true
                                break
                            } else {
                                parentItem.isNewItem = true
                            }
                        }
                        if (!shouldAppend)
                            listToAppend.add(parentItem)
                    }
                }
                listToAppend.partition { txn -> txn.isNewItem }.let { pair ->
                    pair.second.forEach { data ->
                        getTransactionPosition(data)?.let { index ->
                            getRecycleViewAdaptor()?.setItemAt(index, data)
                        }
                    }
                    getRecycleViewAdaptor()?.addList(pair.first)
                }
                getGraphRecycleViewAdapter()?.addList(listToAppend)
                viewModel.isLoadMore.value = false
            } else {
                when {
                    it.isEmpty() -> {
                        //if transaction is empty and filter is applied then state would be Error where no transaction image show
                        if (homeTransactionsRequest.totalAppliedFilter > 0) {
                            getBindings().lyInclude.multiStateView.viewState =
                                MultiStateView.ViewState.ERROR
                        } else {
                            viewModel.state.isUserAccountActivated.set(false)
                            SessionManager.getAccountInfo {
                                GlobalScope.launch(Main) {
                                    setUpDashBoardNotificationsView()
                                }
                            }
                        }
                        transactionViewHelper?.setTooltipVisibility(View.GONE)
                        viewModel.state.isTransEmpty.set(true)
                    }
                    it.size < 5 -> {
                        if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
                            viewModel.state.isTransEmpty.set(false)
                            viewModel.state.isUserAccountActivated.set(true)
                            showTransactions()
                        }
                        getRecycleViewAdaptor()?.setList(it)
                    }
                    else -> {
                        if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
                            viewModel.state.isUserAccountActivated.set(true)
                            showTransactionsAndGraph()
                        } else {
                            viewModel.state.isTransEmpty.set(true)
                        }
                        getRecycleViewAdaptor()?.setList(it)
                        getGraphRecycleViewAdapter()?.setList(it)
                        transactionViewHelper?.setTooltipOnZero()
                    }
                }
            }
        })

        getRecycleViewAdaptor()?.setItemListener(transactionClickListener)
        getRecycleViewAdaptor()?.allowFullItemClickListener = true
        getBindings().lyInclude.rvTransaction.addOnScrollListener(
            object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager =
                        getBindings().lyInclude.rvTransaction.layoutManager as LinearLayoutManager
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    if (viewModel.state.showTxnShimmer.value?.status == Status.SUCCESS)
                        if (lastVisiblePosition == layoutManager.itemCount - 1) {
                            if (false == viewModel.isLoadMore.value && false == viewModel.isLast.value) {
                                viewModel.isLoadMore.value = true
                            }
                        }
                }
            })

        viewModel.isLoadMore.observe(this, Observer
        {
            if (it) {
                homeTransactionsRequest.number =
                    homeTransactionsRequest.number + 1
                val item =
                    getRecycleViewAdaptor()?.getDataForPosition(getRecycleViewAdaptor()?.itemCount!! - 1)
                        ?.copy()
                item?.totalAmount = "loader"
                getRecycleViewAdaptor()?.addListItem(item!!)
                viewModel.loadMore()
            }

        })
        viewModel.parentViewModel?.isYapHomeFragmentVisible?.observe(
            this,
            Observer { isHomeFragmentVisible ->
                if (isHomeFragmentVisible) {
                    viewModel.parentViewModel?.isShowHomeTour?.value = isHomeFragmentVisible
                } else {
                    tourStep?.let {
                        if (it.isShowing) {
                            it.dismiss()
                        }
                    }
                }
            })
        viewModel.parentViewModel?.isUnverifiedScreenNotVisible?.observe(
            this,
            Observer { isUnverifiedScreenVisible ->
                if (isUnverifiedScreenVisible) {
                    viewModel.parentViewModel?.isShowHomeTour?.value = isUnverifiedScreenVisible
                }
            })

        viewModel.parentViewModel?.isShowHomeTour?.observe(this, Observer {
            if (viewModel.parentViewModel?.isUnverifiedScreenNotVisible?.value == true && viewModel.parentViewModel?.isYapHomeFragmentVisible?.value == true && viewModel.state.showTxnShimmer.value?.status != Status.LOADING) {
                showHomeTourGuide()
            }
        })

        viewModel.parentViewModel?.isKycCompelted?.observe(this, Observer {
            if(it)
                viewModel.clickEvent.setValue(viewModel.ON_ADD_NEW_ADDRESS_EVENT)
        })
    }

    private fun getTransactionPosition(item: HomeTransactionListData): Int? {
        return getRecycleViewAdaptor()?.getDataList()
            ?.indexOf(getRecycleViewAdaptor()?.getDataList()?.first {
                it.date == item.date
            })
    }

    private fun checkUserStatus() {
        setNotificationAdapter(SessionManager.user, SessionManager.card.value)
    }

    private fun setNotificationAdapter(accountInfo: AccountInfo?, paymentCard: Card?) {
        accountInfo?.let { account ->
            paymentCard?.let { card ->
                mAdapter = NotificationAdapter(
                    requireContext(),
                    NotificationHelper.getNotifications(account, card, requireContext()),
                    this
                )
                getBindings().lyInclude.rvNotificationList.setSlideOnFling(false)
                getBindings().lyInclude.rvNotificationList.setOverScrollEnabled(true)
                getBindings().lyInclude.rvNotificationList.adapter = mAdapter
                getBindings().lyInclude.rvNotificationList.smoothScrollToPosition(0)
                getBindings().lyInclude.rvNotificationList.setItemTransitionTimeMillis(150)
                getBindings().lyInclude.rvNotificationList.setItemTransformer(
                    ScaleTransformer.Builder()
                        .setMaxScale(1.05f)
                        .setMinScale(1f)
                        .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                        //.setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                        .build()
                )
            }
        }
    }

    private fun clearNotification() {
        mAdapter?.removeAllItems()
    }

    override fun onCloseClick(notification: HomeNotification, position: Int) {
        super.onCloseClick(notification, position)
        clearNotification()
    }

    private fun showTransactionsAndGraph() {
        if (viewModel.transactionsLiveData.value.isNullOrEmpty()) {
            if (0 >= viewModel.state.filterCount.get() ?: 0) {
                viewModel.state.isTransEmpty.set(true)
            }
        } else {
            getBindings().lyInclude.multiStateView.viewState = MultiStateView.ViewState.CONTENT
            viewModel.state.isTransEmpty.set(false)
            view?.let {
                transactionViewHelper = TransactionsViewHelper(
                    requireContext(),
                    it,
                    viewModel
                )
                getGraphRecycleViewAdapter()?.helper = transactionViewHelper
            }
        }
    }

    private fun showTransactions() {
        if (viewModel.transactionsLiveData.value.isNullOrEmpty()) {
            if (0 >= viewModel.state.filterCount.get() ?: 0) {
                viewModel.state.isTransEmpty.set(true)
            }
        } else {
            getBindings().lyInclude.multiStateView.viewState = MultiStateView.ViewState.CONTENT
            viewModel.state.isTransEmpty.set(false)
            view?.let {
                transactionViewHelper = TransactionsViewHelper(
                    requireContext(),
                    it,
                    viewModel
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.state.filterCount.set(homeTransactionsRequest.totalAppliedFilter)
        SessionManager.updateCardBalance {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterTransactionBroadcast()
        getBindings().appbar.removeOnOffsetChangedListener(appbarListener)
    }

    override fun onDestroy() {
        viewModel.clickEvent.removeObservers(this)
        viewModel.parentViewModel?.isYapHomeFragmentVisible?.removeObservers(this)
        SessionManager.onAccountInfoSuccess.removeObservers(this)
        super.onDestroy()

    }

    private fun setAvailableBalance(balance: String?) {
        getBindings().skeletonLayout.apply {
            if (balance.isNullOrEmpty()) showSkeleton() else showOriginal()
        }
        getBindings().tvAvailableBalance.text = balance.getAvailableBalanceWithFormat()
    }

    override fun onClick(notification: HomeNotification, position: Int) {
        if (position != getBindings().lyInclude.rvNotificationList.currentItem) {
            getBindings().lyInclude.rvNotificationList.smoothScrollToPosition(position)
            return
        }

        when (notification.action) {
            NotificationAction.COMPLETE_VERIFICATION -> {
                launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
                    putExtra(
                        Constants.name,
                        SessionManager.user?.currentCustomer?.firstName.toString()
                    )
                    putExtra(Constants.data, false)
                }
            }

            NotificationAction.SET_PIN -> {
                SessionManager.card.value?.let {
                    viewModel.clickEvent.setValue(viewModel.EVENT_SET_CARD_PIN)
                }
            }

            NotificationAction.UPDATE_EMIRATES_ID -> {
                if (SessionManager.user?.otpBlocked == true) {
                    if (SessionManager.eidStatus == EIDStatus.NOT_SET &&
                        PartnerBankStatus.ACTIVATED.status != SessionManager.user?.partnerBankStatus
                    ) {
                        launchActivity<DocumentsDashboardActivity>(requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS) {
                            putExtra(
                                Constants.name,
                                SessionManager.user?.currentCustomer?.firstName.toString()
                            )
                            putExtra(Constants.data, true)
                            putExtra(
                                "document",
                                GetMoreDocumentsResponse.Data.CustomerDocument.DocumentInformation(
                                    identityNo = SessionManager.user?.currentCustomer?.identityNo
                                )
                            )
                        }
                    } else {
                        showBlockedFeatureAlert(requireActivity(), FeatureSet.UPDATE_EID)
                    }
                } else {
                    launchActivity<DocumentsDashboardActivity>(
                        requestCode = RequestCodes.REQUEST_KYC_DOCUMENTS,
                        type = FeatureSet.UPDATE_EID
                    ) {
                        putExtra(
                            Constants.name,
                            SessionManager.user?.currentCustomer?.firstName.toString()
                        )
                        putExtra(Constants.data, true)
                        putExtra(
                            "document",
                            GetMoreDocumentsResponse.Data.CustomerDocument.DocumentInformation(
                                identityNo = SessionManager.user?.currentCustomer?.identityNo
                            )
                        )
                    }
                }
            }
            NotificationAction.HELP_AND_SUPPORT -> {
                startActivity(
                    FragmentPresenterActivity.getIntent(
                        requireContext(),
                        Constants.MODE_HELP_SUPPORT, null
                    )
                )
            }
            NotificationAction.CARD_FEATURES_BLOCKED -> {
                requireContext().makeCall(SessionManager.helpPhoneNumber)
            }
        }
    }

    private val appbarListener = object : AppBarStateChangeListener() {
        override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
            if (state == State.COLLAPSED) {
                transactionViewHelper?.onToolbarCollapsed()
            } else if (state == State.EXPANDED) {
                transactionViewHelper?.onToolbarExpanded()
            }
        }
    }

    private fun listenForToolbarExpansion() {
        getBindings().appbar.addOnOffsetChangedListener(appbarListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RequestCodes.REQUEST_KYC_DOCUMENTS -> {
                data?.let {
                    val result =
                        data.getBooleanExtra(Constants.result, false)
                    if (result) {
                        startActivityForResult(
                            LocationSelectionActivity.newIntent(
                                context = requireContext(),
                                address = SessionManager.userAddress ?: Address(),
                                headingTitle = getString(Strings.screen_meeting_location_display_text_add_new_address_title),
                                subHeadingTitle = getString(Strings.screen_meeting_location_display_text_subtitle),
                                onBoarding = true
                            ), RequestCodes.REQUEST_FOR_LOCATION
                        )
                    } else {
                        val kycAction =
                            data.getValue(
                                "status",
                                ExtraType.STRING.name
                            ) as? String
//                        if (KYCAction.ACTION_EID_UPDATE.name == kycAction) checkUserStatus()
                    }
                }
            }
            RequestCodes.REQUEST_FOR_LOCATION -> {
                data?.let {
                    val result = it.getBooleanExtra(ADDRESS_SUCCESS, false)
                    if (result) {
                        viewModel.clickEvent.setValue(viewModel.ON_ADD_NEW_ADDRESS_EVENT)
                    }
                }
            }
            RequestCodes.REQUEST_TXN_FILTER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val filters: TransactionFilters? =
                        data?.getParcelableExtra<TransactionFilters?>("txnRequest")
                    if (viewModel.txnFilters != filters) {
                        setTransactionRequest(filters)
                        getFilterTransactions()
                    }
                }
            }
            RequestCodes.REQUEST_MEETING_CONFIRMED -> {
                SessionManager.getAccountInfo()
            }
            RequestCodes.REQUEST_FOR_SET_PIN -> {
                data?.let {
                    val isPinSet =
                        it.getBooleanExtra(Constants.isPinCreated, false)
                    val isSkip =
                        it.getBooleanExtra(Constants.IS_TOPUP_SKIP, false)
                    getGraphRecycleViewAdapter()?.notifyDataSetChanged()
                    if (isPinSet) {
                        SessionManager.getDebitCard {
                            GlobalScope.launch(Main) {
                                setUpDashBoardNotificationsView()
                            }
                        }
                    } else {
                        SessionManager.getDebitCard {
                            GlobalScope.launch(Main) {
                                setUpDashBoardNotificationsView()
                            }
                        }
                        launchActivity<AddMoneyActivity>()
                    }
                }
            }
            RequestCodes.REQUEST_FOR_TRANSACTION_NOTE_ADD_EDIT -> {

                val groupPosition = data.let { intent ->
                    intent?.getIntExtra(
                        ExtraKeys.TRANSACTION_OBJECT_GROUP_POSITION.name,
                        -1
                    )
                }
                val childPosition = data.let { intent ->
                    intent?.getIntExtra(
                        ExtraKeys.TRANSACTION_OBJECT_CHILD_POSITION.name,
                        -1
                    )
                }
                if (groupPosition != -1 && childPosition != -1) {
                    getRecycleViewAdaptor()?.getDataForPosition(
                        groupPosition ?: 0
                    )?.transaction?.get(
                        childPosition ?: 0
                    )?.transactionNote =
                        (data?.getParcelableExtra(ExtraKeys.TRANSACTION_OBJECT_STRING.name) as Transaction).transactionNote

                    getRecycleViewAdaptor()?.getDataForPosition(
                        groupPosition ?: 0
                    )?.transaction?.get(
                        childPosition ?: 0
                    )?.receiverTransactionNote =
                        (data.getParcelableExtra(ExtraKeys.TRANSACTION_OBJECT_STRING.name) as Transaction).receiverTransactionNote

                    getRecycleViewAdaptor()?.getDataForPosition(
                        groupPosition ?: 0
                    )?.transaction?.get(
                        childPosition ?: 0
                    )?.transactionNoteDate =
                        (data.getParcelableExtra(ExtraKeys.TRANSACTION_OBJECT_STRING.name) as Transaction).transactionNoteDate
                    getRecycleViewAdaptor()?.notifyItemChanged(
                        groupPosition ?: 0,
                        getRecycleViewAdaptor()?.getDataForPosition(
                            groupPosition ?: 0
                        )?.transaction?.get(childPosition ?: 0)
                    )

                    getRecycleViewAdaptor()?.getDataForPosition(
                        groupPosition ?: 0
                    )?.transaction?.get(
                        childPosition ?: 0
                    )?.receiverTransactionNoteDate =
                        (data.getParcelableExtra(ExtraKeys.TRANSACTION_OBJECT_STRING.name) as Transaction).receiverTransactionNoteDate
                    getRecycleViewAdaptor()?.notifyItemChanged(
                        groupPosition ?: 0,
                        getRecycleViewAdaptor()?.getDataForPosition(
                            groupPosition ?: 0
                        )?.transaction?.get(childPosition ?: 0)
                    )

                }
            }

            RequestCodes.REQUEST_FOR_ADDITIONAL_REQUIREMENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    SessionManager.getAccountInfo {
                        GlobalScope.launch(Main) {
                            dashboardNotificationStatusHelper?.notifyAdapter()
                        }
                    }
                }
            }
        }
    }

    private fun setTransactionRequest(filters: TransactionFilters?) {
        filters?.let {
            viewModel.txnFilters = it
            homeTransactionsRequest.number = 0
            homeTransactionsRequest.size = YAPApplication.pageSize
            homeTransactionsRequest.txnType = it.getTxnType()
            homeTransactionsRequest.amountStartRange = it.amountStartRange
            homeTransactionsRequest.amountEndRange = it.amountEndRange
            homeTransactionsRequest.title = null
            homeTransactionsRequest.totalAppliedFilter = it.totalAppliedFilter
            homeTransactionsRequest.categories = it.categories
            homeTransactionsRequest.statues =
                if (it.pendingTxn == true) arrayListOf(
                    TransactionStatus.PENDING.name,
                    TransactionStatus.IN_PROGRESS.name
                ) else null
        }
    }

    private fun getFilterTransactions() {
        transactionViewHelper?.setTooltipVisibility(View.GONE)
        viewModel.filterTransactions()
    }

    private fun getRecycleViewAdaptor(): TransactionsHeaderAdapter? {
        return if (getBindings().lyInclude.rvTransaction.adapter is TransactionsHeaderAdapter) {
            (getBindings().lyInclude.rvTransaction.adapter as TransactionsHeaderAdapter)
        } else {
            null
        }
    }

    private fun getGraphRecycleViewAdapter(): GraphBarsAdapter? {
        return if (rvTransactionsBarChart.adapter is GraphBarsAdapter) {
            (rvTransactionsBarChart.adapter as GraphBarsAdapter)
        } else {
            null
        }
    }

    private fun getBindings(): FragmentYapHomeBinding {
        return viewDataBinding as FragmentYapHomeBinding
    }

    private fun registerTransactionBroadcast() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadCastReceiver, IntentFilter(BROADCAST_UPDATE_TRANSACTION))
    }

    private fun unregisterTransactionBroadcast() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(broadCastReceiver)
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                BROADCAST_UPDATE_TRANSACTION -> {
                    onRefresh()
                }
            }
        }
    }

    private fun openTopUpScreen() {
        launchActivity<AddMoneyActivity>(type = FeatureSet.TOP_UP)
    }

    private fun setViewsArray(): ArrayList<GuidedTourViewDetail> {
        val list = ArrayList<GuidedTourViewDetail>()
        list.add(
            GuidedTourViewDetail(
                getBindings().ivMenu,
                getString(R.string.screen_dashboard_tour_guide_display_text_top_menu),
                getString(R.string.screen_dashboard_tour_guide_display_text_top_menu_des),
                padding = -getDimension(R.dimen._20sdp),
                circleRadius = getDimension(R.dimen._60sdp),
                callBackListener = homeTourItemListener
            )
        )
        list.add(
            GuidedTourViewDetail(
                getBindings().tvAvailableBalance,
                getString(R.string.screen_dashboard_tour_guide_display_text_balance),
                getString(R.string.screen_dashboard_tour_guide_display_text_balance_des),
                padding = getDimension(R.dimen._70sdp),
                circleRadius = getDimension(R.dimen._70sdp),
                callBackListener = homeTourItemListener
            )
        )
        list.add(
            GuidedTourViewDetail(
                getParentActivity().cvYapIt,
                getString(R.string.screen_dashboard_tour_guide_display_text_top_yap_it),
                getString(R.string.screen_dashboard_tour_guide_display_text_top_yap_it_des),
                padding = getDimension(R.dimen._260sdp),
                circleRadius = getDimension(R.dimen._70sdp),
                callBackListener = homeTourItemListener
            )
        )
        list.add(
            GuidedTourViewDetail(
                getBindings().ivSearch,
                getString(R.string.screen_dashboard_tour_guide_display_text_search),
                getString(R.string.screen_dashboard_tour_guide_display_text_search_des),
                padding = getDimension(R.dimen._45sdp),
                circleRadius = getDimension(R.dimen._60sdp),
                btnText = getString(R.string.screen_dashboard_tour_guide_display_text_finish),
                showSkip = false,
                callBackListener = homeTourItemListener
            )
        )
        return list
    }

    private val homeTourItemListener = object : OnTourItemClickListener {
        override fun onTourCompleted(pos: Int) {
            TourGuideManager.lockTourGuideScreen(TourGuideType.DASHBOARD_SCREEN, completed = true)
            showGraphTourGuide(viewModel.transactionsLiveData.value?.size ?: 0)
        }

        override fun onTourSkipped(pos: Int) {
            TourGuideManager.lockTourGuideScreen(TourGuideType.DASHBOARD_SCREEN, skipped = true)
            showGraphTourGuide(viewModel.transactionsLiveData.value?.size ?: 0)
        }
    }

    private val graphTourItemListener = object : OnTourItemClickListener {
        override fun onTourCompleted(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.DASHBOARD_GRAPH_SCREEN,
                completed = true
            )
        }

        override fun onTourSkipped(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.DASHBOARD_GRAPH_SCREEN,
                skipped = true
            )
        }
    }

    private fun getParentActivity(): ActivityYapDashboardBinding {
        return (activity as? YapDashboardActivity)?.viewDataBinding as ActivityYapDashboardBinding
    }

    private fun setUpDashBoardNotificationsView() {
        dashboardNotificationStatusHelper = DashboardNotificationStatusHelper(
            this,
            getBindings(),
            viewModel
        )
    }


    private fun setGraphViewsArray(): ArrayList<GuidedTourViewDetail> {
        val list = ArrayList<GuidedTourViewDetail>()
        list.add(
            GuidedTourViewDetail(
                getBindings().lyInclude.llGraph,
                getString(R.string.screen_dashboard_tour_guide_display_text_graph),
                getString(R.string.screen_dashboard_tour_guide_display_text_graph_des),
                padding = getDimension(R.dimen._5sdp),
                circleRadius = getDimension(R.dimen._80sdp),
                btnText = getString(R.string.screen_dashboard_tour_guide_display_text_finish),
                showSkip = false,
                showPageNo = false,
                callBackListener = graphTourItemListener,
                circlePadding = getDimension(R.dimen._25sdp)
            )
        )
        return list
    }

    private suspend fun startGraphTour() {
        if (parentView?.isDrawerOpen() == false && viewModel.parentViewModel?.isYapHomeFragmentVisible?.value == true) {
            tourStep =
                requireActivity().launchTourGuide(TourGuideType.DASHBOARD_GRAPH_SCREEN) {
                    addAll(setGraphViewsArray())
                }
            delay(300)
            drawerButtonEnabled = true
        }
    }

    private fun showGraphTourGuide(listSize: Int) {
        if (listSize >= 5)
            CoroutineScope(Main).launch {
                drawerButtonEnabled = false
                delay(500)
                startGraphTour()
            }
    }

    private fun showHomeTourGuide() {
        if (PartnerBankStatus.ACTIVATED.status == SessionManager.user?.partnerBankStatus) {
            tourStep = requireActivity().launchTourGuide(TourGuideType.DASHBOARD_SCREEN) {
                addAll(setViewsArray())
            }
            if (tourStep == null)
                showGraphTourGuide(viewModel.transactionsLiveData.value?.size ?: 0)
        }
    }
}
