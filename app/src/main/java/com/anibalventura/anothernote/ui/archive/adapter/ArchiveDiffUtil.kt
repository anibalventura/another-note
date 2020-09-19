package com.anibalventura.anothernote.ui.archive.adapter

import androidx.recyclerview.widget.DiffUtil
import com.anibalventura.anothernote.data.models.ArchiveData

class ArchiveDiffUtil(
    private val newList: List<ArchiveData>,
    private val oldList: List<ArchiveData>
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