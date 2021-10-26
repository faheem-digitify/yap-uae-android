package co.yap.modules.dashboard.cards.analytics.interfaces

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import co.yap.modules.dashboard.cards.analytics.models.AnalyticsItem
import co.yap.networking.transactions.responsedtos.TxnAnalytic
import co.yap.widgets.CoreCircularImageView
import co.yap.yapcore.IBase
import co.yap.yapcore.SingleClickEvent
import com.google.android.material.imageview.ShapeableImageView

interface ICardAnalytics {
    interface View : IBase.View<ViewModel> {
        fun setObservers()
    }

    interface ViewModel : IBase.ViewModel<State> {
        val clickEvent: SingleClickEvent
        var selectedModel: MutableLiveData<AnalyticsItem>
        fun fetchCardCategoryAnalytics(currentMonth: String)
        fun fetchCardMerchantAnalytics(currentMonth: String)
        fun handlePressOnView(id: Int)
        fun isDataAvailableForSelectedMonth(tab : Int): Boolean
        var type : ObservableField<String>
        fun setPieChartIcon(image : CoreCircularImageView)
    }

    interface State : IBase.State {
        var monthlyAverageString: String
        var monthlyMerchantAverageString: String
        var currencyType: String?
        var monthlyCategoryAvgAmount: String?
        var monthlyMerchantAvgAmount: String?
        var selectedItemSpentValue: String
        var selectedItemPercentage: String
        var selectedItemName: String?
        var selectedItemPosition: ObservableInt
        var totalSpent: String?
        var totalCategorySpent: String?
        var totalMerchantSpent: String?
        var selectedMonth: String?
        var monthCount: Int
        var selectedTxnAnalyticsItem: ObservableField<TxnAnalytic>
        var nextMonth: Boolean?
        var previousMonth: Boolean?
        var displayMonth: String
        var selectedTab : ObservableField<Int>
    }
}