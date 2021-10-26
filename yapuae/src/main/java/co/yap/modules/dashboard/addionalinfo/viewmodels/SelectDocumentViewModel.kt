package co.yap.modules.dashboard.addionalinfo.viewmodels

import android.app.Application
import android.os.Build
import co.yap.yapuae.R
import co.yap.modules.dashboard.addionalinfo.adapters.UploadAdditionalDocumentAdapter
import co.yap.modules.dashboard.addionalinfo.interfaces.ISelectDocument
import co.yap.modules.dashboard.addionalinfo.states.SelectDocumentState
import co.yap.networking.customers.CustomersRepository
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.networking.customers.requestdtos.UploadAdditionalInfo
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.translation.Strings
import co.yap.widgets.bottomsheet.BottomSheetItem
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.helpers.extentions.sizeInMb
import co.yap.yapcore.managers.SessionManager
import id.zelory.compressor.Compressor
import java.io.File
import java.util.*

class SelectDocumentViewModel(application: Application) :
    AdditionalInfoBaseViewModel<ISelectDocument.State>(application),
    ISelectDocument.ViewModel, IRepositoryHolder<CustomersRepository> {
    override val repository: CustomersRepository = CustomersRepository
    override val uploadAdditionalDocumentAdapter: UploadAdditionalDocumentAdapter =
        UploadAdditionalDocumentAdapter(context, mutableListOf())

    override fun moveToNext() {
        moveStep()
    }

    override val state: ISelectDocument.State = SelectDocumentState(application)
    override fun onCreate() {
        super.onCreate()
        state.title.set(getString(Strings.screen_additional_info_label_text_additional_info))
        uploadAdditionalDocumentAdapter.setList(getDocumentList())
        setEnabled(uploadAdditionalDocumentAdapter.getDataList()) {
            setSubTitle(it)
        }
        if (parentViewModel?.stepCount?.value == 2) {
            parentViewModel?.state?.buttonTitle?.set(getString(Strings.screen_add_beneficiary_button_next))
        } else {
            parentViewModel?.state?.buttonTitle?.set(getString(Strings.common_button_submit))
        }
    }


    override fun uploadDocument(
        file: File,
        documentType: String,
        contentType: String?,
        success: (Boolean) -> Unit
    ) {

        launch {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                Compressor.compress(context, file) {
                    upload(file, documentType, contentType = contentType) { isUploadSuccessfully ->
                        success(isUploadSuccessfully)
                    }
                }
            } else {
                upload(file, documentType, contentType = contentType) { isUploadSuccessfully ->
                    success(isUploadSuccessfully)
                }
            }
        }
    }

    override fun setEnabled(list: List<AdditionalDocument>, isUploaded: (Boolean) -> Unit) {
        val list = list.filter { additionalDocument -> additionalDocument.status == "PENDING" }
        state.valid.set(list.isEmpty())
        isUploaded.invoke(list.isEmpty())
    }

    private fun upload(
        file: File,
        documentType: String,
        contentType: String? = null,
        success: (Boolean) -> Unit
    ) {
        launch {
            if (file.sizeInMb() < 25) {
                state.loading = true
                when (val response = repository.uploadAdditionalDocuments(
                    UploadAdditionalInfo(
                        files = file,
                        documentType = documentType, contentType = contentType
                    )
                )) {
                    is RetroApiResponse.Success -> {
                        state.loading = false
                        success(true)
                    }
                    is RetroApiResponse.Error -> {
                        showToast(response.error.message)
                        state.loading = false
                        success(false)
                    }
                }
            } else {
                showToast("Your file size is too big. Please upload a file less than 25MB to proceed")
                success(false)
            }
        }

    }

    override fun setSubTitle(isUploaded: Boolean) {
        if (isUploaded) {
            state.subTitle.set(getString(Strings.screen_additional_info_label_text_upload_document_complete))
        } else {
            state.subTitle.set(
                SessionManager.user?.currentCustomer?.firstName + getString(
                    Strings.screen_additional_info_label_text_upload_document
                )
            )
        }
    }

    override fun getUploadDocumentOptions(isShowRemovePhoto: Boolean): ArrayList<BottomSheetItem> {
        val list = arrayListOf<BottomSheetItem>()
        list.add(
            BottomSheetItem(
                icon = R.drawable.ic_camera,
                title = getString(Strings.screen_update_profile_photo_display_text_open_camera),
                subTitle = getString(Strings.screen_upload_documents_display_sheet_text_scan_single_document),
                tag = PhotoSelectionType.CAMERA.name
            )
        )
        list.add(
            BottomSheetItem(
                icon = R.drawable.ic_file_manager,
                title = getString(Strings.screen_upload_documents_display_sheet_text_upload_from_files),
                subTitle = getString(Strings.screen_upload_documents_display_sheet_text_upload_from_files_descriptions),
                tag = PhotoSelectionType.GALLERY.name
            )
        )
        if (isShowRemovePhoto)
            list.add(
                BottomSheetItem(
                    icon = R.drawable.ic_remove,
                    title = getString(Strings.screen_update_profile_photo_display_text_remove_photo),
                    tag = PhotoSelectionType.REMOVE_PHOTO.name
                )
            )

        return list
    }

}
