package com.brett.coloringkids.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.brett.coloringkids.domain.model.Stroke
import com.brett.coloringkids.domain.model.Tool
import kotlin.math.min

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bgBitmap: Bitmap? = null
    private lateinit var strokeBitmap: Bitmap
    private lateinit var strokeCanvas: Canvas

    private val contentMatrix = Matrix()
    private val inverseMatrix = Matrix()

    private val activePathPoints = mutableListOf<PointF>()
    private val undoStack = mutableListOf<Stroke>()
    private val redoStack = mutableListOf<Stroke>()

    var currentColor: Int = Color.BLACK
    var currentWidthPx: Float = dp(8f)
    var currentTool: Tool = Tool.PEN

    // Gesture detectors
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val gestureDetector = GestureDetector(context, PanListener())

    private var isDrawing = false
    private var lastX = 0f
    private var lastY = 0f

    // Scale limits
    private var minScale = 0.5f
    private var maxScale = 5f
    private var currentScale = 1f

    init {
        setBackgroundColor(Color.WHITE)
    }

    fun setBackgroundBitmap(bmp: Bitmap) {
        bgBitmap = bmp
        ensureStrokeLayer(bmp.width, bmp.height)
        fitToView()
        invalidate()
    }

    private fun ensureStrokeLayer(w: Int, h: Int) {
        strokeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        strokeCanvas = Canvas(strokeBitmap)
        undoStack.clear()
        redoStack.clear()
    }

    private fun fitToView() {
        contentMatrix.reset()
        bgBitmap?.let { bmp ->
            if (width > 0 && height > 0) {
                val scale = min(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
                currentScale = scale
                minScale = scale * 0.5f
                maxScale = scale * 5f
                contentMatrix.setScale(scale, scale)
                val dx = (width - bmp.width * scale) / 2f
                val dy = (height - bmp.height * scale) / 2f
                contentMatrix.postTranslate(dx, dy)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bgBitmap != null) {
            fitToView()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.concat(contentMatrix)
        bgBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawBitmap(strokeBitmap, 0f, 0f, null)
        canvas.restore()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val multi = ev.pointerCount > 1
        scaleDetector.onTouchEvent(ev)
        gestureDetector.onTouchEvent(ev)

        if (multi || scaleDetector.isInProgress) {
            if (isDrawing) {
                // Cancel current stroke if user starts multi-touch
                isDrawing = false
                activePathPoints.clear()
            }
            return true
        }

        // Map to content coords
        contentMatrix.invert(inverseMatrix)
        val pt = mapPoint(ev.x, ev.y, inverseMatrix)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDrawing = true
                activePathPoints.clear()
                activePathPoints.add(pt)
                lastX = pt.x
                lastY = pt.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrawing) {
                    activePathPoints.add(pt)
                    drawSegment(lastX, lastY, pt.x, pt.y)
                    lastX = pt.x
                    lastY = pt.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDrawing) {
                    commitStroke()
                    isDrawing = false
                    invalidate()
                }
            }
        }
        return true
    }

    private fun drawSegment(x0: Float, y0: Float, x1: Float, y1: Float) {
        val paint = makePaint(currentTool, currentColor, currentWidthPx)
        strokeCanvas.drawLine(x0, y0, x1, y1, paint)
    }

    private fun commitStroke() {
        if (activePathPoints.size < 2) return
        undoStack.add(
            Stroke(
                points = ArrayList(activePathPoints),
                color = currentColor,
                widthPx = currentWidthPx,
                tool = currentTool
            )
        )
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val stroke = undoStack.removeAt(undoStack.size - 1)
        redoStack.add(stroke)
        replayStrokes()
        invalidate()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val stroke = redoStack.removeAt(redoStack.size - 1)
        undoStack.add(stroke)
        replayStrokes()
        invalidate()
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    private fun replayStrokes() {
        strokeBitmap.eraseColor(Color.TRANSPARENT)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val pathPaint = Paint(paint)
        for (s in undoStack) {
            pathPaint.color = s.color
            pathPaint.strokeWidth = s.widthPx
            if (s.tool == Tool.ERASER) {
                pathPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                pathPaint.xfermode = null
            }
            for (i in 1 until s.points.size) {
                val a = s.points[i - 1]
                val b = s.points[i]
                strokeCanvas.drawLine(a.x, a.y, b.x, b.y, pathPaint)
            }
        }
    }

    private fun makePaint(tool: Tool, color: Int, width: Float) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            this.color = color
            strokeWidth = width
            if (tool == Tool.ERASER) {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
        }

    private fun mapPoint(x: Float, y: Float, m: Matrix): PointF {
        val pts = floatArrayOf(x, y)
        m.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density

    fun getStrokeBitmap(): Bitmap = strokeBitmap
    fun getBackgroundBitmap(): Bitmap? = bgBitmap

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = currentScale * scaleFactor

            if (newScale in minScale..maxScale) {
                val focusX = detector.focusX
                val focusY = detector.focusY

                contentMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
                currentScale = newScale
                invalidate()
            }
            return true
        }
    }

    private inner class PanListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e2.pointerCount > 1) {
                contentMatrix.postTranslate(-distanceX, -distanceY)
                invalidate()
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // Quick zoom to fit
            fitToView()
            invalidate()
            return true
        }
    }
}
