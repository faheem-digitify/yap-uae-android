package co.yap.sendmoney.y2y.home.phonecontacts

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.customers.requestdtos.Contact
import co.yap.networking.customers.responsedtos.sendmoney.IBeneficiary
import co.yap.repositories.InviteFriendRepository
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.FragmentPhoneContactsBinding
import co.yap.sendmoney.y2y.main.fragments.Y2YBaseFragment
import co.yap.widgets.State
import co.yap.widgets.Status
import co.yap.widgets.skeletonlayout.Skeleton
import co.yap.widgets.skeletonlayout.applySkeleton
import co.yap.yapcore.BR
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.helpers.Utils.getBody
import co.yap.yapcore.helpers.extentions.share
import co.yap.yapcore.interfaces.OnItemClickListener

class PhoneContactFragment : Y2YBaseFragment<IPhoneContact.ViewModel>(), IPhoneContact.View {
    private lateinit var skeleton: Skeleton
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_phone_contacts
    override val viewModel: PhoneContactViewModel
        get() = ViewModelProvider(this).get(PhoneContactViewModel::class.java)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
        setObservers()
    }

    private fun initComponents() {
        viewModel.adaptor.setItemListener(listener)
        skeleton = getBinding().recycler.applySkeleton(
            R.layout.layout_item_contacts_shimmer,
            5
        )
    }

    private fun setObservers() {
        viewModel.state.stateLiveData?.observe(this, Observer { handleShimmerState(it) })
        viewModel.parentViewModel?.yapContactLiveData?.observe(this, Observer {
            viewModel.adaptor.setList(it)
            if (!it.isNullOrEmpty())
                viewModel.adaptor.filter.filter(viewModel.parentViewModel?.searchQuery?.value)
            viewModel.state.stateLiveData?.value =
                if (it.isNullOrEmpty()) State.error(null) else State.success(null)
        })
        viewModel.parentViewModel?.searchQuery?.observe(this, Observer {
            viewModel.adaptor.filter.filter(it)
        })
        viewModel.adaptor.filterCount.observe(this, Observer {
            if (it == 0 && viewModel.parentViewModel?.isSearching?.value == true && !viewModel.state.isNoContacts.get()) {
                viewModel.state.stateLiveData?.value = State.empty(null)
            } else {
                viewModel.state.isNoSearchResult.set(false)
            }
            viewModel.state.isShowContactsCounter.set(it != 0)
        })
    }

    val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when (view.id) {
                R.id.userPackageType -> {

                }
                R.id.tvInvite -> {
                    InviteFriendRepository().inviteAFriend()
                    requireContext().share(text = getBody(requireContext(), (data as Contact)))
                }
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
                viewModel.state.isNoContacts.set(true)
                viewModel.state.isShowContactsCounter.set(false)
            }
            Status.SUCCESS -> {
                viewModel.state.isNoSearchResult.set(false)
                viewModel.state.isShowContactsCounter.set(true)
                skeleton.showOriginal()
            }
            else -> {
                skeleton.showOriginal()
            }
        }
    }

    private fun getBinding(): FragmentPhoneContactsBinding {
        return (viewDataBinding as FragmentPhoneContactsBinding)
    }
}