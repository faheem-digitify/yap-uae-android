package co.yap.modules.dashboard.transaction.receipt.previewer

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapcore.BaseBindingFragment
import co.yap.yapcore.constants.Constants.FILE_PATH
import co.yap.yapcore.constants.RequestCodes
import co.yap.yapcore.helpers.ExtraKeys
import co.yap.yapcore.helpers.extentions.toast
import co.yap.yapcore.helpers.permissions.PermissionEnum
import co.yap.yapcore.helpers.permissions.PermissionHelper
import co.yap.yapcore.helpers.permissions.PermissionUtils
import java.io.File

class PreviewTransactionReceiptFragment :
    BaseBindingFragment<IPreviewTransactionReceipt.ViewModel>(),
    IPreviewTransactionReceipt.View {
    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutId() = R.layout.fragment_preview_transaction_reseipt
    private var permissionHelper: PermissionHelper? = null

    override val viewModel: IPreviewTransactionReceipt.ViewModel
        get() = ViewModelProviders.of(this).get(PreviewTransactionReceiptViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(FILE_PATH)?.let {
                viewModel.state.filePath = Uri.fromFile(File(it))
            }
            bundle.getString(ExtraKeys.TRANSACTION_ID.name)?.let { id ->
                viewModel.transactionId = id
            }
            bundle.getString(ExtraKeys.TAKE_IMAGE_FROM.name)?.let { from ->
                if (from == "CAMERA") viewModel.state.showRedo.set(true) else viewModel.state.showRedo.set(
                    false
                )
            }

        }
        initPermissionHelper()
        registerObserver()
    }

    private fun initPermissionHelper() {
        permissionHelper = PermissionHelper(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), RequestCodes.REQUEST_STORAGE_PERMISSION
        )
    }

    override fun registerObserver() {
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.btnSave -> {
                    if (PermissionUtils.isGranted(
                            requireContext(),
                            PermissionEnum.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        saveImageToGallery()
                    } else {
                        askPermission()
                    }
                }
                R.id.tvRedo -> {
                    activity?.onBackPressed()
                }
                R.id.ivBack -> {
                    activity?.finish()
                }
            }
        })
    }

    private fun saveImageToGallery() {
        viewModel.state.filePath?.let { uri ->
            val file = File(uri.path ?: "")
            viewModel.requestSavePicture(file) {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }
        }
    }

    private fun askPermission() {
        permissionHelper?.request(object : PermissionHelper.PermissionCallback {
            override fun onPermissionGranted() {
                saveImageToGallery()
            }

            override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
            }

            override fun onPermissionDenied() {
                toast(requireContext(), requireContext().getString(R.string.all_permission_msg))

            }

            override fun onPermissionDeniedBySystem() {

            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper?.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onDestroyView() {
        unRegisterObserver()
        super.onDestroyView()
    }

    override fun unRegisterObserver() {
        viewModel.clickEvent.removeObservers(this)
    }
}
