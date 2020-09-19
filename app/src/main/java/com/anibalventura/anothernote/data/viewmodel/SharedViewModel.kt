package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** ============================= ListFragment ============================= **/
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfNoteIsEmpty(noteData: List<NoteData>) {
        emptyDatabase.value = noteData.isEmpty()
    }

    fun checkIfArchiveIsEmpty(archiveData: List<ArchiveData>) {
        emptyDatabase.value = archiveData.isEmpty()
    }

    fun checkIfTrashIsEmpty(trashData: List<TrashData>) {
        emptyDatabase.value = trashData.isEmpty()
    }

    fun verifyData(title: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(title) || TextUtils.isEmpty(description) -> false
            else -> !(title.isEmpty() || description.isEmpty())
        }
    }
}