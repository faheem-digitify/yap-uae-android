package co.yap.widgets.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.R
import co.yap.yapcore.interfaces.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet(
    private val mListener: OnItemClickListener? = null,
    private val bottomSheetItems: MutableList<BottomSheetItem>,
    private val headingLabel: String? = null,
    private val subHeadingLabel: String? = null,
    private val showDivider: Boolean? = null

) : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = BottomSheetAdapter(bottomSheetItems)
        val view = inflater.inflate(R.layout.core_bottom_sheet, container, false)
        val tvHeading: TextView = view.findViewById(R.id.tvHeadingTitle)
        val tvSubHeading: TextView = view.findViewById(R.id.tvSubTitle)
        val divider: View = view.findViewById(R.id.divider)
        headingLabel?.let {
            tvHeading.visibility = View.VISIBLE
            tvHeading.text = it
        }
        subHeadingLabel?.let {
            tvSubHeading.visibility = View.VISIBLE
            tvSubHeading.text = it
        }
        divider.visibility = if (showDivider == true) View.VISIBLE else View.GONE

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.allowFullItemClickListener = true
        adapter.onItemClickListener = myListener
        recyclerView.adapter = adapter
        return view
    }

    private val myListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(view: View, data: Any, pos: Int) {
            mListener?.onItemClick(view, data, pos)
            dismiss()
        }
    }
}