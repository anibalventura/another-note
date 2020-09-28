package com.anibalventura.anothernote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.databinding.RecyclerviewTrashBinding

class TrashAdapter : RecyclerView.Adapter<TrashAdapter.MyViewHolder>() {

    private var dataList = emptyList<TrashModel>()

    class MyViewHolder(private val binding: RecyclerviewTrashBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trashModel: TrashModel) {
            binding.trashData = trashModel
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(trashData: List<TrashModel>) {
        val trashDiffUtil = TrashDiffUtil(dataList, trashData)
        val trashDiffUtilResult = DiffUtil.calculateDiff(trashDiffUtil)
        this.dataList = trashData
        trashDiffUtilResult.dispatchUpdatesTo(this)
    }
}

class TrashDiffUtil(
    private val oldList: List<TrashModel>,
    private val newList: List<TrashModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].title == newList[newItemPosition].title
                && oldList[oldItemPosition].description == newList[newItemPosition].description
    }
}