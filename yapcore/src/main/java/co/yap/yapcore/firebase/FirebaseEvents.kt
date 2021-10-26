package co.yap.yapcore.firebase

import androidx.annotation.Keep


//enum class FirebaseEvents(val event: String) {
//    VERIFY_NUMBER("verifynumber"),
//    SCAN_ID("scanid"),
//    CONFIRM_ID("confirmid"),
//    DELIVERY_STARTED("deliverystarted"),
//    DELIVERY_CONFIRMED("deliveryconfirmed")
//}
@Keep
enum class FirebaseEvent(val event: String?, val screenName: String?) {
    /**	When a user opens the app for the first time, default Firebase event	*/
    FIRST_OPEN(
        "first_open",
        ""
    ),

    /**
     * User clicks Get Started in any of the tutorial screens
     */
    CLICK_GET_STARTED(
        "click_get_started",
        "Welcome_01a"
    ),

    /**User click on send after entering OTP code*/
    VERIFY_NUMBER(
        "verifynumber",
        "VerificationCode_01b"
    ),

    /**	User clicks  on Resend code	*/
    RESEND_OTP(
        "resend_otp",
        "Verification_Code_01b"
    ),

    /**	User enters the number and clicks Create Passcode	*/
    CREATE_PIN(
        "create_pin",
        "Passcode_01b"
    ),

    /**	Users clicks next after entering name	*/
    SIGNUP_NAME(
        "	signup_name",
        "Name_01c"
    ),

    /**	User clicks send after entering email	*/
    SIGNUP_EMAIL(
        "signup_email",
        "Email_01c"
    ),

    /**	User clicks next after successful email input	*/
    SIGNUP_EMAIL_SUCCESS(
        "	signup_email_success",
        "Email_01d"
    ),

    /**	User clicks on back button after email failed 	*/
    SIGNUP_EMAIL_FAILURE(
        "signup_email_failure",
        "Email_Error_01e"
    ),

    /**	User clicks on Complete Verification in the IBAN assignment page	*/
    COMPLETE_VERIFICATION(
        "complete_verification",
        "Virtual_Card_01b"
    ),

    /**	The user opens the Scan Emirates id screen for the first time	*/
    SCAN_ID(
        "scanid",
        "Verification_Process_01a"
    ),

    /**	User clicks on 'Skip and go to dashboard' button in the Scan id screen	*/
    CLICK_SKIP_EID(
        "	click_skip_eid	",
        "Verification_Process_01a"
    ),

    /**	User clicks on Scan in the Scan the front of your Emirates Id screen	*/
    CLICK_SCAN_FRONT(
        "click_scan_front	",
        "Camera_01a"
    ),

    /**	User clicks on Scan in the Scan the back of your Emirates Id screen	*/
    CLICK_SCAN_BACK(
        "click_scan_back",
        "Camera_01b"
    ),

    /**	User clicks on confirm in emirates id scan success screen	*/
    CONFIRM_ID(
        "confirmid",
        "Success_ID_with_3_names_01a"
    ),

    /**	User clicks on Re-scan Emirates id	*/
    RESCAN_ID(
        "rescan_id",
        "Success_ID_with 3_names_01a"
    ),

    /**	User clicks on the edit button next to a field. The field name should be sent as a parameter along with the event (e.g. first_name, middle_name, etc.)	*/
    EDIT_FIELD(
        "edit_field",
        "Success_ID_with_3_names_01a"
    ),

    /**	User clicks on This is Fine in the name on card screen	*/
    CLICK_NAME_FINE(
        "click_name_fine",
        "Confirm_name_01b"
    ),

    /**	User clicks on Edit My Name in the name on card screen	*/
    CLICK_NAME_EDIT(
        "click_name_edit",
        "Confirm_name_01b"
    ),

    /**	User clicks Next after editing the name on the card	*/
    NAME_EDIT_NEXT(
        "name_edit_next",
        "Confirm_name_edit_01b"
    ),

    /**	User clicks on Go to dashboard in US citizen KYC screen	*/
    KYC_US(
        "kyc_us",
        "US_citizen_01a"
    ),

    /**	User clicks on Go to dashboard in sanctioned KYC screen	*/
    KYC_SANCTIONED(
        "kyc_sanctioned",
        "sanctioned_01a"
    ),

    /**	User clicks on Go to dashboard in under 18 KYC screen	*/
    KYC_UNDERAGED(
        "kyc_underaged",
        "under_18_01a"
    ),

    /**	User clicks on know more link for CRS and FATCA	*/
    FATCA_KNOW_MORE_ADDRESS(
        "fatca_know_more",
        "Address_revised_01"
    ),

