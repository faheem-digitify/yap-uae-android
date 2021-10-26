package co.yap.sendmoney.home.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import co.yap.sendmoney.BR
import co.yap.sendmoney.R
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.IFragmentHolder
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.defaults.DefaultNavigator
import co.yap.yapcore.defaults.INavigator
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.interfaces.BackPressImpl
import co.yap.yapcore.interfaces.IBaseNavigator

class SMBeneficiaryParentActivity : BaseBindingActivity<ISMBeneficiaryParent.ViewModel>(),
    INavigator,
    IFragmentHolder {


    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_sm_beneficiary_parent

    override val viewModel: ISMBeneficiaryParent.ViewModel
        get() = ViewModelProviders.of(this).get(SMBeneficiaryParentViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(ExtraKeys.SEND_MONEY_TYPE.name)) {
            viewModel.state.sendMoneyType?.value = intent.getStringExtra(ExtraKeys.SEND_MONEY_TYPE.name)
        }
    }

    override val navigator: IBaseNavigator
        get() = DefaultNavigator(
            this@SMBeneficiaryParentActivity,
            R.id.sm_beneficiary_parent_nav_host_fragment
        )

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.sm_beneficiary_parent_nav_host_fragment)
        if (!BackPressImpl(fragment).onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCodes.REQUEST_NOTIFY_BENEFICIARY_LIST -> {
                    if (data?.getBooleanExtra(Constants.BENEFICIARY_CHANGE, false) == false) {
                        setResultData()
                    }
                }
                RequestCodes.REQUEST_TRANSFER_MONEY -> {
                    if (data?.getBooleanExtra(Constants.MONEY_TRANSFERED, false) == true) {
                        setResultData()
                    }
                }
            }

        }
    }

    private fun setResultData() {
        val intent = Intent()
        intent.putExtra(Constants.MONEY_TRANSFERED, true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
