package co.yap.yapcore

sealed class Dispatcher {
    object Main : Dispatcher()
    object Background : Dispatcher()
    object LongOperation : Dispatcher()
}