package com.anibalventura.anothernote

import androidx.recyclerview.widget.ItemTouchHelper

object Constants {
    // Settings.
    const val THEME = "theme"
    const val NOTE_VIEW = "note_view"
    const val ARCHIVE_VIEW = "archive_view"

    // SwipeItem.
    const val DELETE_NOTE = ItemTouchHelper.LEFT
    const val ARCHIVE_NOTE = ItemTouchHelper.RIGHT

    // Database
    const val NOTE = "note"
    const val ARCHIVE = "archive"
}