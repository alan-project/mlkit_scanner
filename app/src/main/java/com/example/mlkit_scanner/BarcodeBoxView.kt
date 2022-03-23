package com.example.mlkit_scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View

class BarcodeBoxView(context:Context): View(context) {

    init {
        setWillNotDraw(false)
    }

    private val paint = Paint()
    private var mRect = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("alan","onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        Log.d("alan","onSizeChanged")
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        Log.d("alan", "BarcodeBoxView: onDraw")

        val cornerRadius = 10f

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f

        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
    }

    fun setRect(rect: RectF) {
        mRect = rect
//        mRect = RectF(100F,100F,100F,100F)
        requestLayout()
        invalidate()
        Log.d(
            "alan",
            "Left: ${rect.left}, Right: ${rect.right}, Top: ${rect.top}, Bottom: ${rect.bottom}"
        )
    }
}