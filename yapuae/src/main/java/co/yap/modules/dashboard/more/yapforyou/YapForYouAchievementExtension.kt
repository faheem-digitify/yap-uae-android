package co.yap.modules.dashboard.more.yapforyou

import android.graphics.Color
import co.yap.yapuae.R
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.yapcore.enums.AchievementStatus
import co.yap.yapcore.enums.AchievementType
import co.yap.yapcore.enums.YapForYouGoalType
import co.yap.yapcore.helpers.DateUtils
import java.util.*


fun ArrayList<AchievementResponse>.achievementImage(forAchievementType: AchievementType): Int {
    val achievement = this.firstOrNull { it.achievementType == forAchievementType.name }
    return when (achievement?.achievementType) {
        AchievementType.GET_STARTED.name -> if (achievement.isCompleted) R.drawable.ic_gs_badge else R.drawable.ic_gs_badge_faded
        AchievementType.UP_AND_RUNNING.name -> if (achievement.isCompleted) R.drawable.ic_uar_badge else R.drawable.ic_uar_badge_faded
        AchievementType.BETTER_TOGETHER.name -> if (achievement.isCompleted) R.drawable.ic_bt_badge else R.drawable.ic_bt_badge_faded
        AchievementType.TAKE_THE_LEAP.name -> if (achievement.isCompleted) R.drawable.ic_ttl_badge else R.drawable.ic_ttl_badge_faded
        AchievementType.YAP_STORE.name -> if (achievement.isCompleted) R.drawable.ic_ys_badge else R.drawable.ic_ys_badge_faded
        AchievementType.YOU_ARE_A_PRO.name -> if (achievement.isCompleted) R.drawable.ic_yrp_badge else R.drawable.ic_yrp_badge_faded
        else -> -1
    }
}

fun ArrayList<AchievementResponse>.achievementStatusIcon(forAchievementType: AchievementType): Int {
    return when (this.getAchievementStatus(forAchievementType)) {
        AchievementStatus.LOCKED -> R.drawable.ic_lock
        AchievementStatus.COMPLETED -> R.drawable.ic_baseline_check
        AchievementStatus.IN_COMPLETE -> R.drawable.ic_yap_it
    }
}

fun ArrayList<AchievementResponse>.isLocked(forAchievementType: AchievementType): Boolean =
    this.firstOrNull { it.achievementType == forAchievementType.name }?.isForceLocked ?: true

fun ArrayList<AchievementResponse>.percentage(forAchievementType: AchievementType): Int =
    this.firstOrNull { it.achievementType == forAchievementType.name }?.percentage?.toInt() ?: 0

fun ArrayList<AchievementResponse>.getLastUpdatedDate(forAchievementType: AchievementType): Date {
    val lastUpdatedDateString =
        this.firstOrNull { it.achievementType == forAchievementType.name }?.lastUpdated ?: ""
    return DateUtils.stringToDate(lastUpdatedDateString, DateUtils.SERVER_DATE_FULL_FORMAT) ?: Date(
        0
    )
}

fun ArrayList<AchievementResponse>.tintColor(forAchievementType: AchievementType): Int {
    return this.firstOrNull { it.achievementType == forAchievementType.name }?.color?.let {
        Color.parseColor(it)
    } ?: return -1
}

fun ArrayList<AchievementResponse>.isCompleted(
    forGoalType: YapForYouGoalType
): Boolean =
    this.flatMap { it.goals?.toList() ?: emptyList() }.firstOrNull {
        it.achievementTaskType == forGoalType.name
    }?.completion ?: false


fun ArrayList<AchievementResponse>.getLockedStatus(forGoalType: YapForYouGoalType): Boolean =
    this.flatMap { it.goals?.toList() ?: emptyList() }.firstOrNull {
        it.achievementTaskType == forGoalType.name
    }?.locked ?: false

fun ArrayList<AchievementResponse>.getAchievementStatus(forAchievementType: AchievementType): AchievementStatus {
    return when {
        this.isLocked(forAchievementType) -> AchievementStatus.LOCKED
        this.firstOrNull { it.achievementType == forAchievementType.name }?.isCompleted == true -> AchievementStatus.COMPLETED
        else -> {
            AchievementStatus.IN_COMPLETE
        }
    }
}