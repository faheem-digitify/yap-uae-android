package co.yap.yapcore.helpers.extentions


import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.yap.networking.models.ApiResponse
import co.yap.widgets.setOnClick

/**
 * Register [OnClickListener] on ViewHolder root view
 * @param event callback function receiving root view, item position and type
 * @return returns this view holder
 */
fun <T : RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
    itemView.setOnClick {
        event.invoke(it, adapterPosition, itemViewType)
    }
    return this
}

/**
 * Register [OnLongClickListener] on ViewHolder root view
 * @param event callback function receiving root view, item position and type
 * @return returns this view holder
 */
fun <T : RecyclerView.ViewHolder> T.onLongClick(event: (view: View, position: Int, type: Int) -> Boolean): T {
    itemView.setOnLongClickListener() {
        event.invoke(it, adapterPosition, itemViewType)
    }
    return this
}

/**
 * Register [OnTouchListener] on ViewHolder root view
 * @param event callback function receiving root view, motion event, item position and type
 * @return returns this view holder
 */
fun <T : RecyclerView.ViewHolder> T.onTouch(event: (view: View, motionEvent: MotionEvent, position: Int, type: Int) -> Boolean): T {
    itemView.setOnTouchListener { v, e ->
        event.invoke(v, e, adapterPosition, itemViewType)
    }
    return this
}