package co.yap.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import co.yap.app.modules.refreal.DeepLinkNavigation
import co.yap.app.ui.login.VerifyPassCodePresenterActivity
import co.yap.configs.UAEBuildConfiguration
import co.yap.localization.LocaleManager
import co.yap.modules.dashboard.main.activities.YapDashboardActivity
import co.yap.modules.dummy.ActivityNavigator
import co.yap.modules.dummy.NavigatorProvider
import co.yap.modules.kyc.activities.DocumentsDashboardActivity
import co.yap.modules.others.helper.Constants.START_REQUEST_CODE
import co.yap.yapcore.config.BuildConfigManager
import co.yap.yapcore.constants.Constants.EXTRA
import co.yap.yapcore.helpers.NetworkConnectionManager
import co.yap.yapcore.helpers.extentions.longToast
import co.yap.yapcore.initializeAdjustSdk
import com.facebook.appevents.AppEventsLogger
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.leanplum.Leanplum
import com.leanplum.LeanplumActivityHelper
import timber.log.Timber

class AAPApplication : Application(), NavigatorProvider {
    private val uaeBuildConfigurations: UAEBuildConfiguration = UAEBuildConfiguration()

    override fun onCreate() {
        super.onCreate()
        initializePkConfigs()
        initFireBase()
        initAllModules()
    }

    private fun initializePkConfigs() {
        val uaeConfig = uaeBuildConfigurations.configure(
            context = applicationContext,
            flavour = BuildConfig.FLAVOR,
            buildType = BuildConfig.BUILD_TYPE,
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE.toString(),
            applicationId = BuildConfig.APPLICATION_ID,
            googleMapsApiKey = getString(R.string.google_maps_key)
        )

        initializeAdjustSdk(uaeConfig)
        inItLeanPlum(uaeConfig)
    }

    private fun initAllModules() {
        LivePersonChat.getInstance(applicationContext).registerToLivePersonEvents()
        initFacebook()
    }

    private fun initFireBase() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    private fun inItLeanPlum(configManager:BuildConfigManager) {
        Leanplum.setApplicationContext(this)
        //Parser.parseVariables(this)
        LeanplumActivityHelper.enableLifecycleCallbacks(this)

        if (configManager.isReleaseBuild()) {
            Leanplum.setAppIdForProductionMode(
                configManager.leanPlumSecretKey,
                configManager.leanPlumKey
            )
        } else {
            Leanplum.setAppIdForDevelopmentMode(
                configManager.leanPlumSecretKey,
                configManager.leanPlumKey
            )
        }
        Leanplum.setIsTestModeEnabled(false)
        Leanplum.start(this)
    }

    private fun initFacebook() {
        AppEventsLogger.activateApp(this)
    }


    override fun onTerminate() {
        NetworkConnectionManager.destroy(this)
        super.onTerminate()
    }

    override fun provideNavigator(): ActivityNavigator {
        return object : ActivityNavigator {
            override fun startEIDNotAcceptedActivity(activity: FragmentActivity) {

//                activity.startActivity(
//                    Intent(
//                        activity,
//                        InvalidEIDActivity::class.java
//                    )
//                )
            }

            override fun startVerifyPassCodePresenterActivity(
                activity: FragmentActivity, bundle: Bundle,
                completionHandler: ((resultCode: Int, data: Intent?) -> Unit)?
            ) {
                try {
                    val intent = Intent(activity, VerifyPassCodePresenterActivity::class.java)
                    intent.putExtra(EXTRA, bundle)
                    (activity as AppCompatActivity).startForResult(intent) { result ->
                        completionHandler?.invoke(result.resultCode, result.data)
                    }.onFailed { result ->
                        completionHandler?.invoke(result.resultCode, result.data)
                    }

                } catch (e: Exception) {
                    if (e is ClassNotFoundException) {
                        longToast("Something went wrong")
                        activity.startActivityForResult(
                            Intent(activity, VerifyPassCodePresenterActivity::class.java),
                            START_REQUEST_CODE
                        )
                    }
                }

            }

            override fun startDocumentDashboardActivity(
                activity: FragmentActivity
            ) {
                var intent = Intent(activity, DocumentsDashboardActivity::class.java)
                intent.putExtra("GO_ERROR", true)
                activity.startActivity(intent)
            }

            override fun handleDeepLinkFlow(activity: AppCompatActivity, flowId: String?) {
                if (activity is YapDashboardActivity) {
                    DeepLinkNavigation.getInstance(activity).handleDeepLinkFlow(flowId)
                }
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }
}