    /**	User taps on map to confirm location	*/
    MAP_FIND_LOCATION(
        "map_find_location",
        "Address_revised_01"
    ),

    /**	User clicks on confirm location after location is detected in the map	*/
    MAP_CONFIRM_LOCATION(
        "map_confirm_location",
        "Address_revised_01b"
    ),

    /**	User clicks on delete for one of the address fields (Address or Building information). The field name should be sent along with the event as parameter	*/
    DELETE_ADDRESS_FIELD(
        "delete_address_field",
        "Address_revised_01c"
    ),

    /**	User clicks on Next after filling in country and city of birth	*/
    BIRTH_LOCATION_SUBMIT(
        "birth_location_submit",
        "CRS_and_Facta_01"
    ),

    /**	User clicks on Submit after filling in tax residence information	*/
    TAX_RESIDENCE_SUBMIT(
        "	tax_residence_submit",
        "02_CRS_and_Facta_1"
    ),

    /**	User clicks on the link for CRS&FATCA	*/
    FATCA_KNOW_MORE(
        "fatca_know_more",
        "CRS_and_Facta_02_1"
    ),

    /**	User clicks to add another tax residency country 	*/
    ADD_TAX_COUNTRY(
        "add_tax_country",
        "CRS_and_Facta_02_1"
    ),

    /**	User opens 'Your card is on its way' for the first time	*/
    DELIVERY_STARTED(
        "deliverystarted",
        "Meeting_01a"
    ),

    /**	User clicks on Use Touch Id, upon first sign in	*/
    SETUP_TOUCH_ID(
        "setup_touch_id",
        "Sign_In_01f"
    ),

    /**	User clicks on No thanks, upon first sign in	*/
    NO_TOUCH_ID(
        "no_touch_id	",
        "Sign_In_01f"
    ),

    /**	User clicks on Use Face Id, upon first sign in	*/
    SETUP_FACE_ID(
        "setup_face_id",
        "Sign_In_01g"
    ),

    /**	User clicks on No thanks, upon first sign in	*/
    NO_FACE_ID(
        "no_face_id",
        "Sign_In_01g"
    ),

    /**	User clicks on Keep Me Up to Date, upon first sign in	*/
    ACCEPT_NOTIFICATIONS(
        "accept_notifications",
        "Sign_In_01h"
    ),

    /**	User clicks on No thanks, upon first sign in	*/
    DECLINE_NOTIFICATIONS(
        "decline_notifications",
        "Sign_In_01h"
    ),

    /**	User signs in using PIN	*/
    SIGN_IN_PIN("sign_in_pin", "Sign_In_01e"),

    /**	User signs in using Face Id	*/
    SIGN_IN_FACE(
        "sign_in_face",
        "Sign_In_Face_ID_01a"
    ),

    /**	User signs in using Touch Id	*/
    SIGN_IN_TOUCH(
        "sign_in_touch",
        "Sign_In_Touch_ID_01a"
    ),

    /**	User clicks on Open Mail App during Sign In when email verification is pending	*/
    OPEN_MAIL_APP(
        "open_mail_app",
        "Change_Email_01a"
    ),

    /**	User clicks on Send Me A New Mail during Sign In when email verification is pending	*/
    MAIL_VERIFICATION_RESEND(
        "mail_verification_resend",
        "Change_Email_01a"
    ),

    /**	User clicks on 'Click here' to change email during Sign In when email verification is pending	*/
    CHANGE_MAIL(
        "change_mail",
        "Change_Email_01a"
    ),

    /**	User clicks on Forgot Password in the Sign in screen	*/
    CLICK_FORGOT_PWD(
        "click_forgot_pwd",
        "Sign_In_01d"
    ),

    /**	User clicks on Forgot Password to reset pin after being blocked for incorrect attempts	*/
    FORGOT_PWD_BLOCKED(
        "forgot_pwd_blocked",
        "Incorrect_Password_01d"
    ),

    /**	User clicks on Open Camera to scan visa	*/
    VISA_SCAN(
        "visa_scan",
        "Visa_Verification_01b"
    ),

    /**	User clicks on Upload from files to upload visa	*/
    VISA_UPLOAD(
        "visa_upload",
        "Visa_Verification_01b"
    ),

    /**	User clicks on Got it button in the visa success page	*/
    VISA_SUCCESS(
        "visa_success",
        "Visa_Verification_01d"
    ),

