package com.anibalventura.anothernote.utils

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.Constants.ARCHIVE_NOTE
import com.anibalventura.anothernote.Constants.DELETE_NOTE

abstract class SwipeItem(
    private val action: Int,
    private val background: Drawable,
    private val icon: Drawable
) : ItemTouchHelper.SimpleCallback(0, action) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        // Calculate icon position.
        val itemHeight = itemView.bottom - itemView.top
        val iconMargin = 55
        val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        /*
         * Draw background.
         */
        when (action) {
            DELETE_NOTE -> background.setBounds(
                itemView.left + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            ARCHIVE_NOTE -> background.setBounds(
                itemView.left,
                itemView.top,
                itemView.right + dX.toInt(),
                itemView.bottom
            )
        }
        background.draw(canvas)

        /*
         * Draw icon.
         */
        when (action) {
            DELETE_NOTE -> {
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            ARCHIVE_NOTE -> {
                val iconRight = iconMargin + icon.intrinsicWidth
                icon.setBounds(iconMargin, iconTop, iconRight, iconBottom)
            }
        }
        icon.draw(canvas)

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }
}