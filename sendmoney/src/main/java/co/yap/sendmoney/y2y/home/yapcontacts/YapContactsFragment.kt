package co.yap.sendmoney.y2y.home.yapcontacts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.repositories.InviteFriendRepository
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.FragmentYapContactsBinding
import co.yap.sendmoney.y2y.main.fragments.Y2YBaseFragment
import co.yap.widgets.State
import co.yap.widgets.Status
import co.yap.widgets.skeletonlayout.Skeleton
import co.yap.widgets.skeletonlayout.applySkeleton
import co.yap.yapcore.BR
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.share
import co.yap.yapcore.interfaces.OnItemClickListener

class YapContactsFragment : Y2YBaseFragment<IYapContact.ViewModel>(), IYapContact.View {
    private lateinit var skeleton: Skeleton
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_yap_contacts

    override val viewModel: YapContactViewModel
        get() = ViewModelProviders.of(this).get(YapContactViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        setObservers()
    }

    private fun initComponents() {
        viewModel.contactsAdapter.setItemListener(listener)
        skeleton = getDataBindingView<FragmentYapContactsBinding>().recycler.applySkeleton(
            R.layout.layout_item_contacts_shimmer,
            5
        )
    }

    private fun setObservers() {
        viewModel.clickEvent.observe(this, observer)
        viewModel.state.stateLiveData?.observe(this, Observer { handleShimmerState(it) })
        viewModel.parentViewModel?.y2yBeneficiries?.observe(this, Observer {
            viewModel.contactsAdapter.setList(it)
            if (!it.isNullOrEmpty())
                viewModel.contactsAdapter.filter.filter(viewModel.parentViewModel?.searchQuery?.value)
            viewModel.state.stateLiveData?.value =
                if (it.isNullOrEmpty()) State.error(null) else State.success(null)
            viewModel.state.contactsCounts.set(it.size)
        })

        viewModel.parentViewModel?.searchQuery?.observe(this, Observer {
            viewModel.contactsAdapter.filter.filter(it)
        })

        viewModel.contactsAdapter.filterCount.observe(this, Observer {
            if (it == 0 && viewModel.parentViewModel?.isSearching?.value == true && !viewModel.state.isNoYapContacts.get()) {
                viewModel.state.stateLiveData?.value = State.empty(null)
            } else {
                viewModel.state.isNoSearchResult.set(false)
            }
            viewModel.state.isShowContactsCounter.set(it != 0)
            viewModel.state.contactsCounts.set(it)
        })

    }

    val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when (view.id) {
                R.id.lyContact -> {
                    if (data is IBeneficiary && data.isYapUser) {
                        navigate(
                            viewModel.getActionId(parentFragment),
                            args = viewModel.getBundle(data, pos),
                            screenType = FeatureSet.Y2Y_TRANSFER
                        )
                    }
                }
            }
        }
    }

    private val observer = Observer<Int> {
        when (it) {
            R.id.btnInvite -> {
                trackEventWithScreenName(FirebaseEvent.CLICK_INVITE)
                InviteFriendRepository().inviteAFriend()
                requireContext().share(text = Utils.getGeneralInvitationBody(requireContext()))
            }
        }
    }

    private fun handleShimmerState(state: State?) {
        when (state?.status) {
            Status.LOADING -> {
                skeleton.showSkeleton()
                viewModel.state.isShowContactsCounter.set(false)
            }
            Status.EMPTY -> {
                viewModel.state.isNoSearchResult.set(true)
                viewModel.state.isShowContactsCounter.set(false)
            }
            Status.ERROR -> {
                viewModel.state.isNoYapContacts.set(true)
                viewModel.state.isShowContactsCounter.set(false)
            }
            Status.SUCCESS -> {
                viewModel.state.isShowContactsCounter.set(true)
                skeleton.showOriginal()
            }
            else -> {
                skeleton.showOriginal()
            }
        }
    }
}
