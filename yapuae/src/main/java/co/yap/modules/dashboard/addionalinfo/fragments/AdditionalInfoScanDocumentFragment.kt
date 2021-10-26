package co.yap.modules.dashboard.addionalinfo.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAdditionalInfoScanDocumentBinding
import co.yap.modules.dashboard.addionalinfo.interfaces.IAdditionalInfoScanDocument
import co.yap.modules.dashboard.addionalinfo.model.AdditionalDocumentImage
import co.yap.modules.dashboard.addionalinfo.viewmodels.AdditionalInfoScanDocumentViewModel
import co.yap.widgets.guidedtour.description.locationOnScreen
import co.yap.yapcore.helpers.extentions.startFragmentForResult
import co.yap.yapcore.helpers.rx.Task
import com.digitify.identityscanner.camera.CameraException
import com.digitify.identityscanner.camera.CameraListener
import com.digitify.identityscanner.camera.CameraOptions
import com.digitify.identityscanner.camera.PictureResult
import com.digitify.identityscanner.utils.ImageUtils
import java.io.File


class AdditionalInfoScanDocumentFragment :
    AdditionalInfoBaseFragment<IAdditionalInfoScanDocument.ViewModel>(),
    IAdditionalInfoScanDocument.View, CameraListener {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_additional_info_scan_document
    lateinit var fileImage: File
    override val viewModel: IAdditionalInfoScanDocument.ViewModel
        get() = ViewModelProviders.of(this).get(AdditionalInfoScanDocumentViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataArguments()
        setUpCamera()
    }

    private fun setUpCamera() {
        getBindings().camera.setLifecycleOwner(this)
        getBindings().camera.addCameraListener(this)
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivClose -> {
                requireActivity().onBackPressed()
            }
            R.id.btnScan -> {
                capturePicture()
            }
        }
    }

    private fun getBindings(): FragmentAdditionalInfoScanDocumentBinding {
        return viewDataBinding as FragmentAdditionalInfoScanDocumentBinding
    }

    override fun onCameraOpened(options: CameraOptions) {
    }

    override fun onCameraClosed() {
    }

    override fun onCameraError(exception: CameraException) {
        showToast(exception.message.toString())
    }

    override fun onPictureTaken(result: PictureResult) {
        result.toFile(
            ImageUtils.getFilePrivately(activity) ?: File(result.toString())
        ) { file: File? -> file?.let { cropImage(it) } ?: showToast("Invalid image") }
//        result.toBitmap(getScreenWidth(), getScreenHeight()){
//                bitmap: Bitmap? ->
//        }
    }

    private fun cropImage(file: File) {
        fileImage = file
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        setOrientation(fileImage.absolutePath, bitmap) { bitmap ->
            val cropBitmap: Bitmap = Bitmap.createBitmap(
                bitmap,
                (getBindings().ivOverLay.locationOnScreen.x).toInt(),
                (getBindings().ivOverLay.locationOnScreen.y).toInt(),
                getWidth(bitmap),
                getHeight(bitmap)
            )
            setOrientation(fileImage.absolutePath, cropBitmap) {
                reWriteImage(file, it)
            }
        }

    }

    private fun getWidth(bitmap: Bitmap?): Int {
        val width =
            getBindings().ivOverLay.measuredWidth + getBindings().ivOverLay.locationOnScreen.x + 160
        return if (width <= bitmap?.width ?: 0)
            getBindings().ivOverLay.measuredWidth + 160
        else {
            val diff = width - (bitmap?.width ?: 0)
            (getBindings().ivOverLay.measuredWidth + 160) - diff
        }
    }

    private fun getHeight(bitmap: Bitmap?): Int {
        val height =
            getBindings().ivOverLay.measuredHeight + getBindings().ivOverLay.locationOnScreen.y + 160
        return if (height <= bitmap?.height ?: 0)
            getBindings().ivOverLay.measuredHeight + 160
        else {
            val diff = height - (bitmap?.height ?: 0)
            (getBindings().ivOverLay.measuredHeight + 160) - diff
        }
    }

    private fun showImage() {
        startFragmentForResult<AdditionalInfoViewDocumentFragment>(
            fragmentName = AdditionalInfoViewDocumentFragment::class.java.name,
            bundle = bundleOf(
                AdditionalDocumentImage::class.java.name to AdditionalDocumentImage(
                    file = fileImage,
                    name = viewModel.state.documentName.get() ?: ""
                )
            )
        ) { resultCode, _ ->
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent()
                intent.putExtra("file", fileImage)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        }

    }

    override fun onOrientationChanged(orientation: Int) {}

    override fun onAutoFocusStart(point: PointF) {}

    override fun onAutoFocusEnd(successful: Boolean, point: PointF) {}

    override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<out PointF>?) {}

    override fun onExposureCorrectionChanged(
        newValue: Float,
        bounds: FloatArray,
        fingers: Array<out PointF>?
    ) {
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getBindings().camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun capturePicture() {
        if (getBindings().camera.isTakingPicture) return
        getBindings().camera.takePictureSnapshot()
    }


    private fun reWriteImage(filename: File, croppedBmp: Bitmap) {
        var file: File? = null
        Task.runSafely({
//            file = overWrite(filename, croppedBmp, quality = 90)

        }, {
//            activity?.runOnUiThread(Runnable {
//                progress?.hide()
            showImage()
        }, true)

//            onCaptureProcessCompleted(filename)
    }

    private fun setOrientation(
        photoPath: String?,
        bitmap: Bitmap,
        success: (file: Bitmap) -> Unit
    ) {
        val ei = ExifInterface(photoPath ?: "")
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        var rotatedBitmap: Bitmap
        rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        success.invoke(rotatedBitmap)
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun getDataArguments() {
        arguments?.let { bundle ->
            bundle.getParcelable<AdditionalDocumentImage>(AdditionalDocumentImage::class.java.name)
                ?.let {
                    viewModel.state.documentName.set(it.name)
                }
        }
    }
}
