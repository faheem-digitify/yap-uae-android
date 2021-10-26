package co.yap.networking.transactions.responsedtos.achievement

import co.yap.networking.models.ApiResponse
import com.google.gson.annotations.SerializedName

data class AchievementsResponseDTO(
    @SerializedName("data")
    val data: List<AchievementResponse>?
):ApiResponse()