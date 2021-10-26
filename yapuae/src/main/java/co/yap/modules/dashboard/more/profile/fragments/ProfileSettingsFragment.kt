package co.yap.modules.dashboard.more.profile.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.modules.dashboard.more.changepasscode.activities.ChangePasscodeActivity
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.modules.dashboard.more.main.fragments.MoreBaseFragment
import co.yap.modules.dashboard.more.profile.intefaces.IProfile
import co.yap.modules.dashboard.more.profile.viewmodels.ProfileSettingsViewModel
import co.yap.modules.webview.WebViewFragment
import co.yap.translation.Strings
import co.yap.widgets.bottomsheet.BottomSheetItem
import co.yap.yapcore.constants.Constants.KEY_IS_FINGERPRINT_PERMISSION_SHOWN
import co.yap.yapcore.constants.Constants.KEY_TOUCH_ID_ENABLED
import co.yap.yapcore.constants.RequestCodes.REQUEST_NOTIFICATION_SETTINGS
import co.yap.yapcore.enums.FeatureSet
import co.yap.yapcore.enums.PhotoSelectionType
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.SharedPreferenceManager
import co.yap.yapcore.helpers.biometric.BiometricUtil
import co.yap.yapcore.helpers.extentions.*
import co.yap.yapcore.interfaces.OnItemClickListener
import co.yap.yapcore.managers.SessionManager
import kotlinx.android.synthetic.main.layout_profile_picture.*
import kotlinx.android.synthetic.main.layout_profile_settings.*
import pl.aprilapps.easyphotopicker.MediaFile

class ProfileSettingsFragment : MoreBaseFragment<IProfile.ViewModel>(), IProfile.View {
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.fragment_profile
    override val viewModel: IProfile.ViewModel
        get() = ViewModelProviders.of(this).get(ProfileSettingsViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context is MoreActivity) {
            (context as MoreActivity).visibleToolbar()
        }
        viewModel.state.buildVersionDetail = versionName
        val sharedPreferenceManager =
            SharedPreferenceManager.getInstance(requireContext())

