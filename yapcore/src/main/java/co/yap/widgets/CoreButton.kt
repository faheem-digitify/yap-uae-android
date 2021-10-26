package co.yap.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import co.yap.yapcore.R
import co.yap.yapcore.helpers.ThemeColorUtils
import co.yap.yapcore.helpers.Utils


class CoreButton : AppCompatButton {

    private var btnWeight: Int = 0
    private var btnHeight: Int = 0
    private var labelTextColor: Int = 0
    private var pressedColor: Int = 0
    private var defaultStateColor: Int = 0
    private var roundRadius: Int = 0
    private var btnRadius: Int = 0
    private var shapeType: Int = 0
    private var DRAWABLE_RIGHT: Int = 1
    private var DRAWABLE_LEFT: Int = 0
    private var enableButton: Boolean = true
    private var hasBoldText: Boolean = false

    private var defaultDrawablePaddingLeft: Float = 9.5f
    private var defaultDrawablePaddingRight: Float = 1.2f
    private var defaultDrawablePaddingTop: Float = 2.5f

    private var alignmentDistnce: Float = 2f

    private var drawablePaddingLeft: Float = 9.5f
    private var drawablePaddingRight: Float = 1.2f
    private var drawablePaddingTop: Float = 2.5f

    private var drawablePositionType: Int = 0
    private var labelTextSize: Float = 0f
    var drawable: Drawable? = null
    private var paintText: Paint = Paint()
    private var paint: Paint = Paint()
    private var rectF: RectF = RectF()
    lateinit var bitmapIcon: Bitmap
    private var btnSize: Int = -1

    var buttonSize: ButtonSize = ButtonSize.SMALL
        set(value) {
            field = value
            btnSize = value.type
            setButtonDimension()
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs,
        defStyleAttr
    ) {
        init(context, attrs)

    }

    @SuppressLint("ResourceType")
    @Suppress("DEPRECATION")
    private fun init(context: Context, attrs: AttributeSet) {
        if (isInEditMode) {
            return
        }

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CoreButton)

        paint.style = Paint.Style.STROKE

        drawable = typedArray.getDrawable(
            R.styleable.CoreButton_btn_drawable
        )
        btnSize = typedArray.getInt(R.styleable.CoreButton_btn_size, ButtonSize.SMALL.type)
        drawablePositionType = typedArray.getInt(R.styleable.CoreButton_btn_drawable_position, 2)
        enableButton = typedArray.getBoolean(R.styleable.CoreButton_btn_enable, enableButton)
        hasBoldText = typedArray.getBoolean(R.styleable.CoreButton_btn_has_bold_text, hasBoldText)


        labelTextColor = typedArray.getColor(
            R.styleable.CoreButton_btn_text_color,
            resources.getColor(R.color.white)
        )

        if (enableButton) {
            defaultStateColor = typedArray.getColor(
                R.styleable.CoreButton_btn_unpressed_color,
                ThemeColorUtils.colorPrimaryAttribute(context)
            )
            pressedColor = typedArray.getColor(
                R.styleable.CoreButton_btn_pressed_color,
                ThemeColorUtils.colorPressedBtnStateAttribute(context)
            )

        } else {
            defaultStateColor = ThemeColorUtils.colorPrimaryDisabledBtnAttribute(context)
//            defaultStateColor =  resources.getColor(R.color.greyLight)

            pressedColor = ThemeColorUtils.colorPrimaryDisabledBtnAttribute(context)
        }

        shapeType = typedArray.getInt(R.styleable.CoreButton_btn_shape_type, 1)
        alignmentDistnce =
            typedArray.getFloat(
                R.styleable.CoreButton_btn_text_alignment_from_left,
                alignmentDistnce
            )

        drawablePaddingLeft =
            typedArray.getFloat(
                R.styleable.CoreButton_btn_drawable_padding_left,
                defaultDrawablePaddingLeft
            )
        drawablePaddingRight =
            typedArray.getFloat(
                R.styleable.CoreButton_btn_drawable_padding_right,
                defaultDrawablePaddingRight
            )
        drawablePaddingTop =
            typedArray.getFloat(R.styleable.CoreButton_btn_drawable_padding_top, drawablePaddingTop)

        roundRadius = typedArray.getDimensionPixelSize(
            R.styleable.CoreButton_btn_round_radius,
            resources.getDimensionPixelSize(R.dimen.round_radius)
        )

        labelTextSize = typedArray.getDimensionPixelSize(
            R.styleable.CoreButton_btn_text_size,
            resources.getDimensionPixelSize(R.dimen.label_text_size)
        ).toFloat()

