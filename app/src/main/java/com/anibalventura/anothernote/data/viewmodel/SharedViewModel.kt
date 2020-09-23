package com.anibalventura.anothernote.data.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.anibalventura.anothernote.App
import com.anibalventura.anothernote.Constants.ARCHIVE_TO_EMPTY
import com.anibalventura.anothernote.Constants.ARCHIVE_TO_NOTE
import com.anibalventura.anothernote.Constants.ARCHIVE_TO_TRASH
import com.anibalventura.anothernote.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.Constants.NOTE_TO_EMPTY
import com.anibalventura.anothernote.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.Constants.TRASH_TO_EMPTY
import com.anibalventura.anothernote.Constants.TRASH_TO_NOTE
import com.anibalventura.anothernote.Constants.TRASH_TO_NOTE_EDIT
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.utils.showToast
import com.google.android.material.snackbar.Snackbar

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val app = App.resourses!!

    // ViewModels.
    private val noteViewModel: NoteViewModel = NoteViewModel(application)
    private val trashViewModel: TrashViewModel = TrashViewModel(application)
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
     * Move note from one database to another.
     */
    fun moveItem(
        from: String,
        noteItem: NoteData,
        archiveItem: ArchiveData,
        trashItem: TrashData,
        view: View
    ) {
        lateinit var message: String

        if (from == NOTE_TO_ARCHIVE) {
            message = app.getString(R.string.snackbar_archive)
        } else if (from == NOTE_TO_TRASH || from == ARCHIVE_TO_TRASH) {
            message = app.getString(R.string.snackbar_trash)
        } else if (from == ARCHIVE_TO_NOTE) {
            message = app.getString(R.string.snackbar_unarchive)
        } else if (from == TRASH_TO_NOTE) {
            message = app.getString(R.string.snackbar_note)
        } else if (from == TRASH_TO_NOTE_EDIT) {
            app.getString(R.string.snackbar_note_edit)
        }

        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        when (from) {
            NOTE_TO_ARCHIVE -> {
                noteViewModel.deleteItem(noteItem)
                archiveViewModel.insertData(archiveItem)

                snackBar
                    .setAction(app.getString(R.string.snackbar_undo)) {
                        noteViewModel.insertData(noteItem)
                        archiveViewModel.deleteItem(archiveItem)
                    }
            }
            NOTE_TO_TRASH -> {
                noteViewModel.deleteItem(noteItem)
                trashViewModel.insertData(trashItem)

                snackBar
                    .setAction(app.getString(R.string.snackbar_undo)) {
                        noteViewModel.insertData(noteItem)
                        archiveViewModel.deleteItem(archiveItem)
                    }
            }
            ARCHIVE_TO_NOTE -> {
                archiveViewModel.deleteItem(archiveItem)
                noteViewModel.insertData(noteItem)

                snackBar
                    .setAction(app.getString(R.string.snackbar_undo)) {
                        archiveViewModel.insertData(archiveItem)
                        noteViewModel.deleteItem(noteItem)
                    }.show()

                view.findNavController()
                    .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            ARCHIVE_TO_TRASH -> {
                archiveViewModel.deleteItem(archiveItem)
                trashViewModel.insertData(trashItem)

                snackBar
                    .setAction(app.getString(R.string.snackbar_undo)) {
                        archiveViewModel.insertData(archiveItem)
                        trashViewModel.deleteItem(trashItem)
                    }

                view.findNavController()
                    .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            TRASH_TO_NOTE -> {
                trashViewModel.deleteItem(trashItem)
                noteViewModel.insertData(noteItem)

                snackBar
                    .setAction(app.getString(R.string.snackbar_undo)) {
                        trashViewModel.insertData(trashItem)
                        noteViewModel.deleteItem(noteItem)
                    }

                view.findNavController().navigate(R.id.action_trashUpdateFragment_to_trashFragment)
            }
            TRASH_TO_NOTE_EDIT -> {
                snackBar
                    .setAction(app.getString(R.string.snackbar_restore)) {
                        trashViewModel.deleteItem(trashItem)
                        noteViewModel.insertData(noteItem)

                        Snackbar
                            .make(view, app.getString(R.string.snackbar_note), Snackbar.LENGTH_LONG)
                            .setAction(app.getString(R.string.snackbar_undo)) {
                                trashViewModel.insertData(trashItem)
                                noteViewModel.deleteItem(noteItem)
                            }.show()

                        view.findNavController()
                            .navigate(R.id.action_trashUpdateFragment_to_trashFragment)
                    }
            }
        }

        snackBar
            .setActionTextColor(
                ActivityCompat.getColor(
                    getApplication(),
                    R.color.snackBarActionColor
                )
            )
            .show()
    }

    /*
     * Confirm delete database.
     */
    fun emptyData(context: Context, data: String) {
        val dialogBuilder = AlertDialog.Builder(context)

        when (data) {
            TRASH_TO_EMPTY -> {
                dialogBuilder.setTitle(app.getString(R.string.dialog_empty_trash))
                dialogBuilder.setMessage(app.getString(R.string.dialog_empty_trash_you_sure))
                dialogBuilder.setPositiveButton(app.getString(R.string.dialog_empty_trash)) { _, _ ->
                    trashViewModel.deleteAll()
                    showToast(context, app.getString(R.string.empty_trash_successful))
                }
            }
            else -> {
                dialogBuilder.setTitle(app.getString(R.string.dialog_delete_all))
                dialogBuilder.setMessage(app.getString(R.string.dialog_delete_you_sure))
                dialogBuilder.setPositiveButton(app.getString(R.string.dialog_confirmation)) { _, _ ->
                    when (data) {
                        NOTE_TO_EMPTY -> noteViewModel.deleteAll()
                        ARCHIVE_TO_EMPTY -> archiveViewModel.deleteAll()
                    }
                    showToast(context, app.getString(R.string.delete_all_successful))
                }
            }
        }

        dialogBuilder.setNegativeButton(app.getString(R.string.dialog_negative)) { _, _ -> }
        dialogBuilder.show()
    }
}