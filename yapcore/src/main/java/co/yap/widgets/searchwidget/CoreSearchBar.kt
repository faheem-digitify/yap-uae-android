package co.yap.widgets.searchwidget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import co.yap.yapcore.R
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.Utils.hideKeyboard
import kotlinx.android.synthetic.main.layout_core_searchview.view.*

class CoreSearchBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayoutCompat(context, attrs), TextWatcher, TextView.OnEditorActionListener {
    var searchingListener: SearchingListener? = null

    var cancelTextVisibility: Boolean? = null
        set(value) {
            field = value
            tvCancel.visibility =
                if (cancelTextVisibility == true) View.VISIBLE else View.GONE
            invalidate()

        }

    var view: View = LayoutInflater.from(context)
        .inflate(R.layout.layout_core_searchview, this, true)

    init {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.CoreSearchView, 0, 0)
            typedArray.recycle()
        }
    }

    fun initializeSearch(
        searchingListener: SearchingListener
    ) {
        this.searchingListener = searchingListener
        setFocus(view)
        addListeners()
        tvCancel.setOnClickListener { searchingListener?.onCancel() }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        if (etSearch.text.toString().isEmpty()) {
            searchData()
        } else if (!etSearch.text.toString().trim { it <= ' ' }
                .equals("", ignoreCase = true)
        ) {
            if (searchingListener != null) searchingListener?.onTypingSearch(etSearch.text.toString())
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (!etSearch.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                searchDataOnSearchKeyPressed()
            }
            hideKeyboard(v)
            return true
        }
        return false
    }

    private fun searchDataOnSearchKeyPressed() {
        searchingListener?.onSearchKeyPressed(etSearch.text.toString())
    }

    private fun searchData() {
        if (searchingListener != null) searchingListener?.onTypingSearch(etSearch.text.toString())
    }

    private fun setFocus(view: View) {
        etSearch.isFocusable = true
        Utils.requestKeyboard(view, request = true, forced = true)
    }

    private fun addListeners() {
        registerTextChangeListener()
        etSearch.setOnEditorActionListener(this)
    }

    private fun registerTextChangeListener() {
        etSearch.addTextChangedListener(this)
    }
}