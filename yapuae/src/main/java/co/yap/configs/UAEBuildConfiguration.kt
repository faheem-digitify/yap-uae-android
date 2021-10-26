package co.yap.configs

import android.content.Context
import android.util.Base64
import co.yap.app.YAPApplication
import co.yap.networking.AppData
import co.yap.networking.RetroNetwork
import co.yap.networking.interfaces.NetworkConstraintsListener
import co.yap.yapcore.config.BuildConfigManager
import co.yap.yapcore.constants.Constants
import co.yap.yapcore.enums.ProductFlavour
import co.yap.yapcore.helpers.AuthUtils
import co.yap.yapcore.helpers.NetworkConnectionManager
import co.yap.yapcore.helpers.SharedPreferenceManager
import java.util.*

class UAEBuildConfiguration {
    var configManager: BuildConfigManager? = null

    fun configure(
        context: Context,
        flavour: String,
        buildType: String,
        versionName: String,
        versionCode: String,
        applicationId: String,
        googleMapsApiKey: String
    ): BuildConfigManager {
        val productFlavour = getProductFlavour(flavour)

        configManager = BuildConfigManager(
            md5 = md5(productFlavour).decode(),
            sha1 = sha1(productFlavour).decode(),
            sha256 = sha256(productFlavour).decode(),
            leanPlumSecretKey = leanPlumKey(productFlavour, buildType).first,
            leanPlumKey = leanPlumKey(productFlavour, buildType).second,
            adjustToken = adjustToken(productFlavour),
            baseUrl = baseUrl(productFlavour),
            buildType = buildType,
            flavor = flavour,
            versionName = versionName,
            versionCode = versionCode,
            applicationId = applicationId,
            sslPin1 = sslPin1(productFlavour),
            sslPin2 = sslPin2(productFlavour),
            sslPin3 = sslPin3(productFlavour),
            sslHost = sslHost(productFlavour),
            spayServiceId = samsungSpayServiceId(productFlavour)
        )
        YAPApplication.configManager = configManager
        initNetworkLayer(context)
        setAppUniqueId(context)
        return configManager!!
    }

    private fun getProductFlavour(flavour: String): ProductFlavour {
        return when (flavour) {
            ProductFlavour.DEV.flavour -> {
                ProductFlavour.DEV
            }
            ProductFlavour.QA.flavour -> {
                ProductFlavour.QA
            }
            ProductFlavour.STG.flavour -> {
                ProductFlavour.STG
            }
            ProductFlavour.PREPROD.flavour -> {
                ProductFlavour.PREPROD
            }
            ProductFlavour.PROD.flavour -> {
                ProductFlavour.PROD
            }
            else -> ProductFlavour.INTERNAL
        }
    }

