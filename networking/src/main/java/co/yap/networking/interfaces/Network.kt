package co.yap.networking.interfaces

import android.content.Context
import co.yap.networking.AppData

internal interface Network {
    fun initWith(context: Context, appData: AppData)
    fun <T> createService(serviceInterface: Class<T>): T
}