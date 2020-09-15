package com.anibalventura.anothernote.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.Priority
import kotlinx.android.synthetic.main.recyclerview_row.view.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var dataList = emptyList<NoteData>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = holder.itemView
        item.tvListDescription.text = dataList[position].description
        item.tvListTitle.text = dataList[position].title
        item.recyclerview_row.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            item.findNavController().navigate(action)
        }

        when (dataList[position].priority) {
            Priority.HIGH -> item.cvPriorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    item.context,
                    R.color.priorityHigh
                )
            )
            Priority.MEDIUM -> item.cvPriorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    item.context,
                    R.color.priorityMedium
                )
            )
            Priority.LOW -> item.cvPriorityIndicator.setCardBackgroundColor(
                ContextCompat.getColor(
                    item.context,
                    R.color.priorityLow
                )
            )
        }
    }

    fun setData(noteData: List<NoteData>) {
        this.dataList = noteData
        notifyDataSetChanged()
    }
}