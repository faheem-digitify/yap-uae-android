package co.yap.widgets.spinneradapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import co.yap.yapcore.R

/**
 * Uses the magic of [RecyclerView.ViewHolder]s with
 * [ArrayAdapter] to make great Spinner adapters
 */
open class ViewHolderArrayAdapter<T, VH : RecyclerView.ViewHolder, DVH : RecyclerView.ViewHolder>(
    context: Context,
    objects: List<T>,
    private val onCreateViewHolder: (parent: ViewGroup) -> VH,
    private val onCreateDropDownViewHolder: (parent: ViewGroup) -> DVH,
    private val onBindViewHolder: (viewHolder: VH, position: Int, item: T) -> Unit,
    private val onBindDropDownViewHolder: (viewHolder: DVH, position: Int, item: T) -> Unit
) : ArrayAdapter<T>(context, 0, objects) {

    companion object {

        /**
         * Get the position in the ViewHolderArrayAdapter of the View holder.
         * Use this instead of [RecyclerView.ViewHolder.getAdapterPosition]
         *
         * @param viewHolder the holder to find the position of
         * @return the index of the holder, or -1 if not found
         */
        fun getAdapterPosition(viewHolder: RecyclerView.ViewHolder): Int {
            return getPosition(viewHolder)
        }

        /**
         * Get the position in the ViewHolderArrayAdapter of the View holder.
         * Use this instead of [RecyclerView.ViewHolder.getLayoutPosition]
         *
         * @param viewHolder the holder to find the position of
         * @return the index of the holder, or -1 if not found
         */
        fun getLayoutPosition(viewHolder: RecyclerView.ViewHolder): Int {
            return getPosition(viewHolder)
        }

        private fun getPosition(viewHolder: RecyclerView.ViewHolder): Int {
            var position = -1
            val tag = viewHolder.itemView.getTag(R.id.view_holder_array_adapter_position)
            if (tag != null) {
                position = tag as Int
            }
            return position
        }
    }

    override fun setDropDownViewResource(resource: Int) {
        throwIt()
    }

    private fun throwIt() {
        throw RuntimeException("This is not supported due to the need to use ViewHolders. You should extend this class and customize as needed.")
    }

    @CallSuper
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val holder: VH
        if (convertView == null) {
            holder = onCreateViewHolder(parent)
            holder.itemView.setTag(R.id.view_holder_array_adapter_holder, holder)
        } else {
            //no need to worry, we are the only ones setting this tag

            @Suppress("UNCHECKED_CAST")
            holder = convertView.getTag(R.id.view_holder_array_adapter_holder) as VH
        }
        onBindViewHolder.invoke(holder, position, item)
        return holder.itemView
    }

    @CallSuper
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val holder: DVH
        if (convertView == null) {
            holder = onCreateDropDownViewHolder(parent)
            holder.itemView.setTag(R.id.view_holder_array_adapter_holder, holder)
        } else {
            //no need to worry, we are the only ones setting this tag

            @Suppress("UNCHECKED_CAST")
            holder = convertView.getTag(R.id.view_holder_array_adapter_holder) as DVH
        }
        holder.itemView.setTag(R.id.view_holder_array_adapter_position, position)
        onBindDropDownViewHolder.invoke(holder, position, item)
        return holder.itemView
    }
}
