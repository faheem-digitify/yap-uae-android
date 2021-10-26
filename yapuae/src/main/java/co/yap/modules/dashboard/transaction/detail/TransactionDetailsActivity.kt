package co.yap.modules.dashboard.transaction.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ActivityTransactionDetailsBinding
import co.yap.modules.dashboard.transaction.category.TransactionCategoryFragment
import co.yap.modules.dashboard.transaction.feedback.TransactionFeedbackFragment
import co.yap.modules.dashboard.transaction.receipt.add.AddTransactionReceiptFragment
import co.yap.modules.dashboard.transaction.receipt.previewer.PreviewTransactionReceiptFragment
import co.yap.modules.dashboard.transaction.receipt.viewer.ImageViewerActivity
import co.yap.modules.dashboard.transaction.totalpurchases.TotalPurchaseFragment
import co.yap.modules.others.note.activities.TransactionNoteActivity
import co.yap.networking.transactions.responsedtos.ReceiptModel
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.networking.transactions.responsedtos.transaction.Transaction
import co.yap.translation.Strings
import co.yap.widgets.bottomsheet.BottomSheetItem
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingImageActivity
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.constants.Constants.FILE_PATH
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.enums.TxnType
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.helpers.showReceiptSuccessDialog
import co.yap.yapcore.interfaces.OnItemClickListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_transaction_details.*
import pl.aprilapps.easyphotopicker.MediaFile

