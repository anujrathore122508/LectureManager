package com.example.lecturemanager.ui.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var progress = 0
    private var strokeWidth = 20f
    private var color = Color.GREEN
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressBar.strokeWidth
        color = this@CircularProgressBar.color
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        updateColor()
        invalidate()
    }

     fun updateColor() {
        color = when {
            progress >= 100 -> Color.parseColor("#4ABF47")
            progress >= 75 -> Color.parseColor("#6495ED")
            progress >= 60 -> Color.YELLOW
            progress >= 30 -> Color.CYAN
            else -> Color.WHITE
        }
        paint.color = color
         invalidate() // Force redraw to apply the new color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = min(width, height) / 2f - strokeWidth / 2
        val centerX = width / 2f
        val centerY = height / 2f

        paint.style = Paint.Style.STROKE
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.style = Paint.Style.FILL
        paint.textSize = 50f
        paint.textAlign = Paint.Align.CENTER

        paint.color = Color.BLACK
        paint.isFakeBoldText = true

        canvas.drawText("$progress%", centerX, centerY + 20f, paint)
    }
}
