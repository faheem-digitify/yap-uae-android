package co.yap.widgets.scanqrcode

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.networking.customers.responsedtos.sendmoney.Beneficiary
import co.yap.translation.Strings
import co.yap.widgets.qrcode.QRCodeFragment
import co.yap.yapcore.BR
import co.yap.yapcore.BaseBindingImageFragment
import co.yap.yapcore.R
import co.yap.yapcore.constants.RequestCodes.REQUEST_CAMERA_PERMISSION
import co.yap.yapcore.databinding.FragmentScanQrCodeBinding
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.helpers.ImageBinding
import co.yap.yapcore.helpers.extentions.getQRCode
import co.yap.yapcore.helpers.permissions.PermissionHelper
import co.yap.yapcore.managers.SessionManager
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import pl.aprilapps.easyphotopicker.MediaFile
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream

class ScanQRCodeFragment : BaseBindingImageFragment<IScanQRCode.ViewModel>(),
    IScanQRCode.View, QRCodeReaderView.OnQRCodeReadListener {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_scan_qr_code
    val cameraPer = 1
    var oneTimeCall = true;
    var qrCodeReaderView: QRCodeReaderView? = null
    var permissionHelper: PermissionHelper? = null

    override val viewModel: ScanQRCodeViewModel
        get() = ViewModelProviders.of(this).get(
            ScanQRCodeViewModel::class.java
        )

    private fun getBindings(): FragmentScanQrCodeBinding =
        viewDataBinding as FragmentScanQrCodeBinding

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        if (!viewModel.state.loading) {
            qrCodeReaderView?.setQRDecodingEnabled(false)
            sendQrRequest(text?.getQRCode())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.contactInfo.observe(this, onFetchContactInfo)
        viewModel.noContactFoundEvent.observe(this, onNoContactInfo)
    }

    override fun onResume() {
        super.onResume()
        checkPermission(cameraPer)
    }

    private fun initQRCodeReaderView() {
        ImageBinding.setImageDrawable(
            getBindings().ivOverLay,
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_qr_scan)
        )
        qrCodeReaderView = getBindings().qrCodeReaderView
        qrCodeReaderView?.setAutofocusInterval(2000L)
        qrCodeReaderView?.setOnQRCodeReadListener(this)
        qrCodeReaderView?.setBackCamera()
        qrCodeReaderView?.startCamera()
        qrCodeReaderView?.setQRDecodingEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        qrCodeReaderView?.stopCamera()
    }

    private val onFetchContactInfo = Observer<Beneficiary> {
        it?.let {
            val intent = Intent()
            intent.putExtra(Beneficiary::class.java.name, it)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
    }

    private val onNoContactInfo = Observer<Boolean> {
        qrCodeReaderView?.setQRDecodingEnabled(true)
    }

    private fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource =
            RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            contents = result.text
        } catch (e: Exception) {
            showToast("Error decoding QRCode")
            qrCodeReaderView?.setQRDecodingEnabled(true)
        }
        return contents
    }

    private fun checkPermission(type: Int) {
        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.CAMERA
            ), REQUEST_CAMERA_PERMISSION
        )
        permissionHelper?.request(object : PermissionHelper.PermissionCallback {
            override fun onPermissionGranted() {
                if (type == cameraPer) {
                    initQRCodeReaderView()
                } else {
                    openImagePicker(PhotoSelectionType.GALLERY)
                }
            }

            override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
                if (type == cameraPer) {
                    if (grantedPermission.contains(Manifest.permission.CAMERA))
                        initQRCodeReaderView()
                } else {
                    openImagePicker(PhotoSelectionType.GALLERY)

                }
            }

            override fun onPermissionDenied() {
                showToast("Can't proceed without permissions")
                qrCodeReaderView?.setQRDecodingEnabled(true)
            }

            override fun onPermissionDeniedBySystem() {
                showToast("Can't proceed without permissions")
                qrCodeReaderView?.setQRDecodingEnabled(true)

            }
        })
    }

    override fun onToolBarClick(id: Int) {
        when (id) {
            R.id.ivBack -> {
                requireActivity().onBackPressed()
            }
            R.id.ivLibrary -> {
                qrCodeReaderView?.setQRDecodingEnabled(false)
                checkPermission(2)
            }
            R.id.ivMyQrCode -> {
                QRCodeFragment {
                    qrCodeReaderView?.setQRDecodingEnabled(true)
                }.let { fragment ->
                    if (isAdded)
                        qrCodeReaderView?.setQRDecodingEnabled(false)
                    fragment.show(requireActivity().supportFragmentManager, "")
                }
            }
        }
    }

    private fun sendQrRequest(qrCode: String?) {
        SessionManager.user?.let { accountInfo ->
            if (qrCode == accountInfo.encryptedAccountUUID) {
                showToast(getString(Strings.screen_qr_code_own_uuid_error_message))
            } else {
                viewModel.uploadQRCode(qrCode)
            }
        }
    }

    override fun onImageReturn(mediaFile: MediaFile) {
        val inputStream: InputStream =
            BufferedInputStream(FileInputStream(mediaFile.file))
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
        scanQRImage(bitmap)?.let {
            sendQrRequest(it.getQRCode())
        }
    }
}