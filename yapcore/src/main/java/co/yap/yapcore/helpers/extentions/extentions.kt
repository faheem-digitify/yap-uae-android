package co.yap.yapcore.helpers.extentions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Parcelable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.yap.modules.qrcode.BarcodeEncoder
import co.yap.modules.qrcode.BarcodeFormat
import co.yap.yapcore.R
import co.yap.yapcore.helpers.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.navigation.NavigationView
import java.io.IOException
import java.math.RoundingMode

@Keep
enum class ExtraType {
    STRING, INT, BOOLEAN, DOUBLE, LONG, PARCEABLE, SERIALIZEABLE, BUNDLE;
}

fun Intent.getValue(key: String, type: String): Any? {
    return if (hasExtra(key)) {
        return if (ExtraType.valueOf(type).name.isNotEmpty()) {
            when (ExtraType.valueOf(type)) {
                ExtraType.BOOLEAN ->
                    getBooleanExtra(key, false)
                ExtraType.STRING ->
                    getStringExtra(key)
                ExtraType.DOUBLE ->
                    getDoubleExtra(key, 0.0)
                ExtraType.INT ->
                    getIntExtra(key, 0)
                ExtraType.LONG ->
                    getLongExtra(key, 0)
                ExtraType.PARCEABLE ->
                    getParcelableExtra<Parcelable>(key)
                ExtraType.SERIALIZEABLE ->
                    getSerializableExtra(key)
                ExtraType.BUNDLE ->
                    getBundleExtra(key)
            }
        } else null
    } else return null
}

fun Activity.preventTakeScreenShot(isPrevent: Boolean) {
    if (isPrevent)
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    else
        window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

fun ImageView.loadImage(path: String) {
    Glide.with(this)
        .load(path).centerCrop()
        .into(this)
}

fun ImageView.loadCardImage(path: String?) {
    Glide.with(this)
        .load(path)
        .placeholder(R.drawable.card_place_holder)
        .error(R.drawable.card_spare)
        .into(this)
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun AppCompatActivity.replaceFragment(tag: String?, id: Int, fragment: Fragment) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(id, fragment, tag)
    fragmentTransaction.commit()
}

fun RecyclerView.fixSwipeToRefresh(refreshLayout: SwipeRefreshLayout): RecyclerViewSwipeToRefresh {
    return RecyclerViewSwipeToRefresh(refreshLayout).also {
        this.addOnScrollListener(it)
    }
}


class RecyclerViewSwipeToRefresh(private val refreshLayout: SwipeRefreshLayout) :
    RecyclerView.OnScrollListener() {

    private val directionUp = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        refreshLayout.isEnabled = !(recyclerView?.canScrollVertically(directionUp) ?: return)
    }

}

fun View?.hideKeyboard() {
    this?.let { v ->
        val imm =
            this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

fun NavigationView?.navViewWidth(percent: Int) {
    this?.let {
        val params = it.layoutParams
        params.width = Utils.getDimensionInPercent(it.context, true, percent)
    }
}

fun Context?.isNetworkAvailable(): Boolean {
    return this?.let {
        val connectivityManager =
            it.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            connectivityManager.activeNetworkInfo?.let {
                return connectivityManager.activeNetworkInfo.isConnected
            } ?: false
        } ?: false
    } ?: false
}

fun TextView.makeLinks(
    vararg links: Pair<String, View.OnClickListener>,
    @ColorInt color: Int = 0,
    underline: Boolean = false,
    isBold: Boolean = false
) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = underline
                if (isBold) ds.typeface = Typeface.DEFAULT_BOLD
            }
        }

        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (color != 0) {
            spannableString.setSpan(
                ForegroundColorSpan(color),
                startIndexOfLink,
                startIndexOfLink + link.first.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun String.getCountryTwoDigitCodeFromThreeDigitCode(): String {
    if (this.isEmpty()) {
        return this
    }

    return this.substring(0, 2);
}

fun Double?.roundVal(): Double {
    val floatingMultiplier = (this ?: 0.0) * 100
    val rounded =
        floatingMultiplier.toBigDecimal().setScale(2, RoundingMode.HALF_UP)?.toDouble()
    val floatingDivisor = (rounded ?: 0.0).div(100)
    return floatingDivisor.toBigDecimal().setScale(2, RoundingMode.HALF_UP)?.toDouble() ?: 0.0
}

fun Double?.roundValHalfEven(): Double {
    val floatingMultiplier = (this ?: 0.0) * 100
    val rounded =
        floatingMultiplier.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)?.toDouble()
    val floatingDivisor = (rounded ?: 0.0).div(100)
    return floatingDivisor.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)?.toDouble() ?: 0.0
}

fun Context?.startSmsConsent() {
    this?.let {
        SmsRetriever.getClient(it).startSmsUserConsent(null)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
}

fun Context.generateQrCode(resourceKey: String): Drawable? {
    var drawable: Drawable? = null
    try {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap =
            barcodeEncoder.encodeBitmap(resourceKey, BarcodeFormat.QR_CODE, 400, 400)
        drawable = BitmapDrawable(resources, bitmap)
        return drawable
    } catch (e: Exception) {
    }
    return drawable
}

fun Context?.getJsonDataFromAsset(fileName: String): String? {
    val jsonString: String
    try {
        jsonString = this?.assets?.open(fileName)?.bufferedReader().use { it?.readText() ?: "" }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}