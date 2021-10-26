package co.yap.modules.dashboard.transaction.receipt.add

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapuae.databinding.FragmentAddTransactionReceiptBinding
import co.yap.modules.dashboard.transaction.receipt.previewer.PreviewTransactionReceiptFragment
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants.FILE_PATH
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.extentions.createTempFile
import co.yap.yapcore.helpers.extentions.startFragmentForResult
import com.digitify.identityscanner.camera.CameraException
import com.digitify.identityscanner.camera.CameraListener
import com.digitify.identityscanner.camera.CameraOptions
import com.digitify.identityscanner.camera.PictureResult

class AddTransactionReceiptFragment : BaseBindingFragment<IAddTransactionReceipt.ViewModel>(),
    IAddTransactionReceipt.View, CameraListener {
    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutId() = R.layout.fragment_add_transaction_receipt
    override val viewModel: IAddTransactionReceipt.ViewModel
        get() = ViewModelProviders.of(this).get(AddTransactionReceiptViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBindingView().camera.open()
        getBindingView().camera.addCameraListener(this)
        registerObserver()
    }

    private fun getTransactionId(): String {
        arguments?.let { bundle ->
            bundle.getString(ExtraKeys.TRANSACTION_ID.name)?.let { id ->
                return id
            }
        }
        return ""
    }

    override fun registerObserver() {
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.camFab -> {
                    capturePicture()
                }
                R.id.ivBack -> requireActivity().finish()
            }
        })
    }

    private fun capturePicture() {
        if (getBindingView().camera.isTakingPicture) return
        getBindingView().camera.takePicture()
    }

    override fun onDestroyView() {
        unRegisterObserver()
        super.onDestroyView()
    }

    override fun unRegisterObserver() {
        viewModel.clickEvent.removeObservers(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getBindingView().camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.filter { it == PackageManager.PERMISSION_GRANTED }.run {
            if (this.size < grantResults.size) requireActivity().finish()
        }

    }

    private fun getBindingView() = (viewDataBinding as FragmentAddTransactionReceiptBinding)

    override fun onCaptureProcessCompleted(filename: String?) {

    }

    override fun onCameraOpened(options: CameraOptions) {
        getBindingView().camFab.isEnabled = true
    }

    override fun onCameraClosed() {
    }

    override fun onCameraError(exception: CameraException) {
    }

    override fun onPictureTaken(result: PictureResult) {
        result.toFile(
            requireContext().createTempFile("jpg")
        ) {
            it?.let {
                startFragmentForResult<PreviewTransactionReceiptFragment>(
                    fragmentName = PreviewTransactionReceiptFragment::class.java.name,
                    bundle = bundleOf(
                        FILE_PATH to it.absolutePath,
                        ExtraKeys.TRANSACTION_ID.name to getTransactionId(),
                        ExtraKeys.TAKE_IMAGE_FROM.name to "CAMERA"
                    )
                ) { resultCode, data ->
                    if (resultCode == Activity.RESULT_OK) {
                        requireActivity().setResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun onOrientationChanged(orientation: Int) {
    }

    override fun onAutoFocusStart(point: PointF) {
    }

    override fun onAutoFocusEnd(successful: Boolean, point: PointF) {
    }

    override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<out PointF>?) {
    }

    override fun onExposureCorrectionChanged(
        newValue: Float,
        bounds: FloatArray,
        fingers: Array<out PointF>?
    ) {
    }

    override fun onDestroy() {
        super.onDestroy()
        getBindingView().camera.close()
        getBindingView().camera.destroy()
    }
}