    /**	User opens 'Delivery confirmed' screen for the first time	*/
    DELIVERY_CONFIRMED(
        "delivery_confirmed",
        "Sign_In_Card_01b"
    ),
    /**	User opens 'Delivery confirmed' screen for the first time	*/
    DELIVERY_CONFIRMED_NEW(
        "deliveryconfirmed",
        "Sign_In_Card_01b"
    ),
    /**	User clicks on Set Pin Now in the Sign in screen	*/
    CLICK_CARDPIN_NOW(
        "click_cardpin_now",
        "Sign_In_Card_01b"
    ),

    /**	User clicks on Do it Later in the Sign in screen	*/
    CLICK_CARDPIN_LATER(
        "click_cardpin_later",
        "Sign_In_Card_01b"
    ),

    /**	User clicks on Top up now in the Sign in Cards screen	*/
    CLICK_TOPUP_NOW(
        "click_topup_now",
        "Sign_In_Card_01e"
    ),

    /**	User clicks on Do it Later  the Sign in Cards screen	*/
    CLICK_TOPUP_LATER(
        "click_topup_later",
        "Sign_In_Card_01e"
    ),

    /**	User clicks Home in the menu bar of the Dashboard	*/
    CLICK_HOME(
        "click_home",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks Store in the menu bar of the Dashboard	*/
    CLICK_STORE(
        "click_store",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks YAP it in the menu bar of the Dashboard	*/
    CLICK_YAPIT(
        "click_yapit",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks Cards in the menu bar of the Dashboard	*/
    CLICK_CARDS(
        "click_cards",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks More (…) in the menu bar of the Dashboard	*/
    CLICK_MORE_DASHBOARD(
        "click_more",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks on filter icon for the transactions	*/
    CLICK_FILTER_TRANSACTIONS(
        "click_filter_transactions",
        "New_Dashboard_01_1a"
    ),

    /**	User clicks on the main menu icon	*/
    CLICK_MAIN_MENU(
        "click_main_menu",
        "New_Dashboard_01_1a"
    ),

    /**User clicks on Apply Filters.
    The parameters that need to be captured along with the event:
    incoming (BOOLEAN): TRUE if incoming transactions is selected
    outgoing (BOOLEAN): TRUE if outgoing transactions is selected
    category1 (STRING): Name of Category Selected
    category2 (STRING): Name of Category Selected
    category3 (STRING): Name of Category Selected
    category4 (STRING): Name of Category Selected
    category5 (STRING): Name of Category Selected
     * capture only up to 5 categories
    value_from (NUMBER): the min value selected in the slide bar
    value_to (NUMBER): the max value selected in the slide bar
    value_currency (STRING): The currency code"	*/
    APPLY_FILTERS("apply_filters", "Filters_01b"),

    /**	User clicks on Top up in the Actions Dashboard	*/
    CLICK_ACTIONS_TOPUP(
        "click_actions_topup",
        "Dashboard_Action_01a_New"
    ),

    /**	User clicks on Top up in the Actions Dashboard	*/
    CLICK_ACTIONS_YTY(
        "click_actions_yty",
        "Dashboard_Action_01a_New"
    ),

    /**	User clicks on Top up in the Actions Dashboard	*/
    CLICK_ACTIONS_SENDMONEY(
        "click_actions_sendmoney",
        "New_Dashboard_Action_01a"
    ),

    /**	User clicks on My Profile in the Main Menu	*/
    CLICK_PROFILE(
        "click_profile",
        "menu_standard_01"
    ),

    /**	User clicks on Share after expanding account details	*/
    SHARE_ACCOUNT_DETAILS(
        "share_account_details",
        "menu_standard_share_01"
    ),

    /**	User clicks on Refer a Friend in the Main Menu	*/
    CLICK_REFER_FRIEND(
        "click_refer_friend",
        "menu_standard_01"
    ),

    /**	User clicks on Alerts and Notifications in the Main Menu	*/
    CLICK_ALERTS(
        "click_alerts",
        "menu_standard_01"
    ),

    /**	User clicks on Statements in the Main Menu	*/
    CLICK_STATEMENTS(
        "click_statements",
        "menu_standard_01"
    ),

    /**	User clicks on Live Chat in the Main Menu	*/
    CLICK_LIVECHAT_MAIN_MENU(
        "click_livechat",
        "menu_standard_01"
    ),

    /**	User clicks on Help & Support in the Main Menu	*/
    CLICK_HELP_MAIN_MENU(
        "click_help	",
        "menu_standard_01"
    ),

    /**	User clicks onAnalytics in the Main Menu	*/
    CLICK_ANALYTICS_MAIN_MENU(
        "click_analytics",
        "menu_standard-01"
    ),

    /**	User clicks on Add Photo	*/
    CLICK_ADD_PHOTO(
        "click_add_photo",
        "Update_delete_photo_01"
    ),

    /**	User clicks on Open Camera	*/
    CLICK_OPEN_CAMERA(
        "click_open_camera",
        "Update_delete_photo_01"
    ),

    /**	User clicks on Choose photo	*/
    CLICK_CHOOSE_PHOTO(
        "click_choose_photo",
        "Update_delete_photo_01"
    ),

    /**	User clicks on Remove photo	*/
    CLICK_REMOVE_PHOTO(
        "click_remove_photo",
        "Update_delete_photo_01"
    ),

    /**	User clicks on existing beneficiary from the list	*/
    CLICK_BENEFICIARY(
        "click_beneficiary",
        "Send_money_01"
    ),

    /**
     *  User clicks on Add Beneficiary in the Send Money screen
     */
    ADD_BENEFICIARY(
        "add_beneficiary",
        "Send_money_00"
    ),

    /**
     *  User clicks on Edit Beneficiary from the list
     *  */
    EDIT_BENEFICIARY(
        "edit_beneficiary",
        "Send_money_02"
    ),

    /**	User clicks on Delete Beneficiary from the list	*/
    DELETE_BENEFICIARY(
        "delete_beneficiary",
        "Send_money_02"
    ),

    /**	User clicks next after selecting new Beneficiary country	*/
    START_NEW_BENEFICIARY(
        "start_new_beneficiary",
        "Add_Beneficiary_Cash_01"
    ),

    /**	User clicks on Confirm after entering amount for the transfer	*/
    CLICK_CONFIRM_AMOUNT(
        "click_confirm_amount",
        "Add_Beneficiary_Cash_08"
    ),

    /**	User clicks on Confirm after entering OTP for the transfer	*/
    CLICK_CONFIRM_TRANSFER(
        "click_confirm_transfer",
        "Add_Beneficiary_Cash_09"
    ),

    /**	User clicks on Resend OTP in the Confirm transfer screen	*/
    CLICK_RESEND_TRANSFEROTP(
        "click_resend_transferotp",
        "Add_Beneficiary_Cash_10"
    ),

    /**	User clicks on Bank Transfer option in the Top up screen	*/
    CLICK_TOPUP_TRANSFER(
        "click_topup_transfer",
        "TopUp_Bank_01a"
    ),

    /**	User clicks on Card option in the Top up screen	*/
    CLICK_TOPUP_CARD(
        "click_topup_card",
        "TopUp_Bank_01a"
    ),

    /**	User clicks on Share account details in case of Top up through Bank transfer	*/
    CLICK_SHARE_DETAILS(
        "click_share_details",
        "TopUp_Bank_04c"
    ),

    /**	User clicks on Add New Card in the Top Up Card screen	*/
    CLICK_ADD_CARD(
        "click_add_card",
        "TopUp_Add_New_Card_01"
    ),

    /**	User clicks on Invite Now in the Yap to Yap screen	*/
    CLICK_INVITE(
        "click_invite",
        "Y2Y_No_contacts_01a"
    ),

    /**	User clicks on Share referral code	*/
    CLICK_SHARE_CODE(
        "click_share_code",
        "Y2Y_Refer_a_friend_01a"
    ),

    /**	User clicks on Confirm in the Yap to Yap screen, after entering the amount	*/
    CLICK_CONFIRM_YTY(
        "click_confirm_yty",
        "Y2Y-Send_Transfer_MC_user_04"
    ),

    /**	User clicks on Top Up button in the Card main screen	*/
    CLICK_MAIN_TOPUP(
        "click_main_topup",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on Freeze button in the Card main screen	*/
    CLICK_FREEZE_CARD_MAIN_SCREEN(
        "click_freeze",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on Set Limits button in the Card main screen	*/
    CLICK_LIMITS(
        "click_limits",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on analytics in the Card main screen	*/
    CLICK_ANALYTICS_CARD_MAIN_SCREEN(
        "click_analytics	",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on More option (…) in the Card main screen	*/
    CLICK_MORE_CARD_MAIN_SCREEN(
        "click_more",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on Card Details in the Card main screen	*/
    CLICK_CARD_DETAILS_CARD_MAIN_SCREEN(
        "click_card_details",
        "Main_Cards_Home_01"
    ),

    /**	User clicks on Change Pin option in the pop up screen	*/
    CLICK_CHANGE_PIN(
        "click_change_pin",
        "Cards_Home_More_03"
    ),

    /**	User clicks on View statement option in the pop up screen	*/
    CLICK_VIEW_STMT(
        "click_view_stmt",
        "Cards_Home_More_03"
    ),

    /**	User clicks on Report lost or stolen option in the pop up screen	*/
    CLICK_REPORT_LOST(
        "click_report_lost",
        "Cards_Home_More_03"
    ),

    /**	User clicks on Cancel option in the pop up screen	*/
    CLICK_CANCEL(
        "click_cancel",
        "Cards_Home_More_03"
    ),

    /**	User clicks on the monthly tab in the Analytics screen	*/
    CLICK_MONTHLY_VIEW(
        "click_monthly_view",
        "Analytics_01"
    ),

    /**	User clicks on the weekly tab in the Analytics screen	*/
    CLICK_WEEKLY_VIEW(
        "click_weekly_view",
        "Analytics_01"
    ),

    /**	User clicks on the Merchant tab in the Analytics screen	*/
    CLICK_MERCHANT_VIEW(
        "click_merchant_view",
        "Analytics_01"
    ),

    /**	User clicks on the Category tab in the Analytics screen	*/
    CLICK_CATEGORY_VIEW(
        "click_category_view",
        "Analytics_01"
    ),

    /**	User clicks on a specific category. spend_category: the name of the category selected	*/
    CLICK_CATEGORY_LIST(
        "click_category_list",
        "Analytics_01"
    ),

    /**	Users clicks on the scroll buttons to change dates for Analytics (any direction)	*/
    SCROLL_DATES(
        "scroll_dates",
        "Analytics_01"
    ),

    /**	User clicks on Share my QR Code	*/
    SHARE_QR_CODE(
        "share_qr_code",
        "QR_share_to_add_money"
    ),

    /**	User clicks on Save QR Code to Gallery	*/
    SAVE_QR_CODE(
        "save_qr_code",
        "QR_share_to_add_money"
    ),

    /**	User Clicks on QR code in the Add Money options	*/
    TOPUP_QR_CODE(
        "topup_qr_code",
        "TopUp_Bank_Full_version_01a"
    ),

    /**	User Clicks on QR Code	*/
    SEND_QR_CODE(
        "send_qr_code",
        "Send_Money_Dashboard_show"
    ),

    /**	User clicks on Send to send money to the QR Code, after entering the amount	*/
    SEND_QR_PAYMENT(
        "send_qr_payment",
        "QR_Confirm_payment_02"
    ),

    /**	User clicks on Add Funds in the Virtual Card dashboard	*/
    CLICK_ADD_FUNDS(
        "click_add_funds",
        "virtual_Card_section"
    ),

    /**	User clicks on Remove Funds in the Virtual Card dashboard	*/
    CLICK_REMOVE_FUNDS(
        "click_remove_funds",
        "virtual-Card_section"
    ),

    /**	User clicks on Freeze in the Virtual Card dashboard	*/
    CLICK_FREEZE_VIRTUAL_CARD_DASHBOARD(
        "click_freeze",
        "virtual-Card_section"
    ),

    /**	User clicks on Card details in the Virtual Card dashboard	*/
    CLICK_CARD_DETAILS_VIRTUAL_CARD_DASHBOARD(
        "click_card_details",
        "virtual_Card_section	"
    ),

    /**	User clicks on Notifications in the More B2C screen	*/
    CLICK_NOTIFICATIONS(
        "	click_notifications	",
        "More_Personal_01"
    ),

    /**	User clicks on Locate ATM and CDM in the More B2C screen	*/
    CLICK_ATM_LOCATION(
        "click_atm_location",
        "More_Personal-01"
    ),

    /**	User clicks on Help and Support in the More B2C screen	*/
    CLICK_HELP_MORE_SCREEN(
        "click_help",
        "More_Personal_01"
    ),

    /**	User clicks on Invite a Friend in the More B2C screen	*/
    CLICK_INVITE_FRIEND(
        "	click_invite_friend",
        "More_Personal_01"
    ),

    /**	User clicks on Your bank details in the More B2C screen	*/
    CLICK_BANK_DETAILS(
        "	click_bank_details",
        "More_Personal_01"
    ),

    /**	User clicks on Share button after viewing the bank details screen	*/
    SHARE_BANK_DETAILS(
        "	share_bank_details",
        "More_Bank_Details_04"
    ),

    /**	User clicks on FAQs in the Help and Support screen	*/
    CLICK_FAQS(
        "click_faqs",
        "More_Help_01"
    ),

    /**	User clicks on Live Chat in the Help and Support screen	*/
    CLICK_LIVECHAT_HELP_SUPPORT(
        "click_livechat",
        "More_Help_01"
    ),

    /**	User clicks on  Call us in the Help and Support screen	*/
    CLICK_CALL(
        "click_call",
        "More_Help_01"
    )
}
