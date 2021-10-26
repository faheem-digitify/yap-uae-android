package co.yap.modules.location.kyc_additional_info.tax_info

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.yap.translation.Strings
import co.yap.translation.Translator
import co.yap.yapcore.R
import co.yap.yapcore.databinding.ItemTaxInfoBinding
import co.yap.yapcore.helpers.extentions.afterTextChanged
import co.yap.yapcore.interfaces.OnItemClickListener

class TaxItemItemViewHolder(private val itemTaxInfoBinding: ItemTaxInfoBinding) :
    RecyclerView.ViewHolder(itemTaxInfoBinding.root) {

    fun onBind(
        taxModel: TaxModel,
        position: Int,
        onItemClickListener: OnItemClickListener?
    ) {
        itemTaxInfoBinding.viewModel =
            TaxInfoItemViewModel(
                taxModel,
                position,
                onItemClickListener
            )
        itemTaxInfoBinding.etTinNumber.afterTextChanged {
            onItemClickListener?.onItemClick(itemTaxInfoBinding.etTinNumber, it, -1)
        }

        if (position == 0) {
            itemTaxInfoBinding.bcountries.text =
                taxModel.countries.find { it.isoCountryCode2Digit == "AE" }?.getName()
        }
        itemTaxInfoBinding.bcountries.isEnabled = position != 0
        itemTaxInfoBinding.executePendingBindings()

        //Disable TIN for UAE
        itemTaxInfoBinding.optionsSpinner.setSelection(if (position == 0) taxModel.options.indexOfFirst { it == "No" } else 0)
        itemTaxInfoBinding.optionsSpinner.background =
            if (position == 0) itemTaxInfoBinding.reasonsSpinner.context.getDrawable(R.drawable.bg_spinner_empty) else itemTaxInfoBinding.reasonsSpinner.context.getDrawable(
                R.drawable.bg_spinner
            )
        itemTaxInfoBinding.optionsSpinner.isEnabled = (position != 0)
        itemTaxInfoBinding.optionsSpinner.viewTreeObserver.addOnGlobalLayoutListener {
            (itemTaxInfoBinding.optionsSpinner.selectedView as TextView).setTextColor(
                ContextCompat.getColor(
                    itemTaxInfoBinding.optionsSpinner.context,
                    R.color.colorPrimaryDark
                )
            )
        }


        //Disable TIN for UAE
        itemTaxInfoBinding.tvReason.text =
            Translator.getString(
                itemTaxInfoBinding.reasonsSpinner.context,
                if (position == 0) Strings.screen_tax_info_display_text_reason_no_tin_number_selected else Strings.screen_tax_info_display_text_reason_no_tin_number
            )
        itemTaxInfoBinding.reasonsSpinner.setSelection(if (position == 0) 0 else 0)
        itemTaxInfoBinding.reasonsSpinner.background =
            if (position == 0) itemTaxInfoBinding.reasonsSpinner.context.getDrawable(R.drawable.bg_spinner_empty) else itemTaxInfoBinding.reasonsSpinner.context.getDrawable(
                R.drawable.bg_spinner
            )
        itemTaxInfoBinding.reasonsSpinner.isEnabled = (position != 0)

        itemTaxInfoBinding.reasonsSpinner.viewTreeObserver.addOnGlobalLayoutListener {
            (itemTaxInfoBinding.reasonsSpinner.selectedView as? TextView)?.setTextColor(
                ContextCompat.getColor(
                    itemTaxInfoBinding.reasonsSpinner.context,
                    R.color.colorPrimaryDark
                )
            )
        }
    }

}
