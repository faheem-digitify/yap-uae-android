package co.yap.modules.dashboard.cards.paymentcarddetail.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.yap.yapuae.R
import co.yap.modules.others.helper.Constants
import co.yap.yapcore.enums.CardStatus
import co.yap.yapcore.firebase.FirebaseEvent
import co.yap.yapcore.firebase.trackEventWithScreenName
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_primary_card.view.*

class PrimaryCardBottomSheet(
    private val cardStatus: String,
    private val mListener: CardClickListener
) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_primary_card, container, false)

        if (cardStatus == CardStatus.EXPIRED.name) {
            view.tvChangePin.visibility = View.GONE
            view.tvForgotCardPin.visibility = View.GONE
        }
        view.tvAddCardName.setOnClickListener { mListener.onClick(Constants.EVENT_ADD_CARD_NAME) }
        view.tvChangePin.setOnClickListener {
            trackEventWithScreenName(FirebaseEvent.CLICK_CHANGE_PIN)
            mListener.onClick(Constants.EVENT_CHANGE_PIN)
        }
        view.tvForgotCardPin.setOnClickListener { mListener.onClick(Constants.EVENT_FORGOT_CARD_PIN) }
        view.tvViewStatements.setOnClickListener {
            trackEventWithScreenName(FirebaseEvent.CLICK_VIEW_STMT)
            mListener.onClick(Constants.EVENT_VIEW_STATEMENTS)
        }
        view.tvReportCard.setOnClickListener {
            trackEventWithScreenName(FirebaseEvent.CLICK_REPORT_LOST)
            mListener.onClick(Constants.EVENT_REPORT_CARD)
        }
        return view
    }
}