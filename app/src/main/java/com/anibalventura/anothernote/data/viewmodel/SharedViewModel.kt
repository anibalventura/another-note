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
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_EMPTY
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_TO_NOTE
import com.anibalventura.anothernote.utils.Constants.ARCHIVE_TO_TRASH
import com.anibalventura.anothernote.utils.Constants.NOTE_EMPTY
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.utils.Constants.TRASH_EMPTY
import com.anibalventura.anothernote.utils.Constants.TRASH_TO_NOTE
import com.anibalventura.anothernote.utils.Constants.TRASH_TO_NOTE_EDIT
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.utils.showToast
import com.google.android.material.snackbar.Snackbar

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val resources = App.resourses!!

    // ViewModels.
    private val noteViewModel: NoteViewModel = NoteViewModel(application)
    private val trashViewModel: TrashViewModel = TrashViewModel(application)
    private val archiveViewModel: ArchiveViewModel = ArchiveViewModel(application)

    /** ========================= Check for empty database. ========================= **/
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfNoteIsEmpty(noteData: List<NoteModel>) {
        emptyDatabase.value = noteData.isEmpty()
    }

    fun checkIfArchiveIsEmpty(archiveData: List<ArchiveModel>) {
        emptyDatabase.value = archiveData.isEmpty()
    }

    fun checkIfTrashIsEmpty(trashData: List<TrashModel>) {
        emptyDatabase.value = trashData.isEmpty()
    }

    /** ===================== Move note from one database to another. ===================== **/
    fun moveItem(
        from: String,
        noteItem: NoteModel,
        archiveItem: ArchiveModel,
        trashItem: TrashModel,
        view: View
    ) {
        lateinit var message: String

        // Set message.
        if (from == NOTE_TO_ARCHIVE) {
            message = resources.getString(R.string.snackbar_archive)
        } else if (from == NOTE_TO_TRASH || from == ARCHIVE_TO_TRASH) {
            message = resources.getString(R.string.snackbar_trash)
        } else if (from == ARCHIVE_TO_NOTE) {
            message = resources.getString(R.string.snackbar_unarchive)
        } else if (from == TRASH_TO_NOTE) {
            message = resources.getString(R.string.snackbar_note)
        } else if (from == TRASH_TO_NOTE_EDIT) {
            resources.getString(R.string.snackbar_note_edit)
        }

        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

        when (from) {
            NOTE_TO_ARCHIVE -> {
                // Insert/delete data.
                noteViewModel.deleteItem(noteItem)
                archiveViewModel.insertItem(archiveItem)

                // Show SnackBar to undo.
                snackBar.setAction(resources.getString(R.string.snackbar_undo)) {
                    // Insert/delete data.
                    noteViewModel.insertItem(noteItem)
                    archiveViewModel.deleteItem(archiveItem)
                }
            }
            NOTE_TO_TRASH -> {
                noteViewModel.deleteItem(noteItem)
                trashViewModel.insertItem(trashItem)

                snackBar.setAction(resources.getString(R.string.snackbar_undo)) {
                    noteViewModel.insertItem(noteItem)
                    archiveViewModel.deleteItem(archiveItem)
                }
            }
            ARCHIVE_TO_NOTE -> {
                archiveViewModel.deleteItem(archiveItem)
                noteViewModel.insertItem(noteItem)

                snackBar.setAction(resources.getString(R.string.snackbar_undo)) {
                    archiveViewModel.insertItem(archiveItem)
                    noteViewModel.deleteItem(noteItem)
                }.show()

                // Navigate back.
                view.findNavController()
                    .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            ARCHIVE_TO_TRASH -> {
                archiveViewModel.deleteItem(archiveItem)
                trashViewModel.insertItem(trashItem)

                snackBar.setAction(resources.getString(R.string.snackbar_undo)) {
                    archiveViewModel.insertItem(archiveItem)
                    trashViewModel.deleteItem(trashItem)
                }

                view.findNavController()
                    .navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            TRASH_TO_NOTE -> {
                trashViewModel.deleteItem(trashItem)
                noteViewModel.insertItem(noteItem)

                snackBar.setAction(resources.getString(R.string.snackbar_undo)) {
                    trashViewModel.insertItem(trashItem)
                    noteViewModel.deleteItem(noteItem)
                }

                view.findNavController().navigate(R.id.action_trashUpdateFragment_to_trashFragment)
            }
            TRASH_TO_NOTE_EDIT -> {
                snackBar.setAction(resources.getString(R.string.snackbar_restore)) {
                    trashViewModel.deleteItem(trashItem)
                    noteViewModel.insertItem(noteItem)

                    Snackbar.make(
                        view,
                        resources.getString(R.string.snackbar_note),
                        Snackbar.LENGTH_LONG
                    ).setAction(resources.getString(R.string.snackbar_undo)) {
                        trashViewModel.insertItem(trashItem)
                        noteViewModel.deleteItem(noteItem)
                    }.show()

                    view.findNavController()
                        .navigate(R.id.action_trashUpdateFragment_to_trashFragment)
                }
            }
        }

        snackBar.setActionTextColor(
            ActivityCompat.getColor(getApplication(), R.color.snackBarActionColor)
        ).show()
    }

    /** ========================= Confirm empty database. ========================= **/
    fun emptyDatabase(context: Context, database: String) {

        // Create dialog to confirm.
        val dialogBuilder = AlertDialog.Builder(context)

        when (database) {
            TRASH_EMPTY -> {
                dialogBuilder.setTitle(resources.getString(R.string.dialog_empty_trash))
                    .setMessage(resources.getString(R.string.dialog_empty_trash_you_sure))
                    .setPositiveButton(resources.getString(R.string.dialog_empty_trash)) { _, _ ->
                        // Delete database.
                        trashViewModel.deleteDatabase()
                        showToast(context, resources.getString(R.string.empty_trash_successful))
                    }
            }
            else -> {
                dialogBuilder.setTitle(resources.getString(R.string.dialog_delete_all))
                    .setMessage(resources.getString(R.string.dialog_delete_you_sure))
                    .setPositiveButton(resources.getString(R.string.dialog_confirmation)) { _, _ ->
                        when (database) {
                            // Delete database.
                            NOTE_EMPTY -> noteViewModel.deleteDatabase()
                            ARCHIVE_EMPTY -> archiveViewModel.deleteDatabase()
                        }
                        showToast(context, resources.getString(R.string.delete_all_successful))
                    }
            }
        }

        dialogBuilder.setNegativeButton(resources.getString(R.string.dialog_negative)) { _, _ -> }
            .show()
    }
}