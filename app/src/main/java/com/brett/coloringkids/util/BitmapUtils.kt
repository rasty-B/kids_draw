package com.brett.coloringkids.util

import android.graphics.Bitmap
import android.graphics.Canvas
import com.brett.coloringkids.ui.views.DrawingView

object BitmapUtils {

    /**
     * Flatten background and stroke layers into a single bitmap
     */
    fun flatten(drawingView: DrawingView): Bitmap {
        val bgBitmap = drawingView.getBackgroundBitmap()
            ?: throw IllegalStateException("No background bitmap to flatten")

        val strokeBitmap = drawingView.getStrokeBitmap()

        // Create output bitmap with background dimensions
        val result = Bitmap.createBitmap(
            bgBitmap.width,
            bgBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(result)
        canvas.drawBitmap(bgBitmap, 0f, 0f, null)
        canvas.drawBitmap(strokeBitmap, 0f, 0f, null)

        return result
    }
}
