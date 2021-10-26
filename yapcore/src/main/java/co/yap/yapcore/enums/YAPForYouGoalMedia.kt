package co.yap.yapcore.enums

sealed class YAPForYouGoalMedia {
    class LottieAnimation(val jsonFileName: String) : YAPForYouGoalMedia()
    class Image(val imageName: String) : YAPForYouGoalMedia()
    class ImageUrl(val imageUrl: String) : YAPForYouGoalMedia()
    object None : YAPForYouGoalMedia()
}