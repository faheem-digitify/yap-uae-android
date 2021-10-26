package co.yap.household.onboard.otherscreens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import co.yap.household.R
import co.yap.modules.dummy.ActivityNavigator
import co.yap.translation.Strings
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.defaults.DefaultActivity
import co.yap.yapcore.managers.SessionManager
import kotlinx.android.synthetic.main.activity_eidnot_accepted.*


class InvalidEIDActivity : DefaultActivity(), IFragmentHolder {
    private var mNavigator: ActivityNavigator? = null

    companion object {
        const val BUNDLE_DATA = "bundle_data"
        const val EXISTING_USER = "existingYapUser"
        const val USER_INFO = "user_info"
        fun getIntent(context: Context, bundle: Bundle = Bundle()): Intent {
            val intent = Intent(context, InvalidEIDActivity::class.java)
            intent.putExtra(BUNDLE_DATA, bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eidnot_accepted)
        tvEID_NotAcceptMessage.text =
            getString(Strings.screen_house_hold_existing_yap_message).format(
                SessionManager.user?.currentCustomer?.getFullName()
            )
        setClickerListner()

    }

    /*
    * In this function set onClicker listener.
    * */
    private fun setClickerListner() {
        btnEID_NotAccept.setOnClickListener {
            SessionManager.doLogout(this)
            finish()
        }

//        tvEID_NotAcceptNumber.setOnClickListener {
//            userCall()
//        }
    }

}
