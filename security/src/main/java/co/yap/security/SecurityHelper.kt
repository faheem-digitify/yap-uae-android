package co.yap.security

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.security.MessageDigest

class SecurityHelper(
    private val context: Context,
    private val originalSign: AppSignature?,
    private val validator: SignatureValidator
) {
    init {
        System.loadLibrary("signature-lib")
        validateAppSignature()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun bytesFromJNI(context: Context): ByteArray?

    private fun validateAppSignature() {
        getApplicationSignature(context).find { it == originalSign }?.let {
            validator.onValidate(isValid = true, originalSign = originalSign)
        } ?: validator.onValidate(isValid = false, originalSign = originalSign)
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun getApplicationSignature(context: Context): List<AppSignature> {
        val packageInfo = getPackageInfo(context)
        val signaturesList = arrayListOf<AppSignature>()
        packageInfo?.let {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                it.signingInfo.apkContentsSigners.map { sign ->
//                    signaturesList.add(
//                        AppSignature(
//                            sha1 = getGivenSignature("SHA", sign.toByteArray()),
//                            md5 = getGivenSignature("MD5", sign.toByteArray()),
//                            sha256 = getGivenSignature("SHA256", sign.toByteArray())
//                        )
//                    )
//                }
//            } else {
            if (it.signatures.isNotEmpty()) {
                val rawCertJava = it.signatures[0].toByteArray()
                val rawCertNative = bytesFromJNI(context)
                rawCertNative?.let { byteArray ->
                    val signatures = AppSignature(
                        sha1 = getGivenSignature("SHA", byteArray),
                        md5 = getGivenSignature("MD5", byteArray),
                        sha256 = getGivenSignature("SHA256", byteArray),
                        leanPlumSecretKey = "",
                        leanPlumKey = "",
                        adjustToken = "",
                        baseUrl = "",
                        buildType = "",
                        flavor = "",
                        versionName = "",
                        versionCode = "",
                        applicationId = "",
                        sslPin1 = "",
                        sslPin2 = "",
                        sslPin3 = "",
                        sslHost = "",
                        spayServiceId = ""
                    )
                    signaturesList.add(signatures)
                }
            }
        }
        return signaturesList
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            context.packageManager.getPackageInfo(
//                context.packageName,
//                PackageManager.GET_SIGNING_CERTIFICATES
//            )
//        } else {
        return context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNATURES
        )
//        }
    }

    private fun getGivenSignature(type: String, toByteArray: ByteArray): String? {
        val digestSHA256 = MessageDigest.getInstance(type)
        digestSHA256.update(toByteArray)
        return bytesToString(digestSHA256.digest())
    }

    private fun bytesToString(bytes: ByteArray): String? {
        val md5StrBuff = java.lang.StringBuilder()
        for (i in bytes.indices) {
            if (Integer.toHexString(0xFF and bytes[i].toInt()).length == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF and bytes[i].toInt()))
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF and bytes[i].toInt()))
            }
            if (bytes.size - 1 != i) {
                md5StrBuff.append(":")
            }
        }
        return md5StrBuff.toString()
    }
}