package co.yap.widgets

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import co.yap.yapcore.R
import java.util.*

class RollingCounterView : FrameLayout {
    private val ANIMATION_DURATION = 100
    var currentTextView: TextView? = null
    var nextTextView: TextView? = null

    constructor(context: Context) : super(context) {
        init(context = context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context = context)
    }

    private fun init(context: Context?) {
        LayoutInflater.from(context).inflate(R.layout.layout_rolling_counter, this)
        currentTextView = rootView.findViewById(R.id.currentTextView)
        nextTextView = rootView.findViewById(R.id.nextTextView)
        nextTextView?.translationY = height.toFloat()
        setValue(9)
    }

    fun setValue(realValue: Int) {
        if (currentTextView?.text == null || currentTextView?.text?.isEmpty() == true) {
            currentTextView?.text = String.format(Locale.getDefault(), "%d", realValue)
        }
        val oldValue = currentTextView?.text.toString().toInt()
        if (oldValue > realValue) {
            nextTextView?.text = String.format(Locale.getDefault(), "%d", oldValue - 1)
            currentTextView?.animate()?.translationY(-height.toFloat())
                ?.setDuration(ANIMATION_DURATION.toLong())?.start()
            nextTextView?.translationY = (nextTextView?.height?.toFloat() ?: 0f)
            nextTextView?.animate()?.translationY(0f)
                ?.setDuration(ANIMATION_DURATION.toLong())
                ?.setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        currentTextView?.text =
                            String.format(Locale.getDefault(), "%d", oldValue - 1)
                        currentTextView?.translationY = 1f
                        if (oldValue - 1 != realValue) {
                            setValue(realValue)
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })?.start()
        } else if (oldValue < realValue) {
            nextTextView?.text = String.format(Locale.getDefault(), "%d", oldValue + 1)

            currentTextView?.animate()?.translationY(height.toFloat())
                ?.setDuration(ANIMATION_DURATION.toLong())?.start()
            nextTextView?.translationY = -(nextTextView?.height?.toFloat() ?: 0f)
            nextTextView?.animate()?.translationY(0f)
                ?.setDuration(ANIMATION_DURATION.toLong())
                ?.setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        currentTextView?.text =
                            String.format(Locale.getDefault(), "%d", oldValue + 1)
                        currentTextView?.translationY = 1f
                        if (oldValue + 1 != realValue) {
                            setValue(realValue)
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })?.start()
        }
    }
}