package co.yap.modules.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.transactions.responsedtos.CardStatement
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingActivity
import co.yap.yapcore.R
import co.yap.yapcore.helpers.extentions.ExtraType
import co.yap.yapcore.helpers.extentions.deleteTempFolder
import co.yap.yapcore.helpers.extentions.getValue
import co.yap.yapcore.interfaces.BackPressImpl
import com.pdfview.PDFView

class PDFActivity : BaseBindingActivity<IPDFActivity.ViewModel>(), IPDFActivity.View {

    companion object {
        private const val URL = "URL"
        private const val CROSS_VISIBILITY = "CROSS_VISIBILITY"
        private const val CARD_STATEMENT = "CARD_STATEMENT"
        fun newIntent(context: Context, url: String, bool: Boolean): Intent {
            val intent = Intent(context, PDFActivity::class.java)
            intent.putExtra(URL, url)
            intent.putExtra(CROSS_VISIBILITY, bool)
            return intent
        }

        fun newIntent(
            context: Context,
            bool: Boolean,
            cardStatement: CardStatement? = null
        ): Intent {
            val intent = Intent(context, PDFActivity::class.java)
            intent.putExtra(URL, cardStatement?.statementURL)
            intent.putExtra(CROSS_VISIBILITY, bool)
            intent.putExtra(CARD_STATEMENT, cardStatement)
            return intent
        }
    }

    override val viewModel: IPDFActivity.ViewModel
        get() = ViewModelProviders.of(this).get(PDFViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_pdf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupData()
        viewModel.clickEvent.observe(this, listener)
    }

    private fun setupData() {
        val visibility = intent?.getValue(CROSS_VISIBILITY, ExtraType.BOOLEAN.name) as? Boolean
        viewModel.state.cardStatement?.set(
            intent?.getValue(
                CARD_STATEMENT,
                ExtraType.PARCEABLE.name
            ) as? CardStatement
        )
        if (viewModel.state.cardStatement?.get()?.sendEmail == true) viewModel.state.toolBarTitle?.set(
            "${viewModel.state.cardStatement?.get()?.month} ${viewModel.state.cardStatement?.get()?.year} statement"
        )
        viewModel.state.hideCross = visibility
        val url = intent?.getValue(URL, ExtraType.STRING.name) as? String
        url?.let {
            viewModel.downloadFile(it) { file ->
                file?.let {
                    val pdfView = findViewById<PDFView>(R.id.pdfView)
                    pdfView.fromFile(file).show()
                }
            }
        } ?: close()
    }

    val listener = Observer<Int> {
        when (it) {
            R.id.ivCancelPdf -> {
                onBackPressed()
            }
            R.id.btnSendEmail -> {
                viewModel.requestSendEmail(viewModel.state.cardStatement?.get())
            }

        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
        if (!BackPressImpl(fragment).onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun close() {
        showToast("Invalid file")
        finish()
    }

    override fun onDestroy() {
        context.deleteTempFolder()
        super.onDestroy()
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                finish()
            }
        }
    }
}

