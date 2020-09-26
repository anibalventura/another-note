package com.anibalventura.anothernote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.databinding.RecyclerviewNoteBinding
import com.anibalventura.anothernote.utils.diffutil.NoteDiffUtil

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

    var dataList = emptyList<NoteModel>()

    class MyViewHolder(private val binding: RecyclerviewNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(noteModel: NoteModel) {
            binding.noteData = noteModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewNoteBinding.inflate(layoutInflater, parent, false)
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

    fun setData(noteData: List<NoteModel>) {
        val noteDiffUtil = NoteDiffUtil(dataList, noteData)
        val noteDiffUtilResult = DiffUtil.calculateDiff(noteDiffUtil)
        this.dataList = noteData
        noteDiffUtilResult.dispatchUpdatesTo(this)
    }
}