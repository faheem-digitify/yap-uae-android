package co.yap.base

import android.content.Context
import android.content.res.Resources
import co.yap.app.YAPApplication
import co.yap.networking.AppData
import co.yap.networking.RetroNetwork
import org.junit.After
import org.mockito.Mock
import org.mockito.MockitoAnnotations

abstract class BaseTestCase {
    private var closeable: AutoCloseable? = null

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var appData: AppData

    @Mock
    lateinit var resources: Resources

    open fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        appData = AppData(baseUrl = "https://stg.yap.co")
        RetroNetwork.initWith(YAPApplication(), appData)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        closeable?.close()
    }
}