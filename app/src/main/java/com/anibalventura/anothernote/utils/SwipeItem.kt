package com.anibalventura.anothernote.utils

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeItem(
    private val direction: Int,
    private val background: ColorDrawable,
    private val icon: Drawable
) : ItemTouchHelper.SimpleCallback(0, direction) {

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
        // Draw the background.
        drawSwipeBackground(background, direction, viewHolder, canvas, dX)

        // Draw icon.
        drawIcon(icon, direction, viewHolder, canvas)

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

    /*
     * Draw the background for swipe item.
     */
    private fun drawSwipeBackground(
        background: ColorDrawable,
        direction: Int,
        viewHolder: RecyclerView.ViewHolder,
        canvas: Canvas,
        dX: Float
    ) {
        val itemView = viewHolder.itemView

        when (direction) {
            ItemTouchHelper.LEFT -> background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            ItemTouchHelper.RIGHT -> background.setBounds(
                itemView.left + dX.toInt(),
                itemView.top,
                itemView.left,
                itemView.bottom
            )
        }

        background.draw(canvas)
    }

    /*
     * Calculate and draw icon for swipe item.
     */
    private fun drawIcon(
        icon: Drawable,
        direction: Int,
        viewHolder: RecyclerView.ViewHolder,
        canvas: Canvas
    ) {
        // Item.
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Icon position.
        val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when (direction) {
            ItemTouchHelper.LEFT -> {
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            ItemTouchHelper.RIGHT -> {
                val iconRight = iconMargin + icon.intrinsicWidth
                icon.setBounds(iconMargin, iconTop, iconRight, iconBottom)
            }
        }

        icon.draw(canvas)
    }
}