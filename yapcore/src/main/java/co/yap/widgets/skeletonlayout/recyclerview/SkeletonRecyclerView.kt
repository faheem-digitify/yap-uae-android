package co.yap.widgets.skeletonlayout.recyclerview

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import co.yap.widgets.skeletonlayout.Skeleton
import co.yap.widgets.skeletonlayout.SkeletonConfig
import co.yap.widgets.skeletonlayout.SkeletonStyle

internal class SkeletonRecyclerView(
    private val recyclerView: RecyclerView,
    @LayoutRes layoutResId: Int,
    itemCount: Int,
    config: SkeletonConfig
) : Skeleton, SkeletonStyle by config {

    private var originalAdapter = recyclerView.adapter
    private var skeletonAdapter = SkeletonRecyclerViewAdapter(layoutResId, itemCount, config)

    init {
        config.addValueObserver { skeletonAdapter.notifyDataSetChanged() }
    }

    override fun showOriginal() {
        recyclerView.adapter = originalAdapter
    }

    override fun showSkeleton() {
        recyclerView.adapter = skeletonAdapter
    }

    fun setOriginalAdapter(adapter: RecyclerView.Adapter<*>) {
        originalAdapter = adapter
    }

    override fun isSkeleton(): Boolean {
        return recyclerView.adapter == skeletonAdapter
    }
}