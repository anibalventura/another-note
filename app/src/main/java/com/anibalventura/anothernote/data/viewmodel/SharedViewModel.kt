package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.anibalventura.anothernote.data.models.NoteData

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    /** ============================= ListFragment ============================= **/
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseIsEmpty(noteData: List<NoteData>) {
        emptyDatabase.value = noteData.isEmpty()
    }

    fun verifyData(title: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(title) || TextUtils.isEmpty(description) -> false
            else -> !(title.isEmpty() || description.isEmpty())
        }
    }
}