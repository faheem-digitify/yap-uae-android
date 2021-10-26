package co.yap.modules.frame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import co.yap.localization.LocaleManager
import co.yap.yapcore.*
import co.yap.yapcore.constants.Constants.EXTRA
import co.yap.yapcore.constants.Constants.FRAGMENT_CLASS
import co.yap.yapcore.constants.Constants.SHOW_TOOLBAR
import co.yap.yapcore.constants.Constants.TOOLBAR_TITLE
import co.yap.yapcore.databinding.ActivityFrameBinding
import co.yap.yapcore.helpers.extentions.createFragmentInstance
import co.yap.yapcore.helpers.extentions.instantiateFragment
import kotlinx.android.synthetic.main.activity_frame.*

class FrameActivity : BaseBindingActivity<IFrameActivity.ViewModel>(),
    IFrameActivity.View, IFragmentHolder {

    private lateinit var fragment: BaseBindingFragment<*>
    override fun getBindingVariable() = BR.frameActivityViewModel
    override fun getLayoutId() = R.layout.activity_frame
    override val viewModel: IFrameActivity.ViewModel
        get() = ViewModelProviders.of(this).get(FrameActivityViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent
        if (extras == null) {
            finish()
            return
        }
        if (!extras.hasExtra(FRAGMENT_CLASS)) {
            finish()
            return
        }

        setupToolbar(
            if (extras.hasExtra(SHOW_TOOLBAR)) extras.getBooleanExtra(
                SHOW_TOOLBAR,
                false
            ) else false
        )

        viewModel.state.toolbarTitle =
            if (extras.hasExtra(TOOLBAR_TITLE)) extras.getStringExtra(TOOLBAR_TITLE) else ""
        val fragmentName = extras.getStringExtra(FRAGMENT_CLASS)
        if (fragmentName == null || TextUtils.isEmpty(fragmentName)) {
            finish()
            return
        }
        fragment = instantiateFragment<Fragment>(
            fragmentName
        ) as BaseBindingFragment<*>
        if (extras.hasExtra(EXTRA)) {
            createFragmentInstance(fragment, extras.getBundleExtra(EXTRA)!!)
        } else {
            createFragmentInstance(fragment, Bundle())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fragment.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        fragment.onBackPressed()
    }

    private fun setupToolbar(visibility: Boolean) {
        getBinding().toolbar?.let {
            toolbar?.title = ""
            toolbar?.visibility = (if (visibility) View.VISIBLE else View.GONE)
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
                setDisplayShowCustomEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_back_arrow_left)
                if (visibility) show() else hide()

            }
        }

    }

    fun getBinding() = viewDataBinding as ActivityFrameBinding


    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun getScreenName(): String? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }
}
