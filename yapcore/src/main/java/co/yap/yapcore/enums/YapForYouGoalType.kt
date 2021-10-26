package co.yap.yapcore.enums

import co.yap.translation.Strings

enum class YapForYouGoalType(val title: String) {
    //Get started enums
    OPEN_YOUR_YAP_ACCOUNT(Strings.screen_yfy_get_started_text_open_account),
    SET_PIN(Strings.screen_yfy_get_started_text_set_pin),
    TOP_UP(Strings.screen_yfy_get_started_text_add_money),
    SET_PROFILE_PICTURE(Strings.screen_yfy_get_started_text_set_profile),

    //Up & Running
    LOCAL_USE(Strings.screen_yfy_up_and_running_text_use_yap_locally),  //LOCAL_USE
    FREEZE_UNFREEZE_CARD(Strings.screen_yfy_up_and_running_text_freeze_unfreeze),
    SPEND_AMOUNT_100(Strings.screen_yfy_up_and_running_text_spend_money),
    EXPLORE_CARD_CONTROLS(Strings.screen_yfy_up_and_running_text_explore_card_controls),

    //Better Together
    INVITE_FRIEND(Strings.screen_yfy_better_together_text_invite_friend),
    MAKE_Y2Y_TRANSFER(Strings.screen_yfy_better_together_text_y2y_transfer),
    SPLIT_BILLS_WITH_YAP(Strings.screen_yfy_better_together_text_split_bills),
    SEND_MONEY_OUTSIDE_YAP(Strings.screen_yfy_better_together_text_send_money),

    //Take the leap
    ORDER_SPARE_CARD(Strings.screen_yfy_take_a_leap_text_send_money),
    UPGRADE_TO_PRIME(Strings.screen_yfy_take_a_leap_text_upgrade_to_prime),
    UPGRADE_TO_METAL(Strings.screen_yfy_take_a_leap_text_go_metal),
    SETUP_MULTI_CURRENCY(Strings.screen_yfy_take_a_leap_text_set_mc_account),

    //Yap Store
    GET_YAP_YOUNG(Strings.screen_yfy_yap_store_text_get_yap_young),
    GET_YAP_HOUSEHOLD(Strings.screen_yfy_yap_store_text_sign_up_house_hold),
    SET_MISSIONS_ON_YAP_YOUNG(Strings.screen_yfy_yap_store_text_set_mission),
    SALARY_TRANSFER_ON_YAP_HOUSEHOLD(Strings.screen_yfy_yap_store_text_pay_your_help),

    // You are a pro
    INVITE_TEN_FRIENDS(Strings.screen_yfy_you_are_pro_text_invite_friends),
    SPEND_AMOUNT_1000(Strings.screen_yfy_you_are_pro_text_spend_amount),
    COMPLETE_RENEWAL(Strings.screen_yfy_you_are_pro_text_complete_a_renewal),

}
