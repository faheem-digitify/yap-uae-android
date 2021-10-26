package co.yap.modules.dashboard.store.cardplans.viewmodels

import android.app.Application
import android.net.Uri
import android.widget.VideoView
import co.yap.yapuae.R
import co.yap.modules.dashboard.store.cardplans.adaptors.CardPlansAdapter
import co.yap.modules.dashboard.store.cardplans.interfaces.ICardPlans
import co.yap.modules.dashboard.store.cardplans.states.CardPlansState
import co.yap.yapcore.SingleClickEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardPlansViewModel(application: Application) :
    CardPlansBaseViewModel<ICardPlans.State>(application), ICardPlans.ViewModel {
    override val state: CardPlansState = CardPlansState()
    override var cardAdapter: CardPlansAdapter = CardPlansAdapter(mutableListOf(), null)
    override var clickEvent: SingleClickEvent = SingleClickEvent()

    override fun handlePressOnView(id: Int) {
        clickEvent.setValue(id)
    }

    override fun onCreate() {
        super.onCreate()
        getCards()?.let { cardAdapter.setData(it) }
    }

    override fun iniVideoView(video: VideoView) {
        CoroutineScope(Dispatchers.Default).launch {
            val uri =
                Uri.parse("android.resource://" + context.packageName + "/" + R.raw.video_all_card_plans)
            video.layoutParams =
                parentViewModel?.setViewDimensions(32, video)
            video.setVideoURI(uri)
            video.start()
            launch {
                video.setOnCompletionListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    video.start()
                }
            }
        }

    }
}
