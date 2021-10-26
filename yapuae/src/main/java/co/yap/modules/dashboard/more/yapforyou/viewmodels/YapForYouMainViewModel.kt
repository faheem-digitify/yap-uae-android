package co.yap.modules.dashboard.more.yapforyou.viewmodels

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import co.yap.modules.dashboard.more.yapforyou.YAPForYouAchievementsComposer
import co.yap.modules.dashboard.more.yapforyou.YAPForYouItemsComposer
import co.yap.modules.dashboard.more.yapforyou.interfaces.IYapForYouMain
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.modules.dashboard.more.yapforyou.states.YapForYouMainState
import co.yap.networking.interfaces.IRepositoryHolder
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.networking.transactions.responsedtos.achievement.AchievementTask
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.Dispatcher
import co.yap.yapcore.SingleClickEvent
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets

class YapForYouMainViewModel(application: Application) :
    BaseViewModel<IYapForYouMain.State>(application),
    IYapForYouMain.ViewModel, IRepositoryHolder<TransactionsRepository> {
    override val repository: TransactionsRepository = TransactionsRepository

    override val state: YapForYouMainState = YapForYouMainState()
    override var clickEvent: SingleClickEvent = SingleClickEvent()
    override var selectedAchievement: ObservableField<Achievement?> = ObservableField()
    override var selectedAchievementGoal: ObservableField<YAPForYouGoal?> = ObservableField()
    override var achievementsList: MutableList<Achievement> = mutableListOf()
    override var achievementsResponse: MutableLiveData<ArrayList<AchievementResponse>> =
        MutableLiveData(arrayListOf())
    private val itemsComposer: YAPForYouItemsComposer = YAPForYouAchievementsComposer()

    override fun handlePressButton(id: Int) {
        clickEvent.setValue(id)
    }

    override fun getAchievements() {
        launch(Dispatcher.Background) {
            state.viewState.postValue(true)
            val response = repository.getAchievements()
            launch {
                when (response) {
                    is RetroApiResponse.Success -> {
                        state.viewState.value = false
                        achievementsList.clear()
                        achievementsList.addAll(itemsComposer.compose(response.data.data as ArrayList<AchievementResponse>))
                        achievementsResponse.value =
                            (response.data.data as ArrayList<AchievementResponse>)
                    }

                    is RetroApiResponse.Error -> {
                        state.viewState.value = false
                        showToast(response.error.message)
                    }
                }
            }
        }
    }

    override fun getMockApiResponse() {
        launch {
            val list: ArrayList<AchievementResponse> = arrayListOf()
            state.loading = true
            delay(500)
            val mainObj = JSONObject(loadTransactionFromJsonAssets(context) ?: "")
            val mainDataList = mainObj.getJSONArray("data")
            for (i in 0 until mainDataList.length()) {
                val tasksList: ArrayList<AchievementTask> = arrayListOf()
                val parentArrayList = mainDataList.getJSONObject(i)
                val title: String = parentArrayList.getString("title")
                val color: String = parentArrayList.getString("colorCode")
                val percentage: Double = parentArrayList.getDouble("percentage")
                val acheivementType: String = parentArrayList.getString("achievementType")
                val order: Int = parentArrayList.getInt("order")
                val lock = parentArrayList.getBoolean("lock")
                val lastUpdatedDate = parentArrayList.getString("lastUpdated")
                val tasks = parentArrayList.getJSONArray("tasks")
                for (j in 0 until tasks.length()) {
                    tasksList.add(
                        AchievementTask(
                            title = tasks.getJSONObject(j).getString("title"),
                            completion = tasks.getJSONObject(j).getBoolean("completion"),
                            achievementTaskType = tasks.getJSONObject(j).getString("taskType"),
                            locked = tasks.getJSONObject(j).getBoolean("lock")
                        )
                    )
                }
                list.add(
                    AchievementResponse(
                        title = title,
                        color = color,
                        percentage = percentage,
                        achievementType = acheivementType,
                        order = order,
                        isForceLocked = lock,
                        lastUpdated = lastUpdatedDate,
                        goals = tasksList
                    )
                )
            }
            state.loading = false
            achievementsList.clear()
            achievementsList.addAll(itemsComposer.compose(list))
            achievementsResponse.value = list
        }

    }

    private fun loadTransactionFromJsonAssets(context: Context): String? {
        val json: String?
        try {
            val `is` = context.assets.open("yapachievement.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}
