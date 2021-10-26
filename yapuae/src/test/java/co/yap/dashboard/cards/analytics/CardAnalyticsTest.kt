package co.yap.dashboard.cards.analytics

import co.yap.yapcore.helpers.DateUtils
import org.junit.Assert
import org.junit.Test
import java.util.*

class CardAnalyticsTest {
    @Test
    fun testIfMonthsBetweenTwoMonthsIsNotNull() {
        val first = "2020-01-06"
        val second = "2021-01-04"
        Assert.assertEquals(
            13,
            DateUtils.geMonthsBetweenTwoDates(first, second)?.size
        )
    }

    @Test
    fun testIfPriviousDateIsNull() {
        val first = "2020-01-02"
        val second = "2021-01-05"
        val currentDate = DateUtils.stringToDate(first, "yyyy-MM-dd")
        Assert.assertEquals(
            null,
            DateUtils.getPriviousMonthFromCurrentDate(
                DateUtils.geMonthsBetweenTwoDates(
                    first,
                    second
                ), currentDate
            )
        )
    }

    @Test
    fun testIfNextDateIsNull() {
        val first = "2020-01-02"
        val second = "2021-01-04"
        val currentDate = DateUtils.stringToDate(second, "yyyy-MM-dd")
        Assert.assertEquals(
            null,
            DateUtils.getNextMonthFromCurrentDate(
                DateUtils.geMonthsBetweenTwoDates(first, second),
                currentDate
            )
        )
    }

    @Test
    fun DisableVisibilityOfPriviousMonthButton() {
        val first = "2020-01-02"
        val second = "2021-01-05"
        val currentDate = "2020-02-02"
        Assert.assertEquals(
            true,
            isPreviousIconDisabled(
                DateUtils.geMonthsBetweenTwoDates(
                    first,
                    second
                ), DateUtils.stringToDate(currentDate, "yyyy-MM-dd")
            )
        )
    }

    @Test
    fun showVisibilityOfNextMonthButton() {
        val first = "2020-01-02"
        val second = "2021-01-05"
        val currentDate = "2021-01-05"
        Assert.assertEquals(
            false,
            isNextIconDisabled(
                DateUtils.geMonthsBetweenTwoDates(
                    first,
                    second
                ), DateUtils.stringToDate(currentDate, "yyyy-MM-dd")
            )
        )
    }

    @Test
    fun getMonthStartDayAndEndDay() {
        val currentDate = "2020-12-05"
        val convertedDate = DateUtils.stringToDate(currentDate, "yyyy-MM-dd")!!
        Assert.assertEquals(
            "Jan 1 - Jan 31",
            DateUtils.getStartAndEndOfMonthAndDay(convertedDate)
        )
    }

    //DateUtils.getStartAndEndOfMonthAndDay(it)
    private fun isPreviousIconDisabled(listOfMonths: List<Date>, currentDate: Date?): Boolean {
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

    private fun isNextIconDisabled(listOfMonths: List<Date>, currentDate: Date?): Boolean {
        var index: Int = -1
        currentDate?.let {
            for (i in listOfMonths.indices) {
                if (DateUtils.isDateMatched(listOfMonths[i], currentDate)) {
                    index = i
                    break
                }
            }
        }

        return listOfMonths.size >= index + 1
    }
}