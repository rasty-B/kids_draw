package com.brett.coloringkids.domain.model

import android.graphics.PointF

data class Stroke(
    val points: MutableList<PointF>,
    val color: Int,
    val widthPx: Float,
    val tool: Tool
)
