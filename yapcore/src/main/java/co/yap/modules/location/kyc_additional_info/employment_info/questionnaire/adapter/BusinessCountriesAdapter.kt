package co.yap.modules.location.kyc_additional_info.employment_info.questionnaire.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.R
import co.yap.yapcore.databinding.ItemBusinessCountryBinding

class BusinessCountriesAdapter(private val list: MutableList<String>) :
    BaseBindingRecyclerAdapter<String, RecyclerView.ViewHolder>(list) {

    override fun getLayoutIdForViewType(viewType: Int): Int = R.layout.item_business_country

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return BusinessCountriesItemViewHolder(binding as ItemBusinessCountryBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is BusinessCountriesItemViewHolder) {
            holder.onBind(list[position])
        }
    }
}

class BusinessCountriesItemViewHolder(private val itemBusinessCountryBinding: ItemBusinessCountryBinding) :
    RecyclerView.ViewHolder(itemBusinessCountryBinding.root) {

    fun onBind(
        countryName: String
    ) {
        itemBusinessCountryBinding.countryName = countryName
    }
}
