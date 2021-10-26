package co.yap.widgets.currency

import android.content.Context
import android.text.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.TextViewCompat
import co.yap.widgets.DrawableClickEditText
import co.yap.yapcore.R
import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.helpers.extentions.getColors
import co.yap.yapcore.managers.SessionManager
import java.text.DecimalFormat
import java.util.*

class CoreEditText : AppCompatEditText {
    private var _currencySymbol: String? = null
    private var currency: String? = null
        set(value) {
            field = value
            decimalDigits =
                Utils.getConfiguredDecimals(currency ?: SessionManager.getDefaultCurrency())
        }
    private var _showCurrency = false
    private var _showCommas = false
    private var decimalDigits: Int =
        Utils.getConfiguredDecimals(SessionManager.getDefaultCurrency())
    private var textToDisplay: String? = null
    private var maxLength: Int = resources.getInteger(R.integer.unitsCount)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(
        context: Context,
        attrs: AttributeSet?
    ) {
        // Setting Default Parameters
        _currencySymbol = Currency.getInstance(Locale.getDefault()).symbol
        _showCurrency = false
        _showCommas = true

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            val attrArray =
                context.obtainStyledAttributes(attrs, R.styleable.CoreEditText, 0, 0)
            try {

                if (attrArray.hasValue(R.styleable.EasyMoneyWidgets_currency_symbol)) {
                    var currnecy =
                        attrArray.getString(R.styleable.EasyMoneyWidgets_currency_symbol)
                    if (currnecy == null) currnecy =
                        Currency.getInstance(Locale.getDefault()).symbol
                    currnecy?.let { setCurrencySymbol(currnecy) }
                }

                _showCurrency =
                    attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_currency, false)
                _showCommas = attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_commas, true)
                if (attrArray.hasValue(R.styleable.EasyMoneyWidgets_em_currency))
                    currency = attrArray.getString(R.styleable.EasyMoneyWidgets_em_currency)
                decimalDigits = attrArray.getInteger(
                    R.styleable.EasyMoneyWidgets_decimalDigits,
                    Utils.getConfiguredDecimals(currency ?: SessionManager.getDefaultCurrency())
                )
                if (attrArray.hasValue(R.styleable.EasyMoneyWidgets_android_maxLength)) {
                    maxLength = attrArray.getInteger(
                        R.styleable.EasyMoneyWidgets_android_maxLength,
                        resources.getInteger(R.integer.unitsCount)
                    )
                }
            } finally {
                attrArray.recycle()
            }
        }
        setSingleLine()

        filters =
            arrayOf(InputFilter.LengthFilter(maxLength))//arrayOf<InputFilter>(LengthFilter(units))
        // Add Text Watcher for Decimal formatting
        initTextWatchers()
    }

    /**
     * Get the value of the text without any commas and currency.
     * For example, if the edit text value is $ 1,34,000.60 then this method will return 134000.60
     *
     * @return A string of the raw value in the text field
     */
    fun getValueString(): String {
        var string = text?.toString()
        if (string?.contains(",") == true) {
            string = string.replace(",", "")
        }
        if (string?.contains(" ") == true) {
            string = string.substring(string.indexOf(" ") + 1, string.length)
        }
        return string ?: ""
    }

    private fun updateValue(text: String) {
        setText(text)
    }

    /**
     * Shows the commas in the text. (Default is shown).
     */
    fun showCommas() {
        _showCommas = true
        updateValue(text.toString())
    }

    /**
     * Hides the commas in the text. (Default is shown).
     */
    fun hideCommas() {
        _showCommas = false
        updateValue(text.toString())
    }

    private fun getIntValue(value: String?): Double {
        if (!TextUtils.isEmpty(value)) {
            try {
                return value?.toDouble() ?: 0.0
            } catch (e: NumberFormatException) {
            }
        }
        return 0.0
    }

    /**
     * Get the value of the text without any commas and currency.
     * For example, if the edit text value is $ 1,34,000.60 then this method will return 134000.60
     *
     * @return A int of the raw value in the text field
     */
    fun getValueInt(): Double {
        return getIntValue(getValueString())
    }

    /**
     * Whether currency is shown in the text or not. (Default is true)
     *
     * @return true if the currency is shown otherwise false.
     */
    fun isShowCurrency(): Boolean {
        return _showCurrency
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param locale the locale of new symbol. (Defaul is Locale.US)
     */
    fun setCurrencySymbol(locale: Locale?) {
        setCurrencySymbol(Currency.getInstance(locale).symbol)
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param currency the currency object of new symbol. (Defaul is Locale.US)
     */
    fun setCurrencySymbol(currency: Currency) {
        setCurrencySymbol(currency.symbol)
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     *
     * @param newSymbol the new symbol of currency in string
     */
    fun setCurrencySymbol(newSymbol: String) {
        _currencySymbol = newSymbol
        updateValue(text.toString())
    }

    /**
     * Shows the currency in the text. (Default is shown).
     */
    fun showCurrencySymbol() {
        setShowCurrency(true)
    }

    /**
     * Get the value of the text with formatted commas and currency.
     * For example, if the edit text value is $ 1,34,000.60 then this method will return exactly $ 1,34,000.60
     *
     * @return A string of the text value in the text field
     */
    fun getFormattedString(): String? {
        return text.toString()
    }

    /**
     * Hides the currency in the text. (Default is shown).
     */
    fun hideCurrencySymbol() {
        setShowCurrency(false)
    }

    private fun setShowCurrency(value: Boolean) {
        _showCurrency = value
        updateValue(text.toString())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updateValue(text.toString())
    }

    private fun getDecoratedStringFromNumber(number: Long): String? {
        val numberPattern = "#,###,###,###"
        var decoStr = ""
        val formatter =
            DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat
        if (_showCommas && !_showCurrency) formatter.applyPattern(numberPattern)
        else if (_showCommas && _showCurrency) formatter.applyPattern(
            "$_currencySymbol $numberPattern"
        )
        else if (!_showCommas && _showCurrency) formatter.applyPattern("$_currencySymbol ")
        else if (!_showCommas && !_showCurrency) {
            decoStr = number.toString() + ""
            return decoStr
        }
        decoStr = formatter.format(number)
        return decoStr
    }

    private fun initTextWatchers() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                this@CoreEditText.removeTextChangedListener(this)
                try {
                    var originalString: String?
                    val longval: Long
                    originalString = getValueString()
                    longval = originalString.toLong()
                    val formattedString = getDecoratedStringFromNumber(longval)
                    textToDisplay = formattedString
                    setText(textToDisplay)
                    setSelection(text?.length ?: 0)
                } catch (nfe: java.lang.NumberFormatException) {
//                    nfe.printStackTrace();
                    // setText(backupString)
                    val valStr = getValueString()
                    if (valStr.isEmpty()) {
                        setText("")
                        textToDisplay = text.toString()
                    } else {
                        // Some decimal number
                        if (valStr.contains(".")) {
                            if (valStr.indexOf(".") == valStr.length - 1) {
                                // decimal has been currently put
                                val front = getDecoratedStringFromNumber(
                                    valStr.substring(
                                        0,
                                        valStr.length - 1
                                    ).toLong()
                                )
                                textToDisplay = "$front."
                                setText(textToDisplay)
                            } else {
                                val nums =
                                    getValueString().split("\\.".toRegex()).toTypedArray()
                                if (nums[1].length <= decimalDigits) {
                                    val front =
                                        getDecoratedStringFromNumber(nums[0].toLong())
                                    textToDisplay = front + "." + nums[1]
                                    setText(textToDisplay)
                                } else {
                                    val front =
                                        getDecoratedStringFromNumber(nums[0].toLong())
                                    textToDisplay =
                                        front + "." + nums[1].substring(0, decimalDigits)
                                    setText(textToDisplay)
                                }
                            }
                        }
                    }
                    setSelection(text?.length ?: 0)
                }
                this@CoreEditText.addTextChangedListener(this)
            }

            override fun afterTextChanged(editable: Editable) {
                this@CoreEditText.removeTextChangedListener(this)
                if (editable.isNotEmpty())
                    setText(textToDisplay)
                setSelection(text?.length ?: 0)
                this@CoreEditText.addTextChangedListener(this)
            }
        })
    }

    public override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
    }
}