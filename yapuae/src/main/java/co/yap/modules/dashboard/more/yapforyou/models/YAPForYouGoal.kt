package co.yap.modules.dashboard.more.yapforyou.models

import co.yap.yapcore.enums.YAPForYouGoalAction
import co.yap.yapcore.enums.YAPForYouGoalMedia
import co.yap.yapcore.enums.YapForYouGoalType

data class YAPForYouGoal(
    var type: YapForYouGoalType,
    var title: String,
    var action: YAPForYouGoalAction = YAPForYouGoalAction.None,
    var completed: Boolean,
    var description: String = "",
    var media: YAPForYouGoalMedia? = null,
    var completedMedia: YAPForYouGoalMedia? = null,
    var locked: Boolean? = null
)