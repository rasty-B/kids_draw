package com.brett.coloringkids.domain.model

import android.graphics.Bitmap

data class CanvasState(
    val backgroundBitmap: Bitmap? = null,
    val currentTool: Tool = Tool.PEN,
    val currentColor: Int = android.graphics.Color.BLACK,
    val currentWidthPx: Float = 8f,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
