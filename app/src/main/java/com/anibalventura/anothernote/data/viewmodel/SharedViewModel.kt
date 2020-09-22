package com.anibalventura.anothernote.data.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.anibalventura.anothernote.App
import com.anibalventura.anothernote.Constants.ARCHIVE
import com.anibalventura.anothernote.Constants.NOTE
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.utils.showToast

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val app = App.resourses!!

    // ViewModels.
    private val noteViewModel: NoteViewModel = NoteViewModel(application)
    private val archiveViewModel: ArchiveViewModel = ArchiveViewModel(application)

    /*
     * Check for empty database.
     */
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

    /*
     * Verify data input.
     */
    fun verifyData(title: String, description: String): Boolean {
        return when {
            TextUtils.isEmpty(title) || TextUtils.isEmpty(description) -> false
            else -> !(title.isEmpty() || description.isEmpty())
        }
    }

    /*
     * Dialog for confirmation of delete all notes.
     */
    fun confirmDeleteAll(context: Context, data: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(app.getString(R.string.dialog_delete_all))
        dialogBuilder.setMessage(app.getString(R.string.dialog_delete_you_sure))
        dialogBuilder.setPositiveButton(app.getString(R.string.dialog_confirmation)) { _, _ ->
            when (data) {
                NOTE -> noteViewModel.deleteAll()
                ARCHIVE -> archiveViewModel.deleteAll()
            }
            showToast(context, app.getString(R.string.delete_all_successful))
        }
        dialogBuilder.setNegativeButton(app.getString(R.string.dialog_negative)) { _, _ -> }
        dialogBuilder.show()
    }
}