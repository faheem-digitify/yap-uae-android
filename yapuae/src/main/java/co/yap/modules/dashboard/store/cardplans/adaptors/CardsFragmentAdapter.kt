package co.yap.modules.dashboard.store.cardplans.adaptors

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import co.yap.modules.dashboard.store.cardplans.fragments.MetalCardFragment
import co.yap.modules.dashboard.store.cardplans.fragments.PrimeCardFragment
import co.yap.sendmoney.y2y.home.phonecontacts.PhoneContactFragment
import co.yap.sendmoney.y2y.home.yapcontacts.YapContactsFragment
import co.yap.yapcore.constants.Constants

class CardsFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        getFragmentIndex(Constants.PRIME_CARD_PLAN) to { PrimeCardFragment() },
        getFragmentIndex(Constants.METAL_CARD_PLAN) to { MetalCardFragment() }
    )

    private fun getFragmentIndex(cardPlan: String): Int {
        return when (cardPlan) {
            Constants.PRIME_CARD_PLAN -> 0
            Constants.METAL_CARD_PLAN -> 1
            else -> 0
        }
    }

    override fun getItemCount() = tabFragmentsCreators.size
    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}