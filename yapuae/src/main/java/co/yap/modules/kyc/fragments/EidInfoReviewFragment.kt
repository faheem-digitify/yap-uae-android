package co.yap.modules.kyc.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.kyc.activities.DocumentsResponse
import co.yap.modules.kyc.enums.KYCAction
import co.yap.modules.kyc.viewmodels.EidInfoReviewViewModel
import co.yap.modules.onboarding.interfaces.IEidInfoReview
import co.yap.widgets.Status
import co.yap.yapcore.enums.AlertType
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.Utils.hideKeyboard
import co.yap.yapcore.helpers.showAlertDialogAndExitApp
import co.yap.yapcore.managers.SessionManager
import com.digitify.identityscanner.docscanner.activities.IdentityScannerActivity
import com.digitify.identityscanner.docscanner.enums.DocumentType
import kotlinx.android.synthetic.main.activity_eid_info_review.*
import java.io.File


class EidInfoReviewFragment : KYCChildFragment<IEidInfoReview.ViewModel>(), IEidInfoReview.View {

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.activity_eid_info_review

    override val viewModel: EidInfoReviewViewModel
        get() = ViewModelProviders.of(this).get(EidInfoReviewViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.parentViewModel?.skipFirstScreen?.value == true) {
            openCardScanner()
            tbBtnBack.setOnClickListener {
                viewModel.parentViewModel?.finishKyc?.value = DocumentsResponse(false)
            }
        }
        addObservers()
    }

    private fun addObservers() {
        viewModel.clickEvent.observe(this, Observer {
            when (it) {
                R.id.ivEditFirstName, R.id.tvFirstName -> {
                    ivEditFirstName.isEnabled = false
                    ivEditMiddleName.isEnabled = true
                    ivEditLastName.isEnabled = true
                    manageFocus(tvFirstName, ivEditFirstName)
                    trackEventWithScreenName(
                        FirebaseEvent.EDIT_FIELD,
                        bundleOf("field_name" to "first_name")
                    )
                }

                R.id.ivEditMiddleName, R.id.tvMiddleName -> {
                    ivEditMiddleName.isEnabled = false
                    ivEditFirstName.isEnabled = true
                    ivEditLastName.isEnabled = true
                    manageFocus(tvMiddleName, ivEditMiddleName)
                    trackEventWithScreenName(
                        FirebaseEvent.EDIT_FIELD,
                        bundleOf("field_name" to "middle_name")
                    )
                }

                R.id.ivEditLastName, R.id.tvLastName -> {
                    ivEditLastName.isEnabled = false
                    ivEditMiddleName.isEnabled = true
                    ivEditFirstName.isEnabled = true
                    manageFocus(tvLastName, ivEditLastName)
                    trackEventWithScreenName(
                        FirebaseEvent.EDIT_FIELD,
                        bundleOf("field_name" to "last_name")
                    )
                }

                viewModel.eventErrorInvalidEid -> showInvalidEidScreen()
                viewModel.eventErrorExpiredEid -> showExpiredEidScreen()
                viewModel.eventErrorUnderAge -> showUnderAgeScreen()
                viewModel.eventErrorFromUsa -> showUSACitizenScreen()
                viewModel.eventRescan -> openCardScanner()
                R.id.tvNoThanks -> {
                    trackEventWithScreenName(FirebaseEvent.RESCAN_ID)
                    hideKeyboard(tvNoThanks)
                    openCardScanner()
                }
                viewModel.eventAlreadyUsedEid -> {
                    viewModel.parentViewModel?.finishKyc?.value =
                        DocumentsResponse(false, KYCAction.ACTION_EID_FAILED.name)
                }

                viewModel.eventNextWithError -> {
                    viewModel.performUploadDocumentsRequest(true) {
                        if (it.equals("success", true)) {
                            val action =
                                EidInfoReviewFragmentDirections.actionEidInfoReviewFragmentToInformationErrorFragment(
                                    viewModel.errorTitle, viewModel.errorBody
                                )
                            findNavController().navigate(action)
                        } else {
                            viewModel.state.toast = "${it}^${AlertType.DIALOG.name}"
                        }
                    }

                }
                viewModel.eventFinish -> {
                    viewModel.parentViewModel?.finishKyc?.value =
                        DocumentsResponse(false, KYCAction.ACTION_EID_FAILED.name)
                }
                viewModel.eventNext -> {
                    trackEventWithScreenName(FirebaseEvent.CONFIRM_ID)
//                    requireActivity().firebaseTagManagerEvent(FirebaseTagManagerModel(action = FirebaseEvents.CONFIRM_ID.event))
                    SessionManager.getAccountInfo()
                    SessionManager.onAccountInfoSuccess.observe(this, Observer { isSuccess ->
                        if (isSuccess) {
                            viewModel.parentViewModel?.finishKyc?.value =
                                DocumentsResponse(true)
                        } else {
                            showToast("Accounts info failed")
                            viewModel.parentViewModel?.finishKyc?.value =
                                DocumentsResponse(true)
                        }

                    })
                }
                viewModel.eventEidUpdate -> {
                    SessionManager.getAccountInfo()
                    SessionManager.onAccountInfoSuccess.observe(this, Observer { isSuccess ->
                        if (isSuccess) {
                            viewModel.parentViewModel?.finishKyc?.value =
                                DocumentsResponse(false, KYCAction.ACTION_EID_UPDATE.name)
                        } else {
                            showToast("Accounts info failed")
                            viewModel.parentViewModel?.finishKyc?.value =
                                DocumentsResponse(false, KYCAction.ACTION_EID_UPDATE.name)
                        }

                    })
                }
                viewModel.eventCitizenNumberIssue, viewModel.eventEidExpiryDateIssue -> invalidCitizenNumber(
                    "Sorry, that didn’t work. Please try again"
                )
            }
        })
        viewModel.eidStateLiveData.observe(this, Observer {
            if (it.status == Status.ERROR) {
                invalidCitizenNumber(it.message ?: "Sorry, that didn’t work. Please try again")
            }
        })
    }

    private fun invalidCitizenNumber(title: String) {
        activity?.let {
            it.showAlertDialogAndExitApp(
                message = title,
                callback = {
                    openCardScanner()
                },
                closeActivity = false
            )
            viewModel.parentViewModel?.paths?.forEach { filePath ->
                File(filePath).deleteRecursively()
            }
        }
    }

    private fun manageFocus(
        editText: EditText,
        ivEditName: ImageView
    ) {
        if (!editText.isFocused) {
            editText.isFocusable = true

            editText.isFocusableInTouchMode = true
            editText.isActivated = true
            editText.setSelection(editText.length())
            editText.requestFocus()
            editText.performClick()
            (editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                ivEditName.isEnabled = true
                editText.isFocusable = false
                editText.isFocusableInTouchMode = false
            }
        }

        editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.action === KeyEvent.ACTION_DOWN || keyEvent.action === KeyEvent.KEYCODE_ENTER
            ) {
                ivEditName.isEnabled = true
                editText.isFocusable = false
                editText.isFocusableInTouchMode = false
            }
            false
        })


    }

    override fun onDestroyView() {
        viewModel.clickEvent.removeObservers(this)
        super.onDestroyView()
    }

    override fun showUnderAgeScreen() {
        val action =
            EidInfoReviewFragmentDirections.actionEidInfoReviewFragmentToInformationErrorFragment(
                viewModel.errorTitle, viewModel.errorBody
            )
        navigate(action)
    }

    override fun showExpiredEidScreen() {
        val action =
            EidInfoReviewFragmentDirections.actionEidInfoReviewFragmentToInformationErrorFragment(
                viewModel.errorTitle, viewModel.errorBody
            )
        navigate(action)
    }

    override fun showInvalidEidScreen() {
        val action =
            EidInfoReviewFragmentDirections.actionEidInfoReviewFragmentToInformationErrorFragment(
                viewModel.errorTitle, viewModel.errorBody
            )
        navigate(action)
    }

    private fun showEIDAlert(
        message: String,
        posBtn: String,
        negBtn: String? = null,
        response: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(requireContext()).apply {
            setCancelable(false)
            setMessage(message)
            setPositiveButton(posBtn) { _, _ ->
                response.invoke(true)
            }
            if (negBtn != null)
                setNegativeButton(negBtn) { _, _ ->
                    response.invoke(false)
                }
        }.create().show()
    }

    override fun showUSACitizenScreen() {
        val action =
            EidInfoReviewFragmentDirections.actionEidInfoReviewFragmentToInformationErrorFragment(
                viewModel.errorTitle, viewModel.errorBody
            )
        navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null && viewModel.parentViewModel?.skipFirstScreen?.value == true) {

        }
        if (requestCode == IdentityScannerActivity.SCAN_EID_CAM && resultCode == Activity.RESULT_OK) {
            data?.let {
                viewModel.onEIDScanningComplete(it.getParcelableExtra(IdentityScannerActivity.SCAN_RESULT))
            }
        } else {
            viewModel.parentViewModel?.finishKyc?.value = DocumentsResponse(false)
        }
    }

    override fun openCardScanner() {
        viewModel.invalidateFields()
        startActivityForResult(
            IdentityScannerActivity.getLaunchIntent(
                requireContext(),
                DocumentType.EID,
                IdentityScannerActivity.SCAN_FROM_CAMERA
            ),
            IdentityScannerActivity.SCAN_EID_CAM
        )
    }

    override fun onDestroy() {
        viewModel.parentViewModel?.paths?.forEach { filePath ->
            File(filePath).deleteRecursively()
        }
        super.onDestroy()
    }
}