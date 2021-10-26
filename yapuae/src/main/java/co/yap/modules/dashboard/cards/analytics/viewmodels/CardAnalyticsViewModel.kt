package co.yap.modules.dashboard.cards.analytics.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.yapuae.R
import co.yap.modules.dashboard.cards.analytics.interfaces.ICardAnalytics
import co.yap.modules.dashboard.cards.analytics.main.viewmodels.CardAnalyticsBaseViewModel
import co.yap.modules.dashboard.cards.analytics.models.AnalyticsItem
import co.yap.modules.dashboard.cards.analytics.states.CardAnalyticsState
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.translation.Strings
import co.yap.widgets.CoreCircularImageView
import co.yap.yapcore.SingleClickEvent
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import co.yap.yapcore.helpers.DateUtils
import co.yap.yapcore.helpers.DateUtils.FORMAT_MONTH_YEAR
import co.yap.yapcore.helpers.DateUtils.SIMPLE_DATE_FORMAT
import co.yap.yapcore.helpers.extentions.setCircularDrawable
import co.yap.yapcore.helpers.extentions.toFormattedCurrency
import co.yap.yapcore.managers.SessionManager
import java.util.*

class CardAnalyticsViewModel(application: Application) :
    CardAnalyticsBaseViewModel<ICardAnalytics.State>(application = application),
    ICardAnalytics.ViewModel {
    override val state: CardAnalyticsState = CardAnalyticsState(application)
    override var selectedModel: MutableLiveData<AnalyticsItem> = MutableLiveData()
    val repository: TransactionsRepository = TransactionsRepository
    override val clickEvent: SingleClickEvent = SingleClickEvent()
    private var currentDate: Date? = Date()
    private var listOfMonths: List<Date> = arrayListOf()

    override var type: ObservableField<String> = ObservableField("merchant-category-id")

    override fun onCreate() {
        super.onCreate()
        setToolBarTitle(getString(Strings.screen_card_analytics_tool_bar_title))
        val startDate = SessionManager.user?.creationDate ?: ""
        val endDate = DateUtils.dateToString(
            Date(),
            SIMPLE_DATE_FORMAT, DateUtils.TIME_ZONE_Default
        )
        listOfMonths = DateUtils.geMonthsBetweenTwoDates(
            startDate,
            endDate
        )
        setSelectedDate(currentDate)
        state.previousMonth = isPreviousIconEnabled(listOfMonths, currentDate)
    }

    override fun handlePressOnView(id: Int) {
        when (id) {
            R.id.ivPrevious -> {
                currentDate = DateUtils.getPriviousMonthFromCurrentDate(
                    listOfMonths,
                    currentDate
                )
                fetchAnalytics(currentDate)
                state.previousMonth = isPreviousIconEnabled(listOfMonths, currentDate)
                state.nextMonth = true
                trackEventWithScreenName(FirebaseEvent.SCROLL_DATES)
            }
            R.id.ivNext -> {
                currentDate = DateUtils.getNextMonthFromCurrentDate(
                    listOfMonths,
                    currentDate
                )
                fetchAnalytics(currentDate)
                state.nextMonth = isNextIconEnabled(listOfMonths, currentDate)
                state.previousMonth = true
                trackEventWithScreenName(FirebaseEvent.SCROLL_DATES)

            }
        }
        clickEvent.setValue(id)
    }

    override fun fetchCardCategoryAnalytics(currentMonth: String) {
        parentViewModel?.categoryAnalyticsItemLiveData?.value?.clear()
        launch {
            state.loading = true
            when (val response = repository.getAnalyticsByCategoryName(
                currentMonth
            )) {
                is RetroApiResponse.Success -> {
                    response.data.data?.let { analyticsDTO ->
                        state.monthlyCategoryAvgAmount =
                            response.data.data?.monthlyAvgAmount?.toString()

                        state.totalCategorySpent = response.data.data?.totalTxnAmount.toString()
                            .toFormattedCurrency(
                                showCurrency = true,
                                currency = state.currencyType ?: SessionManager.getDefaultCurrency()
                            )
                        state.totalSpent = state.totalCategorySpent
                        clickEvent.postValue(Constants.CATEGORY_AVERAGE_AMOUNT_VALUE)
                        parentViewModel?.categoryAnalyticsItemLiveData?.value =
                            analyticsDTO.txnAnalytics
                        parentViewModel?.state?.isNoDataFound?.set(isDataAvailableForSelectedMonth(0))

                    }

                    fetchCardMerchantAnalytics(currentMonth)

                }
                is RetroApiResponse.Error -> {
                    state.loading = false
                    state.toast = response.error.message

                }
            }
        }

    }

    override fun fetchCardMerchantAnalytics(currentMonth: String) {
        parentViewModel?.merchantAnalyticsItemLiveData?.value?.clear()
        launch {
            when (val response = repository.getAnalyticsByMerchantName(currentMonth)) {
                is RetroApiResponse.Success -> {
                    response.data.data?.let { merchantResponse ->
                        state.monthlyMerchantAvgAmount =
                            merchantResponse.monthlyAvgAmount?.toString()
                        state.totalMerchantSpent = merchantResponse.totalTxnAmount.toString()
                            .toFormattedCurrency(
                                showCurrency = true,
                                currency = state.currencyType ?: SessionManager.getDefaultCurrency()
                            )

                        parentViewModel?.merchantAnalyticsItemLiveData?.value =
                            merchantResponse.txnAnalytics

                    }
                    state.loading = false
                    parentViewModel?.state?.isNoDataFound?.set(isDataAvailableForSelectedMonth(1))
                }
                is RetroApiResponse.Error -> {
                    state.toast = response.error.message
                    state.loading = false
                }
            }
        }
    }

    override fun isDataAvailableForSelectedMonth(tab: Int): Boolean {
        return when {
            tab == 0 && parentViewModel?.categoryAnalyticsItemLiveData?.value?.size == 0 -> false
            tab == 1 && parentViewModel?.merchantAnalyticsItemLiveData?.value?.size == 0 -> false
            else -> true
        }

    }

    private fun isPreviousIconEnabled(listOfMonths: List<Date>, currentDate: Date?): Boolean {
        var index: Int = -1
        currentDate?.let {
            for (i in listOfMonths.indices) {
                if (DateUtils.isDateMatched(listOfMonths[i], currentDate)) {
                    index = i
                    break
                }
            }
        }

        return index - 1 >= 0
    }

    private fun isNextIconEnabled(listOfMonths: List<Date>, currentDate: Date?): Boolean {
        var index: Int = -1
        currentDate?.let {
            for (i in listOfMonths.indices) {
                if (DateUtils.isDateMatched(listOfMonths[i], currentDate)) {
                    index = i
                    break
                }
            }
        }

        return listOfMonths.size >= index + 2
    }

    private fun fetchAnalytics(currentDate: Date?) {
        if (currentDate != null) {
            fetchCardCategoryAnalytics(
                DateUtils.reformatToLocalString(
                    currentDate,
                    SIMPLE_DATE_FORMAT
                )
            )
            setSelectedDate(currentDate)
        }
    }

    private fun setSelectedDate(currentDate: Date?) {
        state.displayMonth =
            currentDate?.let { DateUtils.getStartAndEndOfMonthAndDay(it) } ?: ""
        state.selectedMonth = DateUtils.dateToString(currentDate, FORMAT_MONTH_YEAR, false)
        parentViewModel?.state?.currentSelectedMonth = state.selectedMonth ?: ""
        parentViewModel?.state?.currentSelectedDate =
            DateUtils.dateToString(currentDate, SIMPLE_DATE_FORMAT, false)
    }

    override fun setPieChartIcon(image: CoreCircularImageView) {
        image.setCircularDrawable(
            title = state.selectedTxnAnalyticsItem.get()?.title ?: "",
            url = state.selectedTxnAnalyticsItem.get()?.logoUrl ?: "",
            position = state.selectedItemPosition.get(),
            /*type = type.get() ?: "merchant-name",*/
            type = "merchant-category-id",
            showBackground = (state.selectedTxnAnalyticsItem.get()?.logoUrl.isNullOrEmpty() || state.selectedTxnAnalyticsItem.get()?.logoUrl == " ")
        )
    }
}