class TransactionDetailsActivity : BaseBindingImageActivity<ITransactionDetails.ViewModel>(),
    ITransactionDetails.View, OnMapReadyCallback {

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_transaction_details
    override val viewModel: ITransactionDetails.ViewModel
        get() = ViewModelProviders.of(this).get(TransactionDetailsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        //setContentDataColor(viewModel.transaction.get())
        viewModel.setContentDataColor(
            viewModel.transaction.get(),
            getBindings().tvTotalAmountValue,
            getBindings().tvCurrency
        )
        viewModel.state.transactionData.get()?.isMApVisible?.let { showMAp ->
            if (showMAp) initMap()
            viewModel.setMapVisibility(getBindings().ivMap, map, showMAp)
        }
    }

    override fun setObservers() {
        viewModel.clickEvent.observe(this, clickEvent)
        if (intent?.hasExtra(ExtraKeys.TRANSACTION_OBJECT_STRING.name) == true) {
            intent.getParcelableExtra<Transaction>(ExtraKeys.TRANSACTION_OBJECT_STRING.name)?.let {
                viewModel.transaction.set(it)
                viewModel.composeTransactionDetail(it)
                getBindings().ivMap.setImageResource(viewModel.state.coverImage.get())
                viewModel.setMerchantImage(getBindings().ivPicture)
            }
        }
        viewModel.responseReciept.observe(this, Observer {
            viewModel.setAdapterList(it)
        })
        viewModel.adapter.setItemListener(onReceiptClickListener)
        getBindings().layoutRating.rbMarchant.setOnRatingBarChangeListener { ratingBar, fl, b ->
            //  showRatingDialogue()
        }
    }


    private val onReceiptClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when (data) {
                is BottomSheetItem -> handleReceiptOptionClick(data)
                is ReceiptModel -> openAddedReceipt(data)
            }
        }
    }

    var clickEvent = Observer<Int> {
        when (it) {
            R.id.clNote, R.id.clEditIcon ->
                if (viewModel.state.txnNoteValue.get().isNullOrBlank()) {
                    openNoteScreen()
                } else
                    openNoteScreen(noteValue = viewModel.state.txnNoteValue.get() ?: "")

            R.id.clRecipt -> {
                showAddReceiptOptions()
            }
            R.id.tvTapToChange -> {
                updateCategory()
            }
            R.id.tvImproveLogo -> {
                startFragmentForResult<TransactionFeedbackFragment>(
                    TransactionFeedbackFragment::class.java.name, bundleOf(
                        Constants.FEEDBACK_LOCATION to viewModel.state.transactionData.get()?.locationValue,
                        Constants.FEEDBACK_TITLE to viewModel.state.transactionData.get()?.transactionTitle,
                        Constants.TRANSACTION_DETAIL to viewModel.transaction.get()
                    )
                ) { resultCode, _ ->
                    if (resultCode == Activity.RESULT_OK) {
                    }
                    makeToast(this, "feedback submitted successfully", Toast.LENGTH_SHORT)
                }
            }
            R.id.ivTotalPurchase -> {
                startFragment<TotalPurchaseFragment>(
                    TotalPurchaseFragment::class.java.name, bundle = bundleOf(
                        Constants.TRANSACTION_COUNT to viewModel.totalPurchase.get()?.txnCount,
                        Constants.TRANSACTION_DETAIL to viewModel.transaction.get(),
                        Constants.TOTAL_TRANSACTION to viewModel.totalPurchase.get()?.totalSpendAmount
                    )
                )
            }
        }
    }

    private fun updateCategory() {
        startFragmentForResult<TransactionCategoryFragment>(
            TransactionCategoryFragment::class.java.name,
            bundleOf(
                Constants.TRANSACTION_ID to viewModel.transaction.get()?.transactionId,
                Constants.PRE_SELECTED_CATEGORY to viewModel.state.updatedCategory.get()?.categoryName
            )
        ) { resultCode, data ->
            if (resultCode == Activity.RESULT_OK) {
                val category =
                    data?.getValue(Constants.UPDATED_CATEGORY, "PARCEABLE") as TapixCategory
                viewModel.state.updatedCategory.set(category)
                viewModel.state.categoryDescription.set(viewModel.state.updatedCategory.get()?.description)
                makeToast(this, "category updated sucessfully", LENGTH_SHORT)
            }
        }
    }

    private fun showAddReceiptOptions() {
        launchSheet(
            itemClickListener = onReceiptClickListener,
            itemsList = viewModel.getAddReceiptOptions(),
            heading = getString(Strings.screen_transaction_details_display_sheet_heading),
            subHeading = getString(Strings.screen_transaction_details_display_sheet_sub_heading)
        )
    }

    private fun handleReceiptOptionClick(bottomSheetItem: BottomSheetItem) {
        when (bottomSheetItem.tag) {
            PhotoSelectionType.CAMERA.name -> {
                startFragmentForResult<AddTransactionReceiptFragment>(
                    fragmentName = AddTransactionReceiptFragment::class.java.name,
                    bundle = bundleOf(ExtraKeys.TRANSACTION_ID.name to viewModel.transaction.get()?.transactionId)
                ) { resultCode, _ ->
                    if (resultCode == Activity.RESULT_OK)
                        showAddReceiptSuccessDialog()
                }
            }
            PhotoSelectionType.GALLERY.name -> openImagePicker(PhotoSelectionType.GALLERY)
        }
    }

    private fun showAddReceiptSuccessDialog() {
        this.showReceiptSuccessDialog(
            description = getString(Strings.screen_transaction_details_receipt_success_label),
            addAnotherText = getString(Strings.screen_transaction_add_another_receipt),
            callback = {
                when (it) {
                    R.id.btnActionDone -> {
                        viewModel.requestAllApis()
                    }
                    R.id.tvAddAnother -> {
                        viewModel.requestAllApis()
                        showAddReceiptOptions()
                    }
                }
            }
        )
    }

    private fun openAddedReceipt(receiptModel: ReceiptModel) {
        launchActivity<ImageViewerActivity>(requestCode = RequestCodes.REQUEST_DELETE_RECEIPT) {
            putExtra(ExtraKeys.TRANSACTION_RECEIPT.name, receiptModel)
            putExtra(ExtraKeys.TRANSACTION_ID.name, viewModel.transaction.get()?.transactionId)
            putExtra(
                ExtraKeys.TRANSACTION_RECEIPT_LIST.name,
                viewModel.adapter.getDataList() as ArrayList<ReceiptModel>
            )
        }
    }

    private fun openNoteScreen(noteValue: String = "") {
        startActivityForResult(
            TransactionNoteActivity.newIntent(
                this,
                noteValue,
                viewModel.transaction.get()?.transactionId ?: "",
                viewModel.transaction.get()?.txnType ?: ""
            ), Constants.INTENT_ADD_NOTE_REQUEST
        )
    }

    /*private fun setContentDataColor(transaction: Transaction?) {
        //strike-thru textview
        transaction?.let {
            getBindings().tvTotalAmountValue.paintFlags =
                if (transaction.isTransactionRejected() || transaction.status == TransactionStatus.FAILED.name) getBindings().tvTotalAmountValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG else 0
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.INTENT_ADD_NOTE_REQUEST -> {
                    viewModel.state.txnNoteValue.set(
                        data?.getStringExtra(Constants.KEY_NOTE_VALUE).toString()
                    )
                    if (viewModel.transaction.get()?.txnType == TxnType.DEBIT.type) {
                        viewModel.transaction.get()?.transactionNote =
                            data?.getStringExtra(Constants.KEY_NOTE_VALUE).toString()
                        viewModel.transaction.get()?.receiverTransactionNoteDate =
                            DateUtils.getCurrentDateWithFormat(DateUtils.FORMAT_LONG_OUTPUT)
                    } else {
                        viewModel.transaction.get()?.receiverTransactionNote =
                            data?.getStringExtra(Constants.KEY_NOTE_VALUE).toString()
                        viewModel.transaction.get()?.receiverTransactionNoteDate =
                            DateUtils.getCurrentDateWithFormat(DateUtils.FORMAT_LONG_OUTPUT)
                    }
                    viewModel.state.transactionNoteDate = "Note added  ${
                    DateUtils.getCurrentDateWithFormat(
                        DateUtils.FORMAT_LONG_OUTPUT
                    )
                    }"
                }

                RequestCodes.REQUEST_DELETE_RECEIPT -> {
                    viewModel.requestAllApis()

                }
            }
        }
    }

    override fun onImageReturn(mediaFile: MediaFile) {
        startFragmentForResult<PreviewTransactionReceiptFragment>(
            fragmentName = PreviewTransactionReceiptFragment::class.java.name,
            bundle = bundleOf(
                FILE_PATH to mediaFile.file.absolutePath,
                ExtraKeys.TRANSACTION_ID.name to viewModel.transaction.get()?.transactionId
            )
        ) { resultCode, _ ->
            if (resultCode == Activity.RESULT_OK)
                showAddReceiptSuccessDialog()
        }
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivLeftIcon -> {
                setResult()
            }
        }
    }

    private fun setResult() {
        val intent = Intent()
        if (viewModel.transaction.get() is Transaction) {
            intent.putExtra(
                ExtraKeys.TRANSACTION_OBJECT_STRING.name,
                viewModel.transaction.get() as Transaction
            )
            intent.putExtra(
                ExtraKeys.TRANSACTION_OBJECT_GROUP_POSITION.name, getIntent().getIntExtra(
                    ExtraKeys.TRANSACTION_OBJECT_GROUP_POSITION.name, -1
                )
            )
            intent.putExtra(
                ExtraKeys.TRANSACTION_OBJECT_CHILD_POSITION.name, getIntent().getIntExtra(
                    ExtraKeys.TRANSACTION_OBJECT_CHILD_POSITION.name, -1
                )
            )

            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun removeObservers() {
        viewModel.clickEvent.removeObserver(clickEvent)
    }

    fun getBindings(): ActivityTransactionDetailsBinding =
        viewDataBinding as ActivityTransactionDetailsBinding

    override fun onBackPressed() {
        setResult()
    }

    override fun onDestroy() {
        removeObservers()
        super.onDestroy()
    }

    private fun showRatingDialogue() {
        this.showReceiptSuccessDialog(
            description = "Are you sure you want to submit this rating?",
            addOtherVisibility = false
        )
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.view?.visibility = View.VISIBLE
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        viewModel.gMap = googleMap
        viewModel.setMap()
    }

}
