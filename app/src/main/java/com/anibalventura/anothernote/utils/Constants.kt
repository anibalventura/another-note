package com.anibalventura.anothernote.utils

import androidx.recyclerview.widget.ItemTouchHelper

object Constants {
    // Database
    const val NOTE_TO_ARCHIVE = "note_to_archive"
    const val NOTE_TO_TRASH = "note_to_trash"
    const val NOTE_EMPTY = "note_to_empty"

    const val ARCHIVE_TO_NOTE = "archive_to_note"
    const val ARCHIVE_TO_TRASH = "archive_to_trash"
    const val ARCHIVE_EMPTY = "archive_to_empty"

    const val TRASH_TO_NOTE = "trash_to_note"
    const val TRASH_TO_NOTE_EDIT = "trash_to_note_edit"
    const val TRASH_EMPTY = "trash_to_empty"

    // SwipeItem.
    const val SWIPE_DELETE = ItemTouchHelper.LEFT
    const val SWIPE_ARCHIVE = ItemTouchHelper.RIGHT

    // Preferences.
    const val THEME = "theme"
    const val NOTE_LAYOUT = "note_view"
    const val ARCHIVE_LAYOUT = "archive_view"
}