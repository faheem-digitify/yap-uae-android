package co.yap.modules.dashboard.more.home.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentMoreHomeBinding
import co.yap.modules.dashboard.main.activities.YapDashboardActivity
import co.yap.modules.dashboard.main.fragments.YapDashboardChildFragment
import co.yap.modules.dashboard.more.bankdetails.activities.BankDetailActivity
import co.yap.modules.dashboard.more.cdm.CdmMapFragment
import co.yap.modules.dashboard.more.home.adaptor.YapMoreAdaptor
import co.yap.modules.dashboard.more.home.interfaces.IMoreHome
import co.yap.modules.dashboard.more.home.models.MoreOption
import co.yap.modules.dashboard.more.home.viewmodels.MoreHomeViewModel
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.modules.dashboard.more.notifications.main.NotificationsActivity
import co.yap.modules.dashboard.more.yapforyou.activities.YAPForYouActivity
import co.yap.modules.others.fragmentpresenter.activities.FragmentPresenterActivity
import co.yap.translation.Strings
import co.yap.widgets.SpaceGridItemDecoration
import co.yap.widgets.guidedtour.OnTourItemClickListener
import co.yap.widgets.guidedtour.TourSetup
import co.yap.widgets.guidedtour.models.GuidedTourViewDetail
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.NotificationHelper
import co.yap.yapcore.helpers.TourGuideManager
import co.yap.yapcore.helpers.TourGuideType
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.leanplum.MoreB2CEvents
import co.yap.yapcore.leanplum.trackEvent
import co.yap.yapcore.managers.SessionManager
import com.leanplum.Leanplum
import com.liveperson.infra.configuration.Configuration.getDimension


class YapMoreFragment : YapDashboardChildFragment<IMoreHome.ViewModel>(), IMoreHome.View {

    lateinit var adapter: YapMoreAdaptor
    override fun getBindingVariable(): Int = BR.viewModel
    private var tourStep: TourSetup? = null

    override fun getLayoutId(): Int = R.layout.fragment_more_home

    override val viewModel: IMoreHome.ViewModel
        get() = ViewModelProviders.of(this).get(MoreHomeViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        setupRecycleView()
    }

    override fun onResume() {
        super.onResume()
        initComponents()
        updateNotificationCounter()
    }

    private fun updateNotificationCounter() {
//        Leanplum.forceContentUpdate()
        if (::adapter.isInitialized) {
            if (!adapter.getDataList().isNullOrEmpty()) {
                val notificationCount: Int = NotificationHelper.getNotifications(
                    SessionManager.user,
                    SessionManager.card.value,
                    requireContext()
                ).size
                val item = adapter.getDataForPosition(0)
                viewModel.getTransactionsNotificationsCount {
                    item.badgeCount =
                        Leanplum.getInbox().unreadCount().plus(notificationCount).plus(it ?: 0)
                    //Leanplum.getInbox().unreadCount() > 0
//                Leanplum.getInbox().addChangedHandler(object : InboxChangedCallback() {
//                    override fun inboxChanged() {
//                        item.badgeCount = item.badgeCount.plus(Leanplum.getInbox().unreadCount())
//                        item.hasBadge = item.badgeCount > 0
//                    }
//                })
                    item.hasBadge = item.badgeCount > 0
                    adapter.setItemAt(0, item)
                }
                adapter.setItemAt(0, item)
            }
        }
    }

    private fun initComponents() {
        getBinding().tvName.text =
            SessionManager.user?.currentCustomer?.getFullName()

        val ibanSpan = SpannableString("IBAN ${SessionManager.user?.iban?.maskIbanNumber()}")
        getBinding().tvIban.text = Utils.setSpan(
            0,
            4,
            ibanSpan,
            ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        )

        SessionManager.user?.bank?.swiftCode?.let {
            val bicSpan = SpannableString("BIC $it")
            getBinding().tvBic.text = Utils.setSpan(
                0,
                3,
                bicSpan,
                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
            )
        }

    }

    override fun onDestroy() {
        removeObservers()
        super.onDestroy()
    }

