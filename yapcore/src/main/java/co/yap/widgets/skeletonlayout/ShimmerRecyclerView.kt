package co.yap.widgets.skeletonlayout

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.R

class ShimmerRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var shimmerLayoutId: Int = 0
        set(value) {
            field = value
            shimmerLayoutId?.let {
                invalidate()
            }
        }
 
    var itemCount: Int = 0
        set(value) {
            field = value
            itemCount?.let {
                invalidate()
            }
        }

    var rvAdapter: Adapter<out ViewHolder>? = null
        set(value) {
            field = value
            rvAdapter?.let {
                rvAdapter?.registerAdapterDataObserver(MyDataObserverAdapter())
                skeleton = applySkeleton(shimmerLayoutId, itemCount)
                if(rvAdapter?.itemCount==0){
                    skeleton?.showSkeleton()
                }
                invalidate()
            }
        }

    private var skeleton: Skeleton? = null

    init {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ShimmerRecyclerview, 0, 0)
        shimmerLayoutId =
            attributes.getResourceId(R.styleable.ShimmerRecyclerview_shimmerLayoutId, shimmerLayoutId)
        itemCount =
            attributes.getInteger(R.styleable.ShimmerRecyclerview_itemCount, itemCount)
        attributes.recycle()
    }

    inner class MyDataObserverAdapter : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            if (rvAdapter?.itemCount == 0) {
                showSkeletonAdapter()
            } else {
                showOriginalAdapter()
            }
        }
    }

    fun showOriginalAdapter() {
        skeleton?.showOriginal()
    }

    fun showSkeletonAdapter() {
        skeleton?.showSkeleton()
    }
}