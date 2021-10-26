package co.yap.modules.dashboard.cards.paymentcarddetail.statments.activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.cards.paymentcarddetail.statments.adaptor.CardStatementsAdaptor
import co.yap.modules.dashboard.cards.paymentcarddetail.statments.interfaces.ICardStatments
import co.yap.modules.dashboard.cards.paymentcarddetail.statments.viewmodels.CardStatementsViewModel
import co.yap.modules.pdf.PDFActivity
import co.yap.networking.transactions.responsedtos.CardStatement
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.interfaces.OnItemClickListener

class CardStatementsActivity : BaseBindingActivity<ICardStatments.ViewModel>(),
    ICardStatments.View {

    override val viewModel: ICardStatments.ViewModel
        get() = ViewModelProviders.of(this).get(CardStatementsViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_card_statements

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.card = intent.getParcelableExtra("card")
        val isFromDrawer = intent.getBooleanExtra("isFromDrawer", false)
        if (isFromDrawer) {
            viewModel.state.statementType.set("EMAIL_ME_ACCOUNT")
            viewModel.loadStatementsFromDashBoard()
        } else {
            viewModel.loadStatements(viewModel.card.cardSerialNumber)
            viewModel.state.statementType.set("EMAIL_ME_CARD")
        }
        viewModel.adapter.set(CardStatementsAdaptor(mutableListOf()))
        viewModel.adapter.get()?.allowFullItemClickListener = true
        viewModel.adapter.get()?.setItemListener(listener)
    }


    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> onBackPressed()
        }
    }

    val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            startActivity(
                PDFActivity.newIntent(
                    view.context,
                    false,
                    (data as CardStatement).also {
                        it.statementType = viewModel.state.statementType.get()
                        it.sendEmail =true
                    }
                )
            )
        }
    }
}