    private fun setupRecycleView() {
        adapter = YapMoreAdaptor(requireContext(), viewModel.getMoreOptions())
        getBinding().recyclerOptions.adapter = adapter

        getBinding().recyclerOptions.addItemDecoration(
            SpaceGridItemDecoration(
                dimen(R.dimen.margin_normal_large) ?: 16, 2, true
            )
        )
        adapter.allowFullItemClickListener = true
        adapter.setItemListener(listener)
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, observer)
        if (context is YapDashboardActivity) {
            (context as YapDashboardActivity).viewModel.isYapMoreFragmentVisible.observe(this,
                Observer { isMoreFragmentVisible ->
                    if (isMoreFragmentVisible) {
                        tourStep =
                            requireActivity().launchTourGuide(TourGuideType.MORE_SCREEN) {
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
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObservers(this)
        if (context is YapDashboardActivity) {
            (context as YapDashboardActivity).viewModel.isYapMoreFragmentVisible.removeObservers(
                this
            )
        }
    }

    private val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is MoreOption)
                viewModel.clickEvent.setValue(data.id)
        }
    }

    private val observer = Observer<Int> {
        when (it) {
            R.id.imgProfile, R.id.imgSettings -> {
                requireActivity().launchActivity<MoreActivity>(requestCode = RequestCodes.REQUEST_CODE_MORE_ACTIVITY) {
                }
            }
            R.id.btnBankDetails -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_BANK_DETAILS)
                launchActivity<BankDetailActivity>()
            }
            R.id.yapForYou -> {
                launchActivity<YAPForYouActivity>(type = FeatureSet.YAP_FOR_YOU)
            }
            Constants.MORE_NOTIFICATION -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_NOTIFICATIONS)
                requireActivity().launchActivity<NotificationsActivity>(requestCode = RequestCodes.REQUEST_NOTIFICATION_FLOW) {
                }
            }
            Constants.MORE_LOCATE_ATM -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_ATM_LOCATION)
                trackEvent(MoreB2CEvents.OPEN_ATM_MAP.type)
                startFragment(CdmMapFragment::class.java.name)
            }
            Constants.MORE_INVITE_FRIEND -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_INVITE_FRIEND)
                startFragment(
                    InviteFriendFragment::class.java.name, false,
                    bundleOf()
                )
            }
            Constants.MORE_HELP_SUPPORT -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_HELP_MORE_SCREEN)
                startActivity(
                    FragmentPresenterActivity.getIntent(
                        requireContext(),
                        Constants.MODE_HELP_SUPPORT, null
                    )
                )
            }
        }
    }

    private fun setViewsArray(): ArrayList<GuidedTourViewDetail> {
        val list = ArrayList<GuidedTourViewDetail>()
        list.add(
            GuidedTourViewDetail(
                getBinding().btnBankDetails,
                title = getString(Strings.screen_more_detail_display_text_tour_bank_details_heading),
                description = getString(Strings.screen_more_detail_display_text_tour_bank_details_description),
                padding = -getDimension(R.dimen._45sdp),
                circleRadius = getDimension(R.dimen._65sdp),
                callBackListener = tourItemListener
            )
        )
        list.add(
            GuidedTourViewDetail(
                getBinding().yapForYou,
                title = getString(Strings.screen_more_detail_display_text_tour_yap_for_you_heading),
                description = getString(Strings.screen_more_detail_display_text_tour_yap_for_you_description),
                showSkip = false,
                showPageNo = true,
                btnText = getString(Strings.screen_more_detail_display_text_tour_yap_for_you_btn_text),
                padding = getDimension(R.dimen._80sdp),
                circleRadius = getDimension(R.dimen._90sdp),
                isRectangle = true,
                callBackListener = tourItemListener
            )
        )
        return list
    }

    private val tourItemListener = object : OnTourItemClickListener {
        override fun onTourCompleted(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.MORE_SCREEN,
                completed = true
            )
        }

        override fun onTourSkipped(pos: Int) {
            TourGuideManager.lockTourGuideScreen(
                TourGuideType.MORE_SCREEN,
                skipped = true
            )
        }
    }

    override fun getBinding(): FragmentMoreHomeBinding {
        return viewDataBinding as FragmentMoreHomeBinding
    }

}