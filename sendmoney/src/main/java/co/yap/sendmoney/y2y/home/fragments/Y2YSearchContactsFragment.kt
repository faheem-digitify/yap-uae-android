package co.yap.sendmoney.y2y.home.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.sendmoney.R
import co.yap.sendmoney.databinding.FragmentY2YSearchContactsBinding
import co.yap.sendmoney.y2y.home.interfaces.IY2YSearchContacts
import co.yap.sendmoney.y2y.home.phonecontacts.PhoneContactFragment
import co.yap.sendmoney.y2y.home.viewmodel.Y2YSearchContactsViewModel
import co.yap.sendmoney.y2y.home.yapcontacts.YapContactsFragment
import co.yap.sendmoney.y2y.main.fragments.Y2YBaseFragment
import co.yap.translation.Strings
import co.yap.yapcore.BR
import co.yap.yapcore.adapters.SectionsPagerAdapter
import co.yap.yapcore.helpers.extentions.afterTextChanged
import co.yap.yapcore.helpers.extentions.hideKeyboard
import kotlinx.android.synthetic.main.fragment_y_2_y_search_contacts.view.*

class Y2YSearchContactsFragment : Y2YBaseFragment<IY2YSearchContacts.ViewModel>() {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_y_2_y_search_contacts

    override val viewModel: Y2YSearchContactsViewModel
        get() = ViewModelProviders.of(this).get(Y2YSearchContactsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.clickEvent.observe(this, clickEventObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        setupAdaptor()
    }

    private fun setupSearch() {
        getBindingView().lySearchView.etSearch.afterTextChanged {
            viewModel.parentViewModel?.searchQuery?.value = it
        }
        viewModel.parentViewModel?.isSearching?.value = true
    }

    private fun setupAdaptor() {
        val adapter = SectionsPagerAdapter(requireActivity(), childFragmentManager)
        adapter.addFragmentInfo<YapContactsFragment>(getString(Strings.screen_y2y_display_button_yap_contacts))
        adapter.addFragmentInfo<PhoneContactFragment>(getString(Strings.screen_y2y_display_button_all_contacts))
        getBindingView().viewPager.adapter = adapter
        viewModel.parentViewModel?.selectedTabPos?.value?.let {
            getBindingView().viewPager.currentItem = it
        }
    }

    private val clickEventObserver = Observer<Int> {
        when (it) {
            R.id.tvCancel -> {
                activity?.onBackPressed()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.parentViewModel?.selectedTabPos?.value =
            getBindingView().tabLayout.selectedTabPosition
        viewModel.parentViewModel?.searchQuery?.value = ""
        getBindingView().lySearchView.etSearch.hideKeyboard()
        viewModel.parentViewModel?.isSearching?.value = false
        return super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clickEvent.removeObservers(this)
    }

    private fun getBindingView(): FragmentY2YSearchContactsBinding {
        return (viewDataBinding as FragmentY2YSearchContactsBinding)
    }
}