        if (BiometricUtil.hasBioMetricFeature(requireContext())) {
            val isTouchIdEnabled: Boolean =
                sharedPreferenceManager.getValueBoolien(
                    KEY_TOUCH_ID_ENABLED,
                    false
                )
            swTouchId.isChecked = isTouchIdEnabled
            llSignInWithTouch.visibility = View.VISIBLE

            swTouchId.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    sharedPreferenceManager.save(
                        KEY_IS_FINGERPRINT_PERMISSION_SHOWN,
                        true
                    )
                    sharedPreferenceManager.save(KEY_TOUCH_ID_ENABLED, true)
                } else {
                    sharedPreferenceManager.save(
                        KEY_TOUCH_ID_ENABLED,
                        false
                    )
                }
            }
        } else {
            llSignInWithTouch.visibility = View.GONE
        }

        SessionManager.user?.let {
            if (it.currentCustomer.getPicture() != null) {
                ivAddProfilePic.setImageResource(R.drawable.ic_edit_profile)
            }
        }
    }


    override fun onDestroy() {
        viewModel.clickEvent.removeObservers(this)
        super.onDestroy()
    }

    private fun logoutAlert() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.screen_profile_settings_logout_display_text_alert_title))
            .setMessage(getString(R.string.screen_profile_settings_logout_display_text_alert_message))
            .setPositiveButton(
                getString(R.string.screen_profile_settings_logout_display_text_alert_logout)
            ) { _, _ ->
                viewModel.logout()
            }

            .setNegativeButton(
                getString(R.string.screen_profile_settings_logout_display_text_alert_cancel),
                null
            )
            .show()
    }

    private fun doLogout() {
        SessionManager.doLogout(requireContext())
        activity?.finish()
    }

    override fun onPause() {
        viewModel.clickEvent.removeObservers(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.clickEvent.observe(this, Observer {
            when (it) {

                R.id.tvPersonalDetailView -> {
                    val action =
                        ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToPersonalDetailsFragment()
                    findNavController().navigate(action)
                }

                R.id.tvChangePasscode -> {
                    launchActivity<ChangePasscodeActivity>(type = FeatureSet.CHANGE_PASSCODE)
                }
                R.id.tvTermsAndConditionView -> {
                    startFragment(
                        fragmentName = WebViewFragment::class.java.name, bundle = bundleOf(
                            co.yap.yapcore.constants.Constants.PAGE_URL to co.yap.yapcore.constants.Constants.URL_TERMS_CONDITION,
                            co.yap.yapcore.constants.Constants.TOOLBAR_TITLE to getString(
                                Strings.screen_profile_settings_display_terms_and_conditions
                            )
                        ), showToolBar = false
                    )
                }

                R.id.tvFeesAndPricingPlansView -> {
                    startFragment(
                        fragmentName = WebViewFragment::class.java.name, bundle = bundleOf(
                            co.yap.yapcore.constants.Constants.PAGE_URL to co.yap.yapcore.constants.Constants.URL_FEES_AND_PRICING_PLAN
                        ), showToolBar = false
                    )
                }
                R.id.tvFollowOnInstagram -> requireContext().openInstagram()
                R.id.tvFollowOnTwitter -> requireContext().openTwitter()
                R.id.tvLikeUsOnFaceBook -> requireContext().openFacebook()

                R.id.ivProfilePic -> {
                }

                R.id.tvLogOut -> {
                    logoutAlert()
                }

                R.id.rlAddNewProfilePic -> {
                    trackEventWithScreenName(FirebaseEvent.CLICK_ADD_PHOTO)
                    requireActivity().launchSheet(
                        itemClickListener = itemListener,
                        itemsList = viewModel.getUploadProfileOptions(showRemovePhoto()),
                        heading = getString(Strings.screen_update_profile_photo_display_text_title)
                    )
                }

                viewModel.PROFILE_PICTURE_UPLOADED -> {
                }

                viewModel.EVENT_LOGOUT_SUCCESS -> {
                    doLogout()
                }
                R.id.llNotification -> {
                    navigateToNotificationSettings()
                }
            }
        })
    }

    private fun navigateToNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivityForResult(intent, REQUEST_NOTIFICATION_SETTINGS)
        }
    }

    private fun showRemovePhoto(): Boolean {
        return viewModel.state.profilePictureUrl.isNotEmpty() && ivProfilePic.hasBitmap()
    }

    override fun onImageReturn(mediaFile: MediaFile) {
        viewModel.clickEvent.call()
        viewModel.requestUploadProfilePicture(mediaFile.file)
        viewModel.state.imageUri = mediaFile.file.toUri()
        ivProfilePic.setImageURI(mediaFile.file.toUri())
    }

    private val itemListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            when ((data as BottomSheetItem).tag) {
                PhotoSelectionType.CAMERA.name -> {
                    trackEventWithScreenName(FirebaseEvent.CLICK_OPEN_CAMERA)
                    openImagePicker(PhotoSelectionType.CAMERA)
                }

                PhotoSelectionType.GALLERY.name -> {
                    trackEventWithScreenName(FirebaseEvent.CLICK_CHOOSE_PHOTO)
                    openImagePicker(PhotoSelectionType.GALLERY)
                }

                PhotoSelectionType.REMOVE_PHOTO.name -> {
                    trackEventWithScreenName(FirebaseEvent.CLICK_REMOVE_PHOTO)
                    viewModel.requestRemoveProfilePicture {
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_NOTIFICATION_SETTINGS) {
            if (NotificationManagerCompat.from(requireContext())
                    .areNotificationsEnabled()
            ) {
                viewModel.getNotificationScreenValues(true)

            } else {
                viewModel.getNotificationScreenValues(false)
            }
        }
    }
}