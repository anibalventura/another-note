package com.anibalventura.anothernote.ui.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.databinding.RecyclerviewItemBinding

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

    var dataList = emptyList<NoteData>()

    class MyViewHolder(private val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(noteData: NoteData) {
            binding.noteData = noteData
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewItemBinding.inflate(layoutInflater, parent, false)
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
        val noteDiffUtil = NoteDiffUtil(dataList, noteData)
        val noteDiffUtilResult = DiffUtil.calculateDiff(noteDiffUtil)
        this.dataList = noteData
        noteDiffUtilResult.dispatchUpdatesTo(this)
    }
}