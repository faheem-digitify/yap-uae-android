package co.yap.modules.dashboard.transaction.receipt.previewer

import android.app.Application
import android.os.Build
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.helpers.extentions.sizeInMb
import id.zelory.compressor.Compressor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PreviewTransactionReceiptViewModel(application: Application) :
    BaseViewModel<IPreviewTransactionReceipt.State>(application),
    IPreviewTransactionReceipt.ViewModel {
    override val state = PreviewTransactionReceiptState()
    override val clickEvent = SingleClickEvent()
    override var transactionId: String = ""
    val repository: TransactionsRepository = TransactionsRepository

    override fun handlePressOnView(id: Int) {
        clickEvent.postValue(id)
    }

    override fun saveTransactionReceipt(file: File, success: () -> Unit) {
        launch {
            if (file.sizeInMb() < 25) {
                state.loading = true
                val reqFile =
                    RequestBody.create(MediaType.parse("image/${file.extension}"), file)
                val multiPartImageFile: MultipartBody.Part =
                    MultipartBody.Part.createFormData("receipt-image", file.name, reqFile)
                when (val response =
                    repository.addTransactionReceipt(transactionId, multiPartImageFile)) {
                    is RetroApiResponse.Success -> {
                        response.data.let { resp ->
                            success()
                        }
                        state.loading = false

                    }
                    is RetroApiResponse.Error -> {
                        state.loading = false
                        state.toast = response.error.message
                    }
                }
            }
        }

    }

    override fun requestSavePicture(
        actualFile: File,
        success: () -> Unit
    ) {
        launch {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                Compressor.compress(context, actualFile) {
                    saveTransactionReceipt(actualFile) {
                        success()
                    }
                }
            } else {
                saveTransactionReceipt(actualFile) {
                    success()
                }
            }
        }
    }
}
