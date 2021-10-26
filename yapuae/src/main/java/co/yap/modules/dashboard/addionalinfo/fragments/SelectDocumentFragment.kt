package co.yap.modules.dashboard.addionalinfo.fragments

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.addionalinfo.interfaces.ISelectDocument
import co.yap.modules.dashboard.addionalinfo.model.AdditionalDocumentImage
import co.yap.modules.dashboard.addionalinfo.viewmodels.SelectDocumentViewModel
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.translation.Strings
import co.yap.widgets.bottomsheet.BottomSheetItem
import co.yap.yapcore.enums.AdditionalInfoScreenType
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.helpers.FileUtils
import co.yap.yapcore.helpers.extentions.launchSheet
import co.yap.yapcore.helpers.extentions.openFilePicker
import co.yap.yapcore.helpers.extentions.startFragmentForResult
import co.yap.yapcore.helpers.permissions.PermissionHelper
import co.yap.yapcore.interfaces.OnItemClickListener
import pl.aprilapps.easyphotopicker.EasyImage
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class SelectDocumentFragment : AdditionalInfoBaseFragment<ISelectDocument.ViewModel>(),
    ISelectDocument.View {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_select_document
    private val takePhoto = 1
    private val pickPhoto = 2
    internal var permissionHelper: PermissionHelper? = null
    private var currentPos: Int? = null
//    lateinit var easyImage: EasyImage
    private var currentDocument: AdditionalDocument? = null

    override val viewModel: SelectDocumentViewModel
        get() = ViewModelProviders.of(this).get(SelectDocumentViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showHeader(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        viewModel.uploadAdditionalDocumentAdapter.allowFullItemClickListener = true
        viewModel.uploadAdditionalDocumentAdapter.setItemListener(listener)
    }

    private val listener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            if (data is AdditionalDocument) {
                currentDocument = data
                currentPos = pos
                checkPermission(data.name)
            }
        }
    }

    private fun openBottomSheet(name: String?) {
        this.fragmentManager?.let {
            requireActivity().launchSheet(
                itemClickListener = itemListener,
                itemsList = viewModel.getUploadDocumentOptions(false),
                heading = name + " " + getString(Strings.common_display_text_copy),
                subHeading = getString(Strings.screen_additional_info_label_text_bottom_sheet_des) + " " + name
            )
        }
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.btnNext -> {
                if (viewModel.getScreenType() == AdditionalInfoScreenType.BOTH_SCREENS.name) {
                    viewModel.moveToNext()
                    navigate(R.id.action_selectDocumentFragment_to_additionalInfoQuestion)
                } else {
                    viewModel.parentViewModel?.submitAdditionalInfo {
                        navigate(R.id.action_selectDocumentFragment_to_additionalInfoComplete)
                    }
                }
            }
            R.id.tvDoItLater -> {
                requireActivity().finish()
            }
        }
    }

    private val itemListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when ((data as BottomSheetItem).tag) {
                PhotoSelectionType.CAMERA.name -> {
                    openScanDocumentFragment(currentDocument?.name ?: "")
                }

                PhotoSelectionType.GALLERY.name -> {
                    requireActivity().openFilePicker("File picker",
                        completionHandler = { _, dataUri ->
                            dataUri?.let { uriIntent ->
                                uploadDocumentAndMoveNext(
                                    FileUtils.getFile(requireContext(), uriIntent.data),
                                    currentPos ?: 0,
                                    currentDocument ?: AdditionalDocument(),
                                    isFromCamera = false,
                                    contentType = requireContext().contentResolver.getType(
                                        uriIntent.data ?: Uri.EMPTY
                                    )
                                )
                            }
                        })
                }

                PhotoSelectionType.REMOVE_PHOTO.name -> {

                }

            }
        }
    }

    private fun openScanDocumentFragment(fileName: String) {
        startFragmentForResult<AdditionalInfoScanDocumentFragment>(
            fragmentName = AdditionalInfoScanDocumentFragment::class.java.name,
            bundle = bundleOf(
                AdditionalDocumentImage::class.java.name to AdditionalDocumentImage(
                    name = fileName,
                    file = File(fileName)
                )
            )
        ) { resultCode, intent ->
            if (resultCode == Activity.RESULT_OK) {
                intent?.let {
                    val file: File? = it.extras?.get("file") as File
                    uploadDocumentAndMoveNext(
                        file,
                        currentPos ?: 0,
                        currentDocument ?: AdditionalDocument(), isFromCamera = true,
                        contentType = null
                    )
                }
            }
        }
    }

    private fun uploadDocumentAndMoveNext(
        file: File?,
        pos: Int,
        data: AdditionalDocument,
        isFromCamera: Boolean = false,
        contentType: String? = null
    ) {
        file?.let {
            viewModel.uploadDocument(
                file,
                data.documentType ?: "", contentType = contentType
            ) { isUploadedSuccessfully ->
                if (isFromCamera) file.deleteRecursively()
                if (isUploadedSuccessfully) {
                    if (data.status == "PENDING")
                        data.status = "DONE"
                    viewModel.uploadAdditionalDocumentAdapter.setItemAt(
                        pos,
                        data
                    )
                    viewModel.setEnabled(viewModel.uploadAdditionalDocumentAdapter.getDataList()) {
                        viewModel.setSubTitle(it)
                    }
                }
            }
        } ?: showToast("Invalid Image")
    }

    private fun checkPermission(data: String? = "") {
        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), 100
        )
        permissionHelper?.request(object : PermissionHelper.PermissionCallback {
            override fun onPermissionGranted() {
                openBottomSheet(data)
            }

            override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
            }

            override fun onPermissionDenied() {
            }

            override fun onPermissionDeniedBySystem() {
            }
        })
    }


    /*private fun initEasyImage(type: Int) {
        if (hasCameraPermission()) {
            easyImage = EasyImage.Builder(requireContext())
                .setChooserTitle("Pick Image")
                .setFolderName("YAPImage")
                .allowMultiple(false)
                .build()
            when (type) {
                takePhoto -> {
                    easyImage.openCameraForImage(this)

                }
                pickPhoto -> {
                    easyImage.openGallery(this)
                }
            }
            //  easyImage.openChooser(this)
        } else {
            EasyPermissions.requestPermissions(
                this, "This app needs access to your camera so you can take pictures.",
                RequestCodes.REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA
            )
        }

    }*/

    /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)
          if (resultCode == Activity.RESULT_OK) {
              handleImagePickerResult(requestCode, resultCode, data)
          }
      }*/

    /*private fun handleImagePickerResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (this::easyImage.isInitialized)
            easyImage.handleActivityResult(
                requestCode,
                resultCode,
                data,
                requireActivity(),
                object : DefaultCallback() {
                    override fun onMediaFilesPicked(
                        imageFiles: Array<MediaFile>,
                        source: MediaSource
                    ) {
                        onPhotosReturned(imageFiles, source)
                    }

                    override fun onImagePickerError(
                        @NonNull error: Throwable,
                        @NonNull source: MediaSource
                    ) {
                        viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
                    }

                    override fun onCanceled(@NonNull source: MediaSource) {
                        viewModel.state.toast = "No image detected^${AlertType.DIALOG.name}"
                    }
                })
    }*/

/*    private fun onPhotosReturned(path: Array<MediaFile>, source: MediaSource) {
        path.firstOrNull()?.let { mediaFile ->
            val ext = mediaFile.file.extension
            if (!ext.isBlank()) {
                when (ext) {
                    "png", "jpg", "jpeg" -> {
                        uploadDocumentAndMoveNext(
                            mediaFile.file,
                            currentPos ?: 0,
                            currentDocument ?: AdditionalDocument()
                        )
                    }
                    else -> {
                        viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
                    }
                }
            } else {
                viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }*/

    /*  private fun hasCameraPermission(): Boolean {
          return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)
      }
  */
    override fun onBackPressed(): Boolean {
        return true
    }
}