    private fun sha1(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD, ProductFlavour.PREPROD -> "ODU6OUY6NjM6N0M6NjI6N0I6Qjc6N0E6MDg6RTQ6OEI6MDY6OUU6M0U6MkQ6RTU6MEQ6OEM6Mjg6MjU="
            ProductFlavour.STG -> "REI6QTg6REE6OTg6RUY6ODA6QkY6ODQ6MDQ6RDE6NzM6Rjg6QzE6RjE6QzA6MTU6NTk6MjA6MTY6RDI="
            ProductFlavour.QA -> ""
            ProductFlavour.DEV -> ""
            ProductFlavour.INTERNAL -> ""
        }
    }

    private fun md5(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD, ProductFlavour.PREPROD -> "MDg6NzM6ODQ6RTI6NEM6NTc6RTU6MUU6OEY6ODU6RTM6OTg6MUM6NDM6Qjg6NEE="
            ProductFlavour.STG -> "MjU6ODQ6MUY6RTE6RjE6QTg6QzI6NTg6N0I6QUU6RUE6QjM6NDE6NjU6NzY6RkU="
            ProductFlavour.QA -> ""
            ProductFlavour.DEV -> ""
            ProductFlavour.INTERNAL -> ""
        }
    }

    private fun sha256(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "ODY6QTE6MzQ6NEU6RkM6OTQ6M0I6NzA6Mjk6MjE6OUU6M0I6NzA6MzM6NDI6RUM6M0M6NjI6M0E6MkI6MEU6N0M6QkM6MDc6RTU6N0Q6M0M6Mjk6RTg6MkE6Q0Y6NTM="
            ProductFlavour.PREPROD -> "ODY6QTE6MzQ6NEU6RkM6OTQ6M0I6NzA6Mjk6MjE6OUU6M0I6NzA6MzM6NDI6RUM6M0M6NjI6M0E6MkI6MEU6N0M6QkM6MDc6RTU6N0Q6M0M6Mjk6RTg6MkE6Q0Y6NTM="
            ProductFlavour.STG -> "QTQ6QUM6MTQ6RjM6REQ6RDg6NTc6RTk6RkM6QUM6N0M6MDk6NkM6QTQ6MEQ6RUM6QjU6MEU6RTE6OTY6QTI6RjA6Qjc6Q0M6QjA6MEY6MDc6MDA6Qzc6N0M6RjM6Qjg="
            ProductFlavour.QA -> ""
            ProductFlavour.DEV -> ""
            ProductFlavour.INTERNAL -> ""
        }
    }

    private fun baseUrl(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "https://ae-prod.yap.com/"
            ProductFlavour.PREPROD -> "https://ae-preprod.yap.com/"
            ProductFlavour.STG -> "https://stg.yap.co/"
            ProductFlavour.QA -> "https://qa-a.yap.co"
            ProductFlavour.DEV -> "https://dev-b.yap.co/"
            ProductFlavour.INTERNAL -> "https://stg.yap.co/"
        }
    }

    private fun adjustToken(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "xty7lf6skgsg"
            ProductFlavour.PREPROD -> "uv1oiis7wni8"
            ProductFlavour.DEV, ProductFlavour.QA, ProductFlavour.INTERNAL, ProductFlavour.STG -> "am0wjeshw5xc"
        }
    }

    private fun sslPin1(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "sha256/SK10shgwb9jAeBvxJXrkBmjL2joCFoSq2Sp1tGyOcQk="
            ProductFlavour.PREPROD -> "sha256/SK10shgwb9jAeBvxJXrkBmjL2joCFoSq2Sp1tGyOcQk="
            ProductFlavour.STG -> "sha256/ZrRL6wSXl/4lm1KItkcZyh56BGOoxMWUDJr7YVqE4no="
            ProductFlavour.QA, ProductFlavour.DEV, ProductFlavour.INTERNAL -> "sha256/e5L5CAoQjV0HFzAnunk1mPHVx1HvPxcfJYI0UtLyBwY="
        }
    }

    private fun sslPin2(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD, ProductFlavour.PREPROD, ProductFlavour.STG -> "sha256/8Rw90Ej3Ttt8RRkrg+WYDS9n7IS03bk5bjP/UXPtaY8="
            ProductFlavour.QA, ProductFlavour.DEV, ProductFlavour.INTERNAL -> "sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA="
        }
    }

    private fun sslPin3(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD, ProductFlavour.PREPROD, ProductFlavour.STG -> "sha256/Ko8tivDrEjiY90yGasP6ZpBU4jwXvHqVvQI0GS3GNdA="
            ProductFlavour.QA, ProductFlavour.DEV, ProductFlavour.INTERNAL -> "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f5O+NyoXuWtQdX1aI="
        }
    }

    private fun sslHost(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "*.yap.com"
            ProductFlavour.PREPROD -> "*.yap.com"
            ProductFlavour.STG, ProductFlavour.QA, ProductFlavour.DEV, ProductFlavour.INTERNAL -> "*.yap.co"
        }
    }

    private fun leanPlumKey(
        productFlavour: ProductFlavour,
        buildType: String
    ): Pair<String, String> {

        return if (productFlavour == ProductFlavour.DEV && buildType == "debug") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "dev_2ssrA8Mh1BazUIZHqIQabRP0a76cQwZ1MYfHsJpODMQ"
            )
        } else if (productFlavour == ProductFlavour.DEV && buildType == "release") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "prod_KX4ktWrg5iHyP12VbRZ92U0SOVXyYrcWk5B68TfBAW0"
            )
        } else if (productFlavour == ProductFlavour.QA && buildType == "debug") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "dev_2ssrA8Mh1BazUIZHqIQabRP0a76cQwZ1MYfHsJpODMQ"
            )
        } else if (productFlavour == ProductFlavour.QA && buildType == "release") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "prod_KX4ktWrg5iHyP12VbRZ92U0SOVXyYrcWk5B68TfBAW0"
            )
        } else if (productFlavour == ProductFlavour.STG && buildType == "debug") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "dev_2ssrA8Mh1BazUIZHqIQabRP0a76cQwZ1MYfHsJpODMQ"
            )
        } else if (productFlavour == ProductFlavour.STG && buildType == "release") {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "prod_KX4ktWrg5iHyP12VbRZ92U0SOVXyYrcWk5B68TfBAW0"
            )
        } else if (productFlavour == ProductFlavour.PREPROD && buildType == "debug") {
            Pair(
                "app_jvEgXTi9zZUpoFck8XVxVY4zBgAEYZrPVTliIuaO0IQ",
                "dev_HnmEVN0GDZbhInJjmX767e7InveRC23LkSokuLLuA3s"
            )
        } else if (productFlavour == ProductFlavour.PREPROD && buildType == "release") {
            Pair(
                "app_jvEgXTi9zZUpoFck8XVxVY4zBgAEYZrPVTliIuaO0IQ",
                "prod_EjIC6dCuGaGr36p2qRvG3GkRIhuYf9vgBEGjQ3jBqLM"
            )
        } else if (productFlavour == ProductFlavour.PROD && buildType == "debug") {
            Pair(
                "app_DtOp3ipxDUi9AM7Bg3jv351hZ4DVrLgC9JZX4L46lIc",
                "dev_RAFVBmDKypdOr3kbd326JUoqGLr8iSvt2Lei4BK48qk"
            )
        } else if (productFlavour == ProductFlavour.PROD && buildType == "release") {
            Pair(
                "app_DtOp3ipxDUi9AM7Bg3jv351hZ4DVrLgC9JZX4L46lIc",
                "prod_MfjUF6Sh3GuNE2RtQMkXZTeCUSTS3K0v2CLeGCp0gzk"
            )
        } else {
            Pair(
                "app_OjUbwCEcWfawOQzYABPyg5R7y9sFLgFm9C1JdgIa3Qk",
                "prod_MfjUF6Sh3GuNE2RtQMkXZTeCUSTS3K0v2CLeGCp0gzk"
            )
        }
    }

    private fun samsungSpayServiceId(productFlavour: ProductFlavour): String {
        return when (productFlavour) {
            ProductFlavour.PROD -> "9f189cfac32b46d9b5c284"
            ProductFlavour.PREPROD, ProductFlavour.STG, ProductFlavour.QA, ProductFlavour.DEV, ProductFlavour.INTERNAL -> "9f2b7fca270c4f3c81d99e"
        }
    }

    private fun String.decode(): String {
        return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
    }


    private fun setAppUniqueId(context: Context) {
        var uuid: String?
        val sharedPrefs = SharedPreferenceManager.getInstance(context)
        sharedPrefs.setThemeValue(Constants.THEME_YAP)
        uuid = sharedPrefs.getValueString(Constants.KEY_APP_UUID)
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            sharedPrefs.save(Constants.KEY_APP_UUID, uuid)
        }
    }

    private fun initNetworkLayer(context: Context) {
        RetroNetwork.initWith(context, getAppDataForNetwork(configManager))
        NetworkConnectionManager.init(context)

        RetroNetwork.listenNetworkConstraints(object : NetworkConstraintsListener {
            override fun onInternetUnavailable() {
            }

            override fun onCacheUnavailable() {
            }

            override fun onSessionInvalid() {
                AuthUtils.navigateToSoftLogin(context)
            }
        })
    }


    private fun getAppDataForNetwork(configManager: BuildConfigManager?): AppData {
        return AppData(
            flavor = configManager?.flavor ?: "",
            build_type = configManager?.buildType ?: "",
            baseUrl = configManager?.baseUrl ?: "",
            sslPin1 = configManager?.sslPin1 ?: "",
            sslPin2 = configManager?.sslPin2 ?: "",
            sslPin3 = configManager?.sslPin3 ?: "",
            sslHost = configManager?.sslHost ?: ""
        )
    }
}