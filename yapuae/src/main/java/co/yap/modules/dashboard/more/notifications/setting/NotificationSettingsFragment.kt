package co.yap.modules.dashboard.more.notifications.setting

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProviders
import co.yap.yapuae.BR
import co.yap.yapuae.R
import co.yap.yapcore.BaseBindingFragment
import kotlinx.android.synthetic.main.fragment_notification_settings.*

class NotificationSettingsFragment : BaseBindingFragment<INotificationSettings.ViewModel>(),
    INotificationSettings.View, CompoundButton.OnCheckedChangeListener {
    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_notification_settings

    override val viewModel: INotificationSettings.ViewModel
        get() = ViewModelProviders.of(this).get(NotificationSettingsViewModel::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNotificationSettings {
            if (it) {
                swNotifications.setOnCheckedChangeListener(this)
                swEmail.setOnCheckedChangeListener(this)
                swSms.setOnCheckedChangeListener(this)
            }
        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.swNotifications -> viewModel.state.inAppNotificationsAllowed = isChecked
            R.id.swEmail -> viewModel.state.emailNotificationsAllowed = isChecked
            R.id.swSms -> viewModel.state.smsNotificationsAllowed = isChecked
        }
        viewModel.saveNotificationSettings()
    }

    override fun onToolBarClick(id: Int) {
        super.onToolBarClick(id)
        when (id) {
            R.id.ivLeftIcon -> navigateBack()
        }
    }
}
