package co.yap.modules.dashboard.more.notifications.home

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.BR
import co.yap.yapuae.databinding.ItemNotificationV2Binding
import co.yap.networking.notification.responsedtos.HomeNotification
import co.yap.widgets.advrecyclerview.swipeable.SwipeableItemAdapter
import co.yap.widgets.advrecyclerview.swipeable.SwipeableItemConstants
import co.yap.widgets.advrecyclerview.swipeable.action.SwipeResultAction
import co.yap.widgets.advrecyclerview.swipeable.action.SwipeResultActionDefault
import co.yap.widgets.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import co.yap.widgets.advrecyclerview.utils.AbstractSwipeableItemViewHolder
import co.yap.yapcore.BaseRVAdapter

class NotificationsHomeAdapter(mValue: MutableList<HomeNotification>, navigation: NavController?) :
    BaseRVAdapter<HomeNotification, NotificationItemViewModel, NotificationsHomeAdapter.ViewHolder>(
        mValue,
        navigation
    ), SwipeableItemAdapter<NotificationsHomeAdapter.ViewHolder> {
    private var oldSwipePosition: Int = RecyclerView.NO_POSITION

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.maxLeftSwipeAmount = -0.2f
        holder.maxRightSwipeAmount = 0f
        holder.swipeItemHorizontalSlideAmount = if (datas[position].isPinned == true) -0.2f else 0f
    }

    override fun getItemViewType(position: Int) = position
    override fun getLayoutId(viewType: Int) = getViewModel().layoutRes()
    override fun getItemId(position: Int) = datas[position].id.hashCode().toLong()
    override fun getViewHolder(
        view: View,
        viewModel: NotificationItemViewModel,
        mDataBinding: ViewDataBinding, viewType: Int
    ) = ViewHolder(
        view,
        viewModel,
        mDataBinding
    )

    override fun getViewModel() = NotificationItemViewModel()

    override fun getVariableId() = BR.viewModel
    override fun onGetSwipeReactionType(holder: ViewHolder, position: Int, x: Int, y: Int) =
        if (datas[position].isDeletable == true) SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT else SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_ANY


    override fun onSwipeItemStarted(holder: ViewHolder, position: Int) {
        notifyDataSetChanged()
    }

    override fun onSetSwipeBackground(holder: ViewHolder, position: Int, type: Int) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.binding.flSwipeable.visibility = View.GONE
        } else {
            holder.binding.flSwipeable.visibility = View.VISIBLE
        }
    }

    override fun onSwipeItem(holder: ViewHolder, position: Int, result: Int): SwipeResultAction {
        return when (result) {
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> {
                SwipeLeftResultAction(
                    this,
                    position
                )
            }
            else -> UnpinResultActionconstructor(this, position)
        }
    }

    override fun removeAt(position: Int) {
        super.removeAt(position)
        oldSwipePosition = RecyclerView.NO_POSITION
    }

    class ViewHolder(
        view: View,
        viewModel: NotificationItemViewModel,
        mDataBinding: ViewDataBinding
    ) :
        AbstractSwipeableItemViewHolder<HomeNotification, NotificationItemViewModel>(
            view,
            viewModel,
            mDataBinding
        ) {
        val binding = mDataBinding as ItemNotificationV2Binding
        override fun getSwipeableContainerView() = binding.foregroundContainer
    }

    internal class SwipeLeftResultAction constructor(
        private var mAdapter: NotificationsHomeAdapter?,
        private val position: Int
    ) :
        SwipeResultActionMoveToSwipedDirection() {
        private var mSetPinned = false
        override fun onPerformAction() {
            super.onPerformAction()
            mAdapter?.datas?.get(position)?.isPinned?.let {
                if (!it) {
                    mAdapter?.datas?.get(position)?.isPinned = true
                    mAdapter?.notifyItemChanged(position)
                    mSetPinned = true
                }
            }
            if (mAdapter?.oldSwipePosition != RecyclerView.NO_POSITION)
                mAdapter?.datas?.get(mAdapter?.oldSwipePosition!!)?.isPinned?.let {
                    if (it) {
                        mAdapter?.datas?.get(mAdapter?.oldSwipePosition!!)?.isPinned = false
                        mAdapter?.notifyItemChanged(mAdapter?.oldSwipePosition!!)
                    }
                }
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            mAdapter?.oldSwipePosition = position
            if (mSetPinned) {
                //mAdapter?.mEventListener?.onItemPinned(position, mSetPinned)
            }
        }

        override fun onCleanUp() {
            super.onCleanUp()
            mAdapter = null
        }
    }

    internal class UnpinResultActionconstructor(
        private var mAdapter: NotificationsHomeAdapter?,
        private val position: Int
    ) :
        SwipeResultActionDefault() {

        override fun onPerformAction() {
            super.onPerformAction()
            mAdapter?.datas?.get(position)?.isPinned?.let {
                if (it) {
                    mAdapter?.datas?.get(position)?.isPinned = false
                    mAdapter?.notifyItemChanged(position)
                }
            }
        }

        override fun onSlideAnimationEnd() {
            super.onSlideAnimationEnd()
            //  mAdapter?.mEventListener?.onItemPinned(position, false)
            mAdapter?.oldSwipePosition = RecyclerView.NO_POSITION
        }

        override fun onCleanUp() {
            super.onCleanUp()
            mAdapter = null
        }
    }


}