        typedArray.recycle()
        paint.color = defaultStateColor
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        this.setWillNotDraw(false)
        this.isDrawingCacheEnabled = true
        this.isClickable = true
        this.setBackgroundColor(resources.getColor(R.color.transparent))

        /* text paint styling */

        paintText.color = labelTextColor
        paintText.textSize = labelTextSize
        paintText.textAlign = Paint.Align.CENTER
        paintText.style = Paint.Style.FILL
        if (hasBoldText) {
            paintText.isFakeBoldText = true
        }

        Button(ContextThemeWrapper(context, R.attr.primaryButtonTheme))

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (btnSize != -1)
            setButtonDimension()
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap?

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)
        btnWeight = w
        btnHeight = h
        btnRadius = btnWeight / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (shapeType == 0) {
            canvas.drawCircle(
                (btnWeight / 2).toFloat(), (btnHeight / 2).toFloat(), btnRadius.toFloat(),
                paint
            )
        } else {
            rectF.set(0f, 0f, btnWeight.toFloat(), btnHeight.toFloat())
            canvas.drawRoundRect(rectF, roundRadius.toFloat(), roundRadius.toFloat(), paint)

            val yPos =
                (canvas.height / 2 - (paintText.descent() + paintText.ascent()) / 2)
            canvas.drawText(
                text.toString(),
                (btnWeight / alignmentDistnce).toFloat(),
                yPos,
                paintText
            )
        }

        if (null != drawable) {
            bitmapIcon = drawable?.let { this.drawableToBitmap(it) }!!


            when (drawablePositionType) {
                DRAWABLE_LEFT -> canvas.drawBitmap(
                    bitmapIcon,
                    (btnWeight / drawablePaddingLeft).toFloat(),    //position from left
                    (btnHeight / drawablePaddingTop).toFloat(),     // set y-position of drawableRight left from top
                    paintText
                )

                DRAWABLE_RIGHT -> canvas.drawBitmap(
                    bitmapIcon,
                    (btnWeight / drawablePaddingRight).toFloat(),       //position from left
                    (btnHeight / drawablePaddingTop).toFloat(),         // set y-position of drawableRight right
                    paintText
                )
                else ->
                    canvas.drawBitmap(
                        bitmapIcon,
                        (btnWeight / drawablePaddingTop).toFloat(),     //position from left
                        (btnHeight / drawablePaddingTop).toFloat(),     // set y-position of drawableRight right
                        paintText
                    )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                if (this.isEnabled) {
                    paint.color = pressedColor
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (this.isEnabled) {
                    paint.color = defaultStateColor
                    invalidate()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun enableButton(enable: Boolean) {
        if (!enable) {
//            defaultStateColor = resources.getColor(R.color.greyLight)
            defaultStateColor = ThemeColorUtils.colorPrimaryDisabledBtnAttribute(context)
            pressedColor = ThemeColorUtils.colorPressedBtnStateAttribute(context)
            paint.color = defaultStateColor
            invalidate()
        } else {
            defaultStateColor = ThemeColorUtils.colorPrimaryAttribute(context)
            pressedColor = ThemeColorUtils.colorPressedBtnStateAttribute(context)
            paint.color = defaultStateColor
            invalidate()
        }
        this.setEnabled(enable)

    }

    private fun setButtonDimension() {
        var dimensions: IntArray = intArrayOf()
        when (btnSize) {
            ButtonSize.MINI.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 36, 5)
            }
            ButtonSize.MINI_SMALL.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 30, 4) // used in card details
            }
            ButtonSize.MINI_MEDIUM.type -> {
                dimensions =
                    Utils.getDimensionsByPercentage(context, 43, 4) // used in more home screen
            }
            ButtonSize.MINI_LARGE.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 60, 5)  // used in Maps
            }
            ButtonSize.SMALL.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 55, 8)
            }
            ButtonSize.MEDIUM.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 70, 8)
            }
            ButtonSize.LARGE.type -> {
                dimensions = Utils.getDimensionsByPercentage(context, 80, 8)
            }
            ButtonSize.DYNAMIC.type -> {
                dimensions =
                    Utils.getDimensionsByPercentage(context, 0, 8)
            }
            else -> {
                throw IllegalStateException("Invalid button type found $btnSize")
            }
        }
        val params = layoutParams
        params.width = dimensions[0]
        params.height = dimensions[1]
        layoutParams = params
    }

    enum class ButtonSize(val type: Int) {
        SMALL(0),
        MEDIUM(1),
        LARGE(2),
        MINI_SMALL(3),
        MINI_MEDIUM(4),
        MINI_LARGE(5),
        MINI(6),
        DYNAMIC(7)
    }
}