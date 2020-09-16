package com.anibalventura.anothernote.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.databinding.RecyclerviewRowBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var dataList = emptyList<NoteData>()

    class MyViewHolder(private val binding: RecyclerviewRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(noteData: NoteData) {
            binding.noteData = noteData
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    fun setData(noteData: List<NoteData>) {
        this.dataList = noteData
        notifyDataSetChanged()
    }
}