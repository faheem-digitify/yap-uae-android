package co.yap.modules.dashboard.addionalinfo.adapters

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapuae.R
import co.yap.yapuae.databinding.ItemUploadAdditionalDocumentBinding
import co.yap.modules.dashboard.addionalinfo.viewmodels.UploadAdditionalDocumentItemViewModel
import co.yap.networking.customers.models.additionalinfo.AdditionalDocument
import co.yap.yapcore.BaseBindingRecyclerAdapter
import co.yap.yapcore.interfaces.OnItemClickListener

class UploadAdditionalDocumentAdapter(
    context: Context,
    private val list: MutableList<AdditionalDocument>
) : BaseBindingRecyclerAdapter<AdditionalDocument, UploadAdditionalDocumentAdapter.ViewHolder>(list) {


    override fun getLayoutIdForViewType(viewType: Int): Int =
        R.layout.item_upload_additional_document

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(list[position], position, onItemClickListener)
    }

    class ViewHolder(private val itemUploadAdditionalDocumentBinding: ItemUploadAdditionalDocumentBinding) :
        RecyclerView.ViewHolder(itemUploadAdditionalDocumentBinding.root) {
        fun onBind(
            additionalDocument: AdditionalDocument,
            position: Int,
            onItemClickListener: OnItemClickListener?
        ) {
            itemUploadAdditionalDocumentBinding.ivCheck.visibility =
                if (additionalDocument.status == "DONE") View.VISIBLE else View.GONE

            itemUploadAdditionalDocumentBinding.tvDocumentDescription.text =
                if (additionalDocument.status == "DONE") "Document uploaded. \nTap to rescan" else "Tap to scan your " + additionalDocument.name?.toLowerCase() + " copy"

            itemUploadAdditionalDocumentBinding.viewModel =
                UploadAdditionalDocumentItemViewModel(
                    additionalDocument,
                    position,
                    onItemClickListener
                )
            itemUploadAdditionalDocumentBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(binding: ViewDataBinding): ViewHolder {
        return ViewHolder(binding as ItemUploadAdditionalDocumentBinding)
    }
}