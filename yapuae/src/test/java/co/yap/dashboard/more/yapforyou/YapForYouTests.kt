package co.yap.dashboard.more.yapforyou

import co.yap.app.YAPApplication
import co.yap.base.BaseTestCase
import co.yap.modules.dashboard.more.yapforyou.YAPForYouAchievementsComposer
import co.yap.modules.dashboard.more.yapforyou.YAPForYouItemsComposer
import co.yap.modules.dashboard.more.yapforyou.viewmodels.YAPForYouViewModel
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.yapcore.enums.AchievementType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class YapForYouTests : BaseTestCase() {

    lateinit var sut: YAPForYouViewModel

    @Before
    override fun setUp() {
        super.setUp()
        sut = YAPForYouViewModel(YAPApplication())
    }

    @Test
    fun test_after_onboarding_get_started_is_current_achievement() {
        val response: ArrayList<AchievementResponse> = arrayListOf(
            AchievementResponse(
                achievementType = "GET_STARTED",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "UP_AND_RUNNING",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "BETTER_TOGETHER",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "TAKE_THE_LEAP",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "YAP_STORE",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "YOU_ARE_A_PRO",
                percentage = 0.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            )
        )
        val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()
        val mockComposerItems =
            itemsComposer.compose(response)
        val expectedValue =
            mockComposerItems.first { it.achievementType == AchievementType.GET_STARTED }
        val currentAchievement = sut.getCurrentAchievement(mockComposerItems)

        Assert.assertEquals(expectedValue, currentAchievement)
    }

    @Test
    fun test_current_achievement_is_the_one_with_highest_completion_percentage() {
        val response: ArrayList<AchievementResponse> = arrayListOf(
            AchievementResponse(
                achievementType = "GET_STARTED",
                percentage = 50.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "UP_AND_RUNNING",
                percentage = 98.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "TAKE_THE_LEAP",
                percentage = 95.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            )
        )
        val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()
        val mockComposerItems =
            itemsComposer.compose(response)
        val expectedValue =
            mockComposerItems.first { it.achievementType == AchievementType.UP_AND_RUNNING }
        val currentAchievement = sut.getCurrentAchievement(mockComposerItems)

        Assert.assertEquals(expectedValue, currentAchievement)

    }

    @Test
    fun test_a_completed_achievement_is_not_the_current_achievement() {
        val response: ArrayList<AchievementResponse> = arrayListOf(
            AchievementResponse(
                achievementType = "GET_STARTED",
                percentage = 100.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "UP_AND_RUNNING",
                percentage = 75.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            ),
            AchievementResponse(
                achievementType = "TAKE_THE_LEAP",
                percentage = 95.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            )
        )
        val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()
        val mockComposerItems =
            itemsComposer.compose(response)
        val expectedValue =
            mockComposerItems.first { it.achievementType == AchievementType.TAKE_THE_LEAP }
        val currentAchievement = sut.getCurrentAchievement(mockComposerItems)

        Assert.assertEquals(expectedValue, currentAchievement)

    }

    @Test
    fun test_if_two_achievements_have_highest_and_equal_percentage_then_last_updated_one_is_current_achievement() {
        val response: ArrayList<AchievementResponse> = arrayListOf(
            AchievementResponse(
                achievementType = "GET_STARTED",
                percentage = 75.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = "2021-01-06T19:17:17"
            ),
            AchievementResponse(
                achievementType = "UP_AND_RUNNING",
                percentage = 75.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = "2021-01-08T19:17:17"
            ),
            AchievementResponse(
                achievementType = "TAKE_THE_LEAP",
                percentage = 60.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = null
            )
        )
        val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()
        val mockComposerItems =
            itemsComposer.compose(response)
        val expectedValue =
            mockComposerItems.first { it.achievementType == AchievementType.UP_AND_RUNNING }
        val currentAchievement = sut.getCurrentAchievement(mockComposerItems)

        Assert.assertEquals(expectedValue, currentAchievement)
    }

    @Test
    fun test_most_recent_updated_achievement_is_not_current_achievement_if_percentage_is_less_than_other_achievement() {
        val response: ArrayList<AchievementResponse> = arrayListOf(
            AchievementResponse(
                achievementType = "GET_STARTED",
                percentage = 25.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = "2021-01-06T19:17:17"
            ),
            AchievementResponse(
                achievementType = "UP_AND_RUNNING",
                percentage = 75.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = "2021-01-08T19:17:17"
            ),
            AchievementResponse(
                achievementType = "TAKE_THE_LEAP",
                percentage = 25.0,
                color = "",
                isForceLocked = false,
                goals = arrayListOf(),
                lastUpdated = "2021-01-09T19:17:17"
            )
        )
        val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()
        val mockComposerItems =
            itemsComposer.compose(response)
        val expectedValue =
            mockComposerItems.first { it.achievementType == AchievementType.UP_AND_RUNNING }
        val currentAchievement = sut.getCurrentAchievement(mockComposerItems)

        Assert.assertEquals(expectedValue, currentAchievement)
    }


    /*private fun getMockApiResponse(): List<AchievementResponse> {
        val gson = GsonBuilder().create();
        val itemType = object : TypeToken<List<AchievementResponse>>() {}.type

        return gson.fromJson<List<AchievementResponse>>(readJsonFile(), itemType)
    }

    @Throws(IOException::class)
    private fun readJsonFile(): String? {
        val br =
            BufferedReader(InputStreamReader(FileInputStream("../yap/src/main/assets/y4yMockResponse.json")))
        val sb = StringBuilder()
        var line: String? = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }*/
}