package co.yap.modules.dashboard.more.bankdetails.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.more.bankdetails.interfaces.IBankDetail
import co.yap.modules.dashboard.more.bankdetails.viewmodel.BankDetailViewModel
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.helpers.ImageBinding
import co.yap.yapcore.helpers.extentions.share
import co.yap.yapcore.managers.SessionManager
import kotlinx.android.synthetic.main.activity_bank_detail.*


class BankDetailActivity : BaseBindingActivity<IBankDetail.ViewModel>(), IBankDetail.View {

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_bank_detail

    override val viewModel: IBankDetail.ViewModel
        get() = ViewModelProviders.of(this).get(BankDetailViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.imgProfile -> {

                }
                R.id.btnConfirm -> {
                    context.share(text = getBody(),title = "Share")
                }
            }
        })
    }

    private fun getBody(): String {
        return "Name: ${viewModel.state.name.get()}\n" +
                "SWIFT/BIC: ${viewModel.state.swift.get()}\n" +
                "IBAN: ${viewModel.state.iban.get()}\n" +
                "Account: ${viewModel.state.account.get()}\n" +
                "Bank: ${viewModel.state.bank.get()}\n" +
                "Address: ${viewModel.state.addresse.get()}\n"
    }

    override fun onDestroy() {
        viewModel.clickEvent.removeObservers(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        SessionManager.user?.let { user ->
            ImageBinding.loadAvatar(
                imgProfile,
                user.currentCustomer.getPicture(),
                user.currentCustomer.getFullName(),
                user.currentCustomer.parsedColor
            )

        }

    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> {
                this.finish()
            }
            R.id.ivRightIcon -> {
                this.startActivity(MoreActivity.newIntent(this))
            }
        }
    }
}
