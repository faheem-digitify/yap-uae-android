package co.yap.yapcore.adapters


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import co.yap.yapcore.constants.Constants.INDEX
import java.util.*

class SectionsPagerAdapter  constructor(
    private val mContext: FragmentActivity,
    fm: FragmentManager
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentInfoList: MutableList<FragmentInfo> = ArrayList();

//
//    fun addFragmentInfo(fragmentName: String, title: String = "", bundle: Bundle = Bundle()) {
//        addFragmentInfo(fragmentName, title, 0, bundle)
//    }

    inline fun <reified T : Fragment> addFragmentInfo(
        title: String = "",
        bundle: Bundle = Bundle()
    ) {
        addFragmentInfo(T::class.java.name, title, 0, bundle)
    }

    fun addFragmentInfo(fragmentName: String, title: String, icon: Int, bundle: Bundle?) {
        var bundle = bundle
        if (bundle == null) bundle = Bundle()

        bundle.putInt(INDEX, mFragmentInfoList.size)
        mFragmentInfoList.add(FragmentInfo(fragmentName, title, icon, bundle))
    }

    override fun getItem(position: Int): Fragment {
        val info = getFragmentInfo(position)
        return Fragment.instantiate(
            mContext,
            info.fragmentName, info.args
        )
    }

    fun getFragmentInfo(position: Int): FragmentInfo {
        return mFragmentInfoList[position]
    }

    override fun getCount(): Int {
        return mFragmentInfoList.size
    }

//    override fun getItemPosition(fragment: T): Int {
//
//        val fragment = `object` as BaseViewModelFragment<*, *>
//        //        if (fragment != null && fragment instanceof UpdateableFragment) {
//        //            ((UpdateableFragment) fragment).update();
//        //        }
//        return super.getItemPosition(`object`)
//    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentInfoList[position].title
    }

    fun getTabIndex(fragmentName: String): Int {
        if (TextUtils.isEmpty(fragmentName)) {
            return -1
        }
        for (i in mFragmentInfoList.indices) {
            val info = mFragmentInfoList[i]
            if (info.fragmentName == fragmentName) {
                return i
            }
        }
        return -1
    }

    data class FragmentInfo(
        internal var fragmentName: String,
        internal var title: String,
        internal var icon: Int,
        internal var args: Bundle
    )

}