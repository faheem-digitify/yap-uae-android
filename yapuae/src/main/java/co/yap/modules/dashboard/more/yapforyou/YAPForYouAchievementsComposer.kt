package co.yap.modules.dashboard.more.yapforyou

import co.yap.modules.dashboard.cards.paymentcarddetail.activities.PaymentCardDetailActivity
import co.yap.modules.dashboard.more.main.activities.MoreActivity
import co.yap.modules.dashboard.more.yapforyou.models.Achievement
import co.yap.modules.dashboard.more.yapforyou.models.YAPForYouGoal
import co.yap.modules.dashboard.yapit.addmoney.main.AddMoneyActivity
import co.yap.networking.transactions.responsedtos.achievement.AchievementResponse
import co.yap.translation.Strings
import co.yap.widgets.CoreButton
import co.yap.yapcore.enums.AchievementType
import co.yap.yapcore.enums.YAPForYouGoalAction
import co.yap.yapcore.enums.YAPForYouGoalMedia
import co.yap.yapcore.enums.YapForYouGoalType
import co.yap.yapcore.managers.SessionManager
import java.util.*


interface YAPForYouItemsComposer {
    fun compose(response: ArrayList<AchievementResponse>): ArrayList<Achievement>
}

class YAPForYouAchievementsComposer : YAPForYouItemsComposer {
    override fun compose(response: ArrayList<AchievementResponse>): ArrayList<Achievement> {
        return arrayListOf(
            makeAchievement(
                achievementType = AchievementType.GET_STARTED,
                title = AchievementType.GET_STARTED.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.OPEN_YOUR_YAP_ACCOUNT,
                        title = YapForYouGoalType.OPEN_YOUR_YAP_ACCOUNT.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_open_yap_account_description,
                        media = YAPForYouGoalMedia.Image("ic_spare_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SET_PIN,
                        title = YapForYouGoalType.SET_PIN.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_set_your_pin_description,
                        media = YAPForYouGoalMedia.Image("ic_set_pin"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_completed_set_pin_goal"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.TOP_UP,
                        title = YapForYouGoalType.TOP_UP.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_add_money_to_account_button_label,
                            enabled = true,
                            controllerOnAction = AddMoneyActivity::class.simpleName,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_add_money_to_account_description,
                        media = YAPForYouGoalMedia.LottieAnimation("add_money_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_add_money_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SET_PROFILE_PICTURE,
                        title = YapForYouGoalType.SET_PROFILE_PICTURE.title,

                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_set_a_profile_photo_button_label,
                            enabled = true,
                            controllerOnAction = MoreActivity::javaClass.name,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_set_a_profile_photo_description,
                        media = YAPForYouGoalMedia.LottieAnimation("set_profile_picture_lottie.json"),
                        completedMedia = SessionManager.user?.currentCustomer?.getPicture()?.let {
                            YAPForYouGoalMedia.ImageUrl(
                                it
                            )
                        } ?: YAPForYouGoalMedia.None,
                        response = response
                    )
                )
            ),
            makeAchievement(
                achievementType = AchievementType.UP_AND_RUNNING,
                title = AchievementType.UP_AND_RUNNING.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.LOCAL_USE,
                        title = YapForYouGoalType.LOCAL_USE.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_use_yap_locally_description,
                        media = YAPForYouGoalMedia.LottieAnimation("use_yap_locally.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_spare_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.FREEZE_UNFREEZE_CARD,
                        title = YapForYouGoalType.FREEZE_UNFREEZE_CARD.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_freeze_unfreeze_card_button_label,
                            enabled = true,
                            controllerOnAction = PaymentCardDetailActivity::class.simpleName,
                            buttonSize = CoreButton.ButtonSize.MEDIUM
                        ),
                        description = Strings.screen_yfy_freeze_unfreeze_card_description,
                        media = YAPForYouGoalMedia.LottieAnimation("freez_and_unfreeze_your_card_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.LottieAnimation("freez_and_unfreeze_your_card_lottie.json"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SPEND_AMOUNT_100,
                        title = YapForYouGoalType.SPEND_AMOUNT_100.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_set_a_profile_photo_description,
                        media = YAPForYouGoalMedia.LottieAnimation("spend_aed_amount.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_spend_aed_amount_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.EXPLORE_CARD_CONTROLS,
                        title = YapForYouGoalType.EXPLORE_CARD_CONTROLS.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_explore_card_control_description,
                        media = YAPForYouGoalMedia.Image("ic_card_controls"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_card_controls_completed"),
                        response = response
                    )
                )
            ),
            makeAchievement(
                achievementType = AchievementType.BETTER_TOGETHER,
                title = AchievementType.BETTER_TOGETHER.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.INVITE_FRIEND,
                        title = YapForYouGoalType.INVITE_FRIEND.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_invite_a_friend_button_label,
                            enabled = true,
                            controllerOnAction = YapForYouGoalType.INVITE_FRIEND.name,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_invite_a_friend_description,
                        media = YAPForYouGoalMedia.LottieAnimation("invite_friend_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_goal_invite_friend_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.MAKE_Y2Y_TRANSFER,
                        title = YapForYouGoalType.MAKE_Y2Y_TRANSFER.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_send_money_to_someone_description,
                        media = YAPForYouGoalMedia.LottieAnimation("make_a_y2y_transfer_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_make_y2y_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SPLIT_BILLS_WITH_YAP,
                        title = YapForYouGoalType.SPLIT_BILLS_WITH_YAP.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_split_bills_description,
                        media = YAPForYouGoalMedia.Image("ic_split_yap_bill"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SEND_MONEY_OUTSIDE_YAP,
                        title = YapForYouGoalType.SEND_MONEY_OUTSIDE_YAP.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_send_money_outside_yap_description,
                        media = YAPForYouGoalMedia.Image("ic_sm_out_side_yap"),
                        response = response
                    )
                )
            ),
            makeAchievement(
                achievementType = AchievementType.TAKE_THE_LEAP,
                title = AchievementType.TAKE_THE_LEAP.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.ORDER_SPARE_CARD,
                        title = YapForYouGoalType.ORDER_SPARE_CARD.title,
                        action = YAPForYouGoalAction.None,
                        description = Strings.screen_yfy_order_virtual_card_description,
                        media = YAPForYouGoalMedia.Image("ic_virtual_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.UPGRADE_TO_PRIME,
                        title = YapForYouGoalType.UPGRADE_TO_PRIME.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_upgrade_to_prime_button_label,
                            enabled = false,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_upgrade_to_prime_description,
                        media = YAPForYouGoalMedia.Image("ic_prime_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.UPGRADE_TO_METAL,
                        title = YapForYouGoalType.UPGRADE_TO_METAL.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_upgrade_to_prime_button_label,
                            enabled = false,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_go_metal_description,
                        media = YAPForYouGoalMedia.Image("ic_metal_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SETUP_MULTI_CURRENCY,
                        title = YapForYouGoalType.SETUP_MULTI_CURRENCY.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_set_multi_currency_account_button_label,
                            enabled = false,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_set_multi_currency_account_description,
                        media = YAPForYouGoalMedia.LottieAnimation("multicurrency_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_goal_setup_multi_currency_completed"),
                        response = response
                    )
                )
            ),
            makeAchievement(
                achievementType = AchievementType.YAP_STORE,
                title = AchievementType.YAP_STORE.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.GET_YAP_YOUNG,
                        title = YapForYouGoalType.GET_YAP_YOUNG.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_get_yap_young_button_label,
                            enabled = false,
                            buttonSize = CoreButton.ButtonSize.MEDIUM
                        ),
                        description = Strings.screen_yfy_get_yap_young_description,
                        media = YAPForYouGoalMedia.LottieAnimation("cards_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_goal_young_card_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.GET_YAP_HOUSEHOLD,
                        title = YapForYouGoalType.GET_YAP_HOUSEHOLD.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_signup_to_hh_button_label,
                            enabled = false,
                            buttonSize = CoreButton.ButtonSize.LARGE
                        ),
                        description = Strings.screen_yfy_signup_to_hh_description,
                        media = YAPForYouGoalMedia.Image("ic_hh_card"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SET_MISSIONS_ON_YAP_YOUNG,
                        title = YapForYouGoalType.SET_MISSIONS_ON_YAP_YOUNG.title,
                        description = Strings.screen_yfy_set_a_mission_description,
                        action = YAPForYouGoalAction.None,
                        media = YAPForYouGoalMedia.LottieAnimation("set_missions_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_goal_set_mission_young_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SALARY_TRANSFER_ON_YAP_HOUSEHOLD,
                        title = YapForYouGoalType.SALARY_TRANSFER_ON_YAP_HOUSEHOLD.title,
                        description = Strings.screen_yfy_pay_your_help_description,
                        action = YAPForYouGoalAction.None,
                        media = YAPForYouGoalMedia.Image("ic_hh_salary_transfer"),
                        response = response
                    )
                )
            ),
            makeAchievement(
                achievementType = AchievementType.YOU_ARE_A_PRO,
                title = AchievementType.YOU_ARE_A_PRO.title,
                response = response,
                goals = arrayListOf(
                    makeGoal(
                        type = YapForYouGoalType.INVITE_TEN_FRIENDS,
                        title = YapForYouGoalType.INVITE_TEN_FRIENDS.title,
                        action = YAPForYouGoalAction.Button(
                            title = Strings.screen_yfy_invite_ten_friends_button_label,
                            enabled = true,
                            controllerOnAction = YapForYouGoalType.INVITE_FRIEND.name,
                            buttonSize = CoreButton.ButtonSize.SMALL
                        ),
                        description = Strings.screen_yfy_invite_ten_friends_description,
                        media = YAPForYouGoalMedia.LottieAnimation("invite_friend_lottie.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_goal_invite_friend_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.SPEND_AMOUNT_1000,
                        title = YapForYouGoalType.SPEND_AMOUNT_1000.title,
                        description = Strings.screen_yfy_spend_thousand_description,
                        action = YAPForYouGoalAction.None,
                        media = YAPForYouGoalMedia.LottieAnimation("spend_aed_amount.json"),
                        completedMedia = YAPForYouGoalMedia.Image("ic_spend_aed_amount_completed"),
                        response = response
                    ),
                    makeGoal(
                        type = YapForYouGoalType.COMPLETE_RENEWAL,
                        title = YapForYouGoalType.COMPLETE_RENEWAL.title,
                        description = Strings.screen_yfy_complete_renewal_description,
                        action = YAPForYouGoalAction.None,
                        media = YAPForYouGoalMedia.Image("ic_complete_renewal"),
                        response = response
                    )
                )
            )
        )
    }

    private fun makeGoal(
        type: YapForYouGoalType,
        title: String,
        action: YAPForYouGoalAction,
        description: String,
        media: YAPForYouGoalMedia,
        completedMedia: YAPForYouGoalMedia? = null,
        response: ArrayList<AchievementResponse>
    ): YAPForYouGoal {
        return YAPForYouGoal(
            type = type,
            title = title,
            action = action,
            completed = response.isCompleted(forGoalType = type),
            locked = response.getLockedStatus(forGoalType = type),
            description = description,
            media = media,
            completedMedia = completedMedia
        )
    }

    private fun makeAchievement(
        achievementType: AchievementType,
        title: String,
        response: ArrayList<AchievementResponse>,
        goals: ArrayList<YAPForYouGoal>
    ): Achievement {
        return Achievement(
            achievementType = achievementType,
            title = title,
            lastUpdated = response.getLastUpdatedDate(forAchievementType = achievementType),
            completedPercentage = response.percentage(forAchievementType = achievementType),
            isLocked = response.isLocked(forAchievementType = achievementType),
            tintColor = response.tintColor(forAchievementType = achievementType),
            achievementImage = response.achievementImage(forAchievementType = achievementType),
            achievementStatusIcon = response.achievementStatusIcon(forAchievementType = achievementType),
            goals = goals
        )
    }
}
