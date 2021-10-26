package co.yap.sendmoney.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import co.yap.networking.models.RetroApiResponse
import co.yap.networking.transactions.TransactionsRepository
import co.yap.networking.transactions.requestdtos.RemittanceFeeRequest
import co.yap.networking.transactions.responsedtos.transaction.RemittanceFeeResponse
import co.yap.yapcore.BaseViewModel
import co.yap.yapcore.IBase
import co.yap.yapcore.enums.FeeType
import co.yap.yapcore.helpers.extentions.parseToDouble

abstract class SMFeeViewModel<S : IBase.State>(application: Application) :
    BaseViewModel<S>(application) {
    private val transactionRepository: TransactionsRepository = TransactionsRepository
    var feeTiers: List<RemittanceFeeResponse.RemittanceFee.TierRateDTO> = arrayListOf()
    var isFeeReceived: MutableLiveData<Boolean> = MutableLiveData(false)
    var isAPIFailed: MutableLiveData<Boolean> = MutableLiveData(false)
    var feeType: String = ""
    var slabCurrency: String = "AED"
    var feeCurrency: String = "AED"
    var fixedAmount: Double = 0.0
    var feeAmount: String = ""
    var vat: String = ""
    val updatedFee: MutableLiveData<String> = MutableLiveData("0.0")

    fun getTransferFees(
        productCode: String?,
        feeRequest: RemittanceFeeRequest = RemittanceFeeRequest()
    ) {
        launch {
            when (val response =
                transactionRepository.getTransactionFeeWithProductCode(productCode, feeRequest)) {
                is RetroApiResponse.Success -> {
                    feeType = response.data.data?.feeType ?: "FLAT"
                    feeCurrency = response.data.data?.feeCurrency ?: "AED"
                    slabCurrency = response.data.data?.slabCurrency ?: "AED"
                    fixedAmount = response.data.data?.fixedAmount ?: 0.0
                    response.data.data?.tierRateDTOList?.let {
                        feeTiers =
                            response.data.data?.tierRateDTOList as ArrayList<RemittanceFeeResponse.RemittanceFee.TierRateDTO>
                        isFeeReceived.value = true
                    }
                }
                is RetroApiResponse.Error -> {
                    state.toast = response.error.message
                    state.loading = false
                    isAPIFailed.value = true
                }
            }
        }
    }

    fun updateFees(
        enterAmount: String, isTopUpFee: Boolean = false,
        fxRate: Double = 1.0
    ) {
        var result = "0.0"
        if (!feeTiers.isNullOrEmpty()) {
            result = when (feeType) {
                FeeType.FLAT.name -> getFlatFee(enterAmount, fxRate)
                FeeType.TIER.name -> getFeeFromTier(enterAmount, fxRate)
                else -> {
                    "0.0"
                }
            }
        }
        updatedFee.value = result
    }

    fun getFlatFee(
        enterAmount: String,
        fxRate: Double = 1.0
    ): String {
        val feeTier = feeTiers.firstOrNull() ?: return "0.0"
        return getCalculatedFeeAmount(feeTier, fxRate, enterAmount)
    }

    fun getFeeFromTier(
        enterAmount: String,
        fxRate: Double = 1.0
    ): String {
        return if (!enterAmount.isBlank()) {
            val feeTier = getTierFromEnterAmount(enterAmount, feeTiers) ?: return "0.0"
            return getCalculatedFeeAmount(feeTier, fxRate, enterAmount)
        } else {
            "0.0"
        }
    }

    private fun getTierFromEnterAmount(
        enterAmount: String,
        tiers: List<RemittanceFeeResponse.RemittanceFee.TierRateDTO>
    ): RemittanceFeeResponse.RemittanceFee.TierRateDTO? {
        return tiers.firstOrNull { item ->
            (item.amountFrom ?: 0.0) <= enterAmount.parseToDouble()
                    && (item.amountTo ?: 0.0) >= enterAmount.parseToDouble()
        }
    }

    private fun getFeeAmount(
        feeTier: RemittanceFeeResponse.RemittanceFee.TierRateDTO,
        feeCurrency: String,
        fixedAmount: Double,
        fxRate: Double
    ): Double {
        return if (!feeCurrency.equals("AED", true)) {
            ((feeTier.feeAmount ?: 0.0) * fxRate).plus(fixedAmount)
        } else {
            (feeTier.feeAmount ?: 0.0).plus(fixedAmount)
        }
    }

    private fun getVatAmount(
        feeTier: RemittanceFeeResponse.RemittanceFee.TierRateDTO,
        feeAmount: Double
    ): Double = (feeAmount * (feeTier.vatPercentage?.parseToDouble()?.div(100) ?: 0.0))

    private fun getCalculatedFeeAmount(
        feeTier: RemittanceFeeResponse.RemittanceFee.TierRateDTO,
        fxRate: Double,
        enterAmount: String
    ): String {
        return if (feeTier.feeInPercentage == false) {
            val totalFee = getFeeAmount(feeTier, feeCurrency, fixedAmount, fxRate)
            val localVat = getVatAmount(feeTier, totalFee)
            vat = localVat.toString()
            feeAmount = totalFee.plus(localVat).toString()
            feeAmount
        } else {
            return calFeeInPercentage(enterAmount, feeTier, fxRate) ?: "0.0"
        }
    }

    private fun calFeeInPercentage(
        enterAmount: String,
        feeTier: RemittanceFeeResponse.RemittanceFee.TierRateDTO,
        fxRate: Double
    ): String? {
        var feeAmount =
            enterAmount.parseToDouble() * (feeTier.feePercentage?.parseToDouble()?.div(100) ?: 0.0)
        feeAmount = if (!feeCurrency.equals("AED", true) && !slabCurrency.equals("AED", true)) {
            (feeAmount * fxRate).plus(fixedAmount)
        } else {
            feeAmount.plus(fixedAmount)
        }
        val vatAmount = getVatAmount(feeTier, feeAmount)
        this.feeAmount = feeAmount.toString()
        this.vat = vatAmount.toString()
        return (feeAmount + vatAmount).toString()
    }
}