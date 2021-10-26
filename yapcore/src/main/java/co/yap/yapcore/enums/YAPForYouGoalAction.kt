package co.yap.yapcore.enums

import co.yap.widgets.CoreButton

sealed class YAPForYouGoalAction {
    class Button(
        val title: String,
        val enabled: Boolean,
        val controllerOnAction: String? = null,
        val buttonSize: CoreButton.ButtonSize = CoreButton.ButtonSize.LARGE
    ) : YAPForYouGoalAction()

    object None : YAPForYouGoalAction()
}