package co.yap.modules.others.note.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.R
import co.yap.modules.others.note.interfaces.ITransactionNote
import co.yap.modules.others.note.viewmodels.TransactionNoteViewModel
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.Utils
import kotlinx.android.synthetic.main.activity_transaction_note.*

class TransactionNoteActivity : BaseBindingActivity<ITransactionNote.ViewModel>() {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_transaction_note

    override val viewModel: ITransactionNote.ViewModel
        get() = ViewModelProviders.of(this).get(TransactionNoteViewModel::class.java)

    companion object {
        fun newIntent(
            context: Context,
            noteValue: String? = "",
            transactionId: String,
            TxnType: String
        ): Intent {
            val intent = Intent(context, TransactionNoteActivity::class.java)
            intent.putExtra(Constants.KEY_NOTE_VALUE, noteValue)
            intent.putExtra(Constants.TXN_TYPE, TxnType)
            intent.putExtra(Constants.TRANSACTION_ID, transactionId)
            return intent
        }
    }

    private fun getNoteValue(): String {
        return intent.getStringExtra(Constants.KEY_NOTE_VALUE)
    }

    private fun getTxnType(): String {
        return intent.getStringExtra(Constants.TXN_TYPE)
    }

    private fun getTransactionId(): String {
        return intent.getStringExtra(Constants.TRANSACTION_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        Utils.requestKeyboard(etNote, request = true, forced = true)
        if (intent.hasExtra(Constants.KEY_NOTE_VALUE)) {
            viewModel.txnType = getTxnType()
            viewModel.state.noteValue.set(getNoteValue())
            etNote.append(viewModel.state.noteValue.get())
        }
    }

    private fun setObservers() {
        viewModel.addEditNoteSuccess.observe(this, Observer {
            when (it) {
                true -> onAddEditNoteSuccess()
            }
        })
    }

    private fun onAddEditNoteSuccess() {
        val data = Intent()
        data.putExtra(Constants.KEY_NOTE_VALUE, viewModel.state.noteValue.get())
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> {
                hideKeyboard()
                finish()
            }
            R.id.tvRightText -> {
                viewModel.addEditNote(
                    transactionId = getTransactionId(),
                    transactionDetail = if (viewModel.txnType == TxnType.DEBIT.type) viewModel.state.noteValue.get() else null,
                    receiverNote = if (viewModel.txnType == TxnType.CREDIT.type) viewModel.state.noteValue.get() else null
                )
            }
        }
    }

}
