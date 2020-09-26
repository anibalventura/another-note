package com.anibalventura.anothernote.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.databinding.RecyclerviewArchiveBinding
import com.anibalventura.anothernote.utils.diffutil.ArchiveDiffUtil

class ArchiveAdapter : RecyclerView.Adapter<ArchiveAdapter.MyViewHolder>() {

    private var dataList = emptyList<ArchiveModel>()

    class MyViewHolder(private val binding: RecyclerviewArchiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(archiveModel: ArchiveModel) {
            binding.archiveData = archiveModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewArchiveBinding.inflate(layoutInflater, parent, false)
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

    fun setData(archiveData: List<ArchiveModel>) {
        val archiveDiffUtil = ArchiveDiffUtil(dataList, archiveData)
        val archiveDiffUtilResult = DiffUtil.calculateDiff(archiveDiffUtil)
        this.dataList = archiveData
        archiveDiffUtilResult.dispatchUpdatesTo(this)
    }
}