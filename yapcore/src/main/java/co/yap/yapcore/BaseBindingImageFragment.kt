package co.yap.yapcore

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.yapcore.constants.RequestCodes.REQUEST_CAMERA_PERMISSION
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.enums.PhotoSelectionType
import pl.aprilapps.easyphotopicker.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseBindingImageFragment<V : IBase.ViewModel<*>> : BaseBindingFragment<V>() {
    private lateinit var easyImage: EasyImage
    private lateinit var selectionType: PhotoSelectionType

    fun openImagePicker(selectionType: PhotoSelectionType) {
        this.selectionType = selectionType
        openPicker()
    }

    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION)
    private fun openPicker() {
        if (hasCameraPermission()) {
            easyImage =
                EasyImage.Builder(requireContext()) // Chooser only
                    .setChooserTitle("Pick Image")
                    .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                    .setFolderName("YAPImage")
                    .allowMultiple(false)
                    .build()
            when (selectionType) {
                PhotoSelectionType.CHOOSER ->
                    easyImage.openChooser(this)
                PhotoSelectionType.CAMERA ->
                    easyImage.openCameraForImage(this)
                PhotoSelectionType.GALLERY ->
                    easyImage.openGallery(this)
                else -> throw IllegalStateException("Invalid photo picker selection type found $selectionType")
            }
        } else {
            EasyPermissions.requestPermissions(
                this, Translator.getString(requireContext(), Strings.rationale_camera),
                REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA
            )
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE == requestCode) {
            if (hasCameraPermission()) {
                showToast("permission granted")
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            handleImagePickerResult(requestCode, resultCode, data)
        }
    }

    private fun handleImagePickerResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (::easyImage.isInitialized)
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
                        //Some error handling
                        error.printStackTrace()
                        viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
                    }

                    override fun onCanceled(@NonNull source: MediaSource) {
                        //Not necessary to remove any files manually anymore
                        viewModel.state.toast = "No image detected^${AlertType.DIALOG.name}"
                    }
                })
    }

    private fun onPhotosReturned(path: Array<MediaFile>, source: MediaSource) {
        path.firstOrNull()?.let { mediaFile ->
            val ext = mediaFile.file.extension
            if (!ext.isBlank()) {
                when (ext) {
                    "png", "jpg", "jpeg" -> onImageReturn(mediaFile)
                    else -> viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
                }
            } else {
                viewModel.state.toast = "Invalid file found^${AlertType.DIALOG.name}"
            }
        }
    }

    open fun onImageReturn(mediaFile: MediaFile) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )

    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)
    }
}