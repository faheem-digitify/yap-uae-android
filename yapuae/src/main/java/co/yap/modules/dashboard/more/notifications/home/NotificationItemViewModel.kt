package co.yap.modules.dashboard.more.notifications.home

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import co.yap.yapuae.R
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.yapcore.BaseListItemViewModel

class NotificationItemViewModel :
    BaseListItemViewModel<HomeNotification>() {
    private lateinit var mItem: HomeNotification
    private var position: Int = 0
    override fun setItem(item: HomeNotification, position: Int) {
        mItem = item
        this.position = position
    }

    override fun getItem() = mItem

    override fun onFirsTimeUiCreate(bundle: Bundle?, navigation: NavController?) {

    }

    override fun layoutRes() = R.layout.item_notification_v2

    override fun onItemClick(view: View, data: Any, pos: Int) {
    }

    fun onDeleteClick(view: View) {
        onChildViewClickListener?.invoke(view, position, getItem())
//        DebouncingOnClickListener(
//            intervalMillis = 0){
//
//        }
//        view.setOnClick {
//
//        }
    }

}