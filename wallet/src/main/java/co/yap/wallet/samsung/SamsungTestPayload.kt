package co.yap.wallet.samsung

//import android.content.Context
//import co.yap.wallet.R
//import co.yap.wallet.encriptions.encryption.*
//import co.yap.wallet.encriptions.json.GsonJsonEngine
//import co.yap.wallet.encriptions.utils.EncryptionUtils
//import co.yap.yapcore.helpers.DateUtils
//import java.io.IOException
//import java.security.GeneralSecurityException
//import java.util.*

//fun Context.getTestPayloadForSamsung(
//    cardNumber: String?,
//    expiryYear: String?,
//    expiryMonth: String?,
//    cardholderName: String?,
//    payload: (String) -> Unit
//) {
//    try {
//
////"yyyy-MM-dd'T'HH:mm"
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.MINUTE, 30)
//        val dataValidUntilTimestamp =
//            DateUtils.dateToString(calendar.time, "yyyy-MM-dd'T'HH:mm:ss'Z'", true)
//        //5381230100036491
////        val cardNumber = cardNo//"5381230100035253"
////        val expiryYear = expiryYear//"25"
////        val expiryMonth = expiryMonth//"07"
//        val source = "CARD_ADDED_VIA_APPLICATION"
////        val cardholderName = cardholderName//"JENNIFER WIGGINS"
//        // TAV Process start
//        val tavSignatureConfig = TAVSignatureConfigBuilder.aTAVSignatureConfig()
//            .withAccountExpiry("$expiryMonth$expiryYear")
//            .withAccountNumber(cardNumber)
//            .withTavFormat(TAVSignatureConfig.TAVFormat.TAV_FORMAT_2)
//            .withDataValidUntilTimestamp(dataValidUntilTimestamp)// "2021-03-25T16:10:59Z"
//            .withPrivateKey(
//                EncryptionUtils.loadDecryptionKey(
//                    resources.openRawResource(0)
//                )
//            ).build()
//
//        val base64DigitalSignature =
//            TAVSignatureMethod.createBase64DigitalSignature(tavSignatureConfig)
//        //TAV process End
//        val iss =
//            resources.openRawResource(0)
//        val cardInfoData =
//            "{\"cardInfoData\": {\"accountNumber\": \"$cardNumber\",\"expiryMonth\": \"$expiryMonth\",\"expiryYear\":\"$expiryYear\",\"source\": \"$source\",\"cardholderName\": \"$cardholderName\"}}"
//        val encryptionCertificate =
//            EncryptionUtils.loadEncryptionCertificate(iss)
//        val config =
//            FieldLevelEncryptionConfigBuilder.aFieldLevelEncryptionConfig()
//                .withEncryptionCertificate(encryptionCertificate)
//                .withEncryptionPath("$.cardInfoData", "$.cardInfo")
//                .withOaepPaddingDigestAlgorithm("SHA-256")
//                .withEncryptionCertificateFingerprintFieldName("publicKeyFingerprint")
////                .withEncryptionKeyFingerprintFieldName("publicKeyFingerprint")
//                .withEncryptedValueFieldName("encryptedData")
//                .withEncryptedKeyFieldName("encryptedKey")
//                .withIvFieldName("iv")
//                .withOaepPaddingDigestAlgorithmFieldName("oaepHashingAlgorithm")
//                .withTokenizationAuthenticationValueFieldName("tokenizationAuthenticationValue")
//                .withFieldValueEncoding(FieldLevelEncryptionConfig.FieldValueEncoding.HEX)
//                .build()
//        FieldLevelEncryption.withJsonEngine(GsonJsonEngine())
//        val finalPayload = FieldLevelEncryption.encryptPayloadWithTAV(
//            cardInfoData,
//            config, base64DigitalSignature
//        )
//
//        println("SamsungTestPayload Json>>$finalPayload")
//        payload.invoke(finalPayload)
//    } catch (e: IOException) {
//        e.printStackTrace()
//    } catch (e: GeneralSecurityException) {
//        e.printStackTrace()
//    } catch (e: EncryptionException) {
//        e.printStackTrace()
//    } catch (e: InvalidSignatureException) {
//        e.printStackTrace()
//    }

//}

