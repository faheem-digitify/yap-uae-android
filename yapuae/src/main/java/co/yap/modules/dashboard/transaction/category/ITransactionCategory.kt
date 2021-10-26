package co.yap.modules.dashboard.transaction.category

import android.app.Activity
import androidx.databinding.ObservableField
import co.yap.yapuae.databinding.FragmentMerchantAnalyticsBinding
import co.yap.yapuae.databinding.FragmentTransactionCategoryBinding
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.networking.transactions.responsedtos.transaction.TapixCategory
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent

interface ITransactionCategory {
    interface View : IBase.View<ViewModel>
    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        fun handlePressOnView(id: Int)
        fun fetchTransactionCategories()
        var categoryAdapter : TransactionCategoryAdapter
        fun selectCategory(data : TapixCategory, position : Int)
        var tapixCategories: MutableList<TapixCategory>
        var selectedCategory: ObservableField<TapixCategory>
        fun updateCategory(successCallBack: () -> Unit)
        fun setPreSelectedCategory(name: String)
    }

    interface State : IBase.State{
        var transactionId : ObservableField<String>
        var categoryName : ObservableField<String>
    }
}