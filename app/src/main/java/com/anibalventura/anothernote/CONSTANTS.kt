package com.anibalventura.anothernote

import androidx.recyclerview.widget.ItemTouchHelper

class CONST {
    companion object {
        const val THEME = "theme"
        const val NOTE_VIEW = "note_view"
        const val DELETE_ITEM = ItemTouchHelper.LEFT
        const val ARCHIVE_ITEM = ItemTouchHelper.RIGHT
    }
}