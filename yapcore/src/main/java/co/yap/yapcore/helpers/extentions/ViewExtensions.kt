package co.yap.yapcore.helpers.extentions

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ScrollView
import androidx.annotation.LayoutRes
import co.yap.widgets.CoreCircularImageView
import co.yap.yapcore.R
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.helpers.ImageBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

fun CollapsingToolbarLayout.enableScroll(@AppBarLayout.LayoutParams.ScrollFlags flags: Int = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
    val params = this.layoutParams as AppBarLayout.LayoutParams
    params.scrollFlags = flags
    this.layoutParams = params
}

fun CollapsingToolbarLayout.disableScroll() {
    val params = this.layoutParams as AppBarLayout.LayoutParams
    params.scrollFlags = 0
    this.layoutParams = params
}

fun ScrollView.scrollToBottomWithoutFocusChange() { // Kotlin extension to scrollView
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    post {
        smoothScrollBy(0, delta)
    }
}

fun ChipGroup.generateChipViews(@LayoutRes itemView: Int, list: List<String>) {
    val inflater: LayoutInflater =
        this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    for (index in list.indices) {
        val categoryName = list[index]
        val chip = inflater.inflate(itemView, this, false) as Chip
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,
            this.resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = categoryName
        this.addView(chip)
    }
}

fun CoreCircularImageView?.setCircularDrawable(
    title: String,
    url: String,
    position: Int,
    showBackground: Boolean = true,
    showInitials: Boolean = true,
    type : String = "merchant-category-id"
) {
    this?.let { image ->
        if (type == "merchant-category-id") {
            ImageBinding.loadCategoryAvatar(
                image,
                url,
                title,
                position,
                showBackground,
                showInitials
            )
        } else {
            ImageBinding.loadAnalyticsAvatar(
                image,
                url,
                title,
                position,
                false,
                showInitials
            )
        }
    }
}

fun ImageView?.hasBitmap(): Boolean {
    return this?.let {
        this.drawable != null && (this.drawable is BitmapDrawable)
    } ?: false
}