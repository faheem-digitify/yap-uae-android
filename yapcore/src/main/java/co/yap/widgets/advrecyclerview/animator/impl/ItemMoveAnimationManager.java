package co.yap.widgets.advrecyclerview.animator.impl;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import co.yap.widgets.advrecyclerview.animator.BaseItemAnimator;

public abstract class ItemMoveAnimationManager extends BaseItemAnimationManager<MoveAnimationInfo> {
    public static final String TAG = "ARVItemMoveAnimMgr";

    public ItemMoveAnimationManager(@NonNull BaseItemAnimator itemAnimator) {
        super(itemAnimator);
    }

    @Override
    public long getDuration() {
        return mItemAnimator.getMoveDuration();
    }

    @Override
    public void setDuration(long duration) {
        mItemAnimator.setMoveDuration(duration);
    }

    @Override
    public void dispatchStarting(@NonNull MoveAnimationInfo info, @NonNull RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchMoveStarting(" + item + ")");
        }
        mItemAnimator.dispatchMoveStarting(item);
    }

    @Override
    public void dispatchFinished(@NonNull MoveAnimationInfo info, @NonNull RecyclerView.ViewHolder item) {
        if (debugLogEnabled()) {
            Log.d(TAG, "dispatchMoveFinished(" + item + ")");
        }
        mItemAnimator.dispatchMoveFinished(item);
    }

    @Override
    protected boolean endNotStartedAnimation(@NonNull MoveAnimationInfo info, @Nullable RecyclerView.ViewHolder item) {
        if ((info.holder != null) && ((item == null) || (info.holder == item))) {
            onAnimationEndedBeforeStarted(info, info.holder);
            dispatchFinished(info, info.holder);
            info.clear(info.holder);
            return true;
        } else {
            return false;
        }
    }

    public abstract boolean addPendingAnimation(@NonNull RecyclerView.ViewHolder item, int fromX, int fromY, int toX, int toY);
}
