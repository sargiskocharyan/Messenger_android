package com.example.dynamicmessenger.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.dynamicmessenger.R

class CustomGradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_START_COLOR = Color.BLACK
        private const val DEFAULT_END_COLOR = Color.BLUE
        private const val DEFAULT_PLACE = true
    }

    private var viewGradientStartColor = DEFAULT_START_COLOR
    private var viewGradientEndColor = DEFAULT_END_COLOR
    private var downViewGradientEndColor = DEFAULT_END_COLOR
    private var downViewGradientStartColor = DEFAULT_END_COLOR
    private var viewPlace = DEFAULT_PLACE

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCorrectView(canvas)
    }

    private val firstPaint = Paint()
    private val secondPaint = Paint()
    private val firstViewPath = Path()
    private val secondViewPath = Path()
    private var sizeX = 0
    private var sizeY = 0

    init {
        firstPaint.isAntiAlias = true
        secondPaint.isAntiAlias = true
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs,
            R.styleable.CustomGradientView,
            0, 0)

        viewGradientStartColor = typedArray.getColor(
            R.styleable.CustomGradientView_startColor,
            DEFAULT_START_COLOR
        )
        viewGradientEndColor = typedArray.getColor(
            R.styleable.CustomGradientView_endColor,
            DEFAULT_END_COLOR
        )
        downViewGradientStartColor = typedArray.getColor(
            R.styleable.CustomGradientView_downStartColor,
            DEFAULT_START_COLOR
        )
        downViewGradientEndColor = typedArray.getColor(
            R.styleable.CustomGradientView_downEndColor,
            DEFAULT_END_COLOR
        )
        viewPlace = typedArray.getBoolean(
            R.styleable.CustomGradientView_isTopView,
            DEFAULT_PLACE
        )
        typedArray.recycle()
    }

    private fun drawView(canvas: Canvas) {
        firstViewPath.reset()
        firstViewPath.moveTo(sizeX * 0.12f, sizeY * 0f)
        firstViewPath.quadTo(sizeX * 0.125f, sizeY * 0.05f, sizeX * 0.2f, sizeY * 0.10f)
        firstViewPath.quadTo(sizeX * 0.31f, sizeY * 0.16f, sizeX * 0.42f, sizeY * 0.20f)
        firstViewPath.quadTo(sizeX * 0.74f, sizeY * 0.31f,sizeX * 0.91f, sizeY * 0.77f)
        firstViewPath.quadTo(sizeX * 0.98f, sizeY * 0.97f,sizeX * 1f, sizeY * 0.95f)
        firstViewPath.quadTo(sizeX * 1f, sizeY * 0.5f,sizeX * 1f, sizeY * 0f)
        firstViewPath.quadTo(sizeX * 0.5f, sizeY * 0f,sizeX * 0.12f, sizeY * 0f)
        firstPaint.shader = LinearGradient(sizeX * 0f,sizeY * 0f, sizeX * 0f, sizeY * 1f,
                        viewGradientStartColor, viewGradientEndColor, Shader.TileMode.CLAMP)
        firstPaint.style = Paint.Style.FILL
        canvas.drawPath(firstViewPath, firstPaint)
    }

    private fun drawSecondView(canvas: Canvas) {
        secondViewPath.reset()
        secondViewPath.moveTo(sizeX * 0.12f, sizeY * 0f)
        secondViewPath.quadTo(sizeX * 0.12f, sizeY * 0.1f, sizeX * 0.2f, sizeY * 0.14f)
        secondViewPath.quadTo(sizeX * 0.30f, sizeY * 0.2f, sizeX * 0.42f, sizeY * 0.24f)
        secondViewPath.quadTo(sizeX * 0.74f, sizeY * 0.36f,sizeX * 0.91f, sizeY * 0.8f)
        secondViewPath.quadTo(sizeX * 0.97f, sizeY * 0.98f,sizeX * 1f, sizeY * 1f)
        secondViewPath.quadTo(sizeX * 1f, sizeY * 0.5f,sizeX * 1f, sizeY * 0f)
        secondViewPath.quadTo(sizeX * 0.5f, sizeY * 0f,sizeX * 0.12f, sizeY * 0f)
        secondPaint.shader = LinearGradient(sizeX * 0f,sizeY * 0f, sizeX * 0f, sizeY * 1f,
                        downViewGradientStartColor, downViewGradientEndColor, Shader.TileMode.CLAMP)
        secondPaint.style = Paint.Style.FILL
        secondPaint.alpha
        canvas.drawPath(secondViewPath, secondPaint)
    }

    private fun drawBottomView(canvas: Canvas) {
        firstViewPath.reset()
        firstViewPath.moveTo(sizeX * 0f, sizeY * 0.3f)
        firstViewPath.quadTo(sizeX * 0.1f, sizeY * 0.75f, sizeX * 0.27f, sizeY * 0.9f)
        firstViewPath.quadTo(sizeX * 0.35f, sizeY * 0.95f, sizeX * 0.39f, sizeY * 1f)
        firstViewPath.quadTo(sizeX * 0f, sizeY * 1f,sizeX * 0f, sizeY * 1f)
        firstViewPath.quadTo(sizeX * 0f, sizeY * 0.5f,sizeX * 0f, sizeY * 0f)
        firstPaint.shader = LinearGradient(sizeX * 0f,sizeY * 0f, sizeX * 0f, sizeY * 1f,
            viewGradientStartColor, viewGradientEndColor, Shader.TileMode.CLAMP)
        firstPaint.style = Paint.Style.FILL
        canvas.drawPath(firstViewPath, firstPaint)
    }

    private fun drawSecondBottomView(canvas: Canvas) {
        secondViewPath.reset()
        secondViewPath.moveTo(sizeX * 0f, sizeY * 0f)
        secondViewPath.quadTo(sizeX * 0.10f, sizeY * 0.6f, sizeX * 0.19f, sizeY * 0.74f)
        secondViewPath.quadTo(sizeX * 0.22f, sizeY * 0.80f, sizeX * 0.25f, sizeY * 0.83f)
        secondViewPath.quadTo(sizeX * 0.3f, sizeY * 0.9f,sizeX * 0.49f, sizeY * 0.92f)
        secondViewPath.quadTo(sizeX * 0.54f, sizeY * 0.95f,sizeX * 0.56f, sizeY * 1f)
        secondViewPath.quadTo(sizeX * 0f, sizeY * 1f,sizeX * 0f, sizeY * 1f)
        secondViewPath.quadTo(sizeX * 0f, sizeY * 0.5f,sizeX * 0f, sizeY * 0f)
        secondPaint.shader = LinearGradient(sizeX * 0f,sizeY * 0f, sizeX * 0f, sizeY * 1f,
            downViewGradientStartColor, downViewGradientEndColor, Shader.TileMode.CLAMP)
        secondPaint.style = Paint.Style.FILL
        secondPaint.alpha
        canvas.drawPath(secondViewPath, secondPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        sizeX = measuredWidth.coerceAtMost(measuredWidth)
        sizeY = measuredHeight.coerceAtMost(measuredHeight)
        setMeasuredDimension(sizeX, sizeY)
    }

    private fun drawCorrectView(canvas: Canvas) {
        if (viewPlace) {
            drawSecondView(canvas)
            drawView(canvas)
        } else {
            drawSecondBottomView(canvas)
            drawBottomView(canvas)
        }
    }
}