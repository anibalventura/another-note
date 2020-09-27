package com.anibalventura.anothernote.utils.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.anibalventura.anothernote.data.models.ArchiveModel

class ArchiveDiffUtil(
    private val newList: List<ArchiveModel>,
    private val oldList: List<ArchiveModel>
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