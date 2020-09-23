package com.anibalventura.anothernote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.databinding.RecyclerviewTrashBinding
import com.anibalventura.anothernote.utils.TrashDiffUtil

class TrashAdapter : RecyclerView.Adapter<TrashAdapter.MyViewHolder>() {

    private var dataList = emptyList<TrashData>()

    class MyViewHolder(private val binding: RecyclerviewTrashBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trashData: TrashData) {
            binding.trashData = trashData
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewTrashBinding.inflate(layoutInflater, parent, false)
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

    fun setData(trashData: List<TrashData>) {
        val trashDiffUtil = TrashDiffUtil(dataList, trashData)
        val trashDiffUtilResult = DiffUtil.calculateDiff(trashDiffUtil)
        this.dataList = trashData
        trashDiffUtilResult.dispatchUpdatesTo(this)
    }
}