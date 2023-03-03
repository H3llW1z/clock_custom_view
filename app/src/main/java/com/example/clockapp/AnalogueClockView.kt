package com.example.clockapp

import android.content.Context
import android.graphics.*
import android.icu.util.Calendar
import android.util.AttributeSet
import android.view.View
import java.lang.Integer.min
import kotlin.math.cos
import kotlin.math.sin


class AnalogueClockView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mHeight = 0
    private var mWidth = 0
    private var mMinimum = 0
    private var mCentreX = 0f
    private var mCentreY = 0f

    private var mPadding = 0

    private var mRadius = 0f
    private var mPinRadius = 0f
    private var mBorderDotRadius = 0f
    private var mAngle = 0.0

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRect: Rect = Rect()
    private val mTypeface = Typeface.create("sans-serif-thin", Typeface.NORMAL)

    private val mNumbers = Array(12) { i: Int -> i + 1 }

    private var mHour = 0f
    private var mMinute = 0f
    private var mSecond = 0f

    private var mHourHandSize = 0
    private var mHourHandTailSize = 0
    private var mHourHandWidth = 0f

    private var mHandSize = 0
    private var mHandTailSize = 0
    private var mHandWidth = 0f

    private var mBorderWidth = 0f

    private var mIsInit = false

    private var mBackgroundColor = 0
    private var mHourHandColor = 0
    private var mMinuteHandColor = 0
    private var mSecondHandColor = 0
    private var mBorderColor = 0
    private var mBorderDotsEnabled = true
    private var mNumeralsEnabled = true
    private var mBorderDotsColor = 0
    private var mNumeralsColor = 0
    private var mCenterPinColor = 0

    init {
        getContext().obtainStyledAttributes(
            attrs,
            R.styleable.AnalogueClockView
        ).apply {
            mHourHandColor = getColor(R.styleable.AnalogueClockView_hourHandColor, Color.BLACK)
            mMinuteHandColor = getColor(R.styleable.AnalogueClockView_minuteHandColor, Color.BLACK)
            mSecondHandColor = getColor(R.styleable.AnalogueClockView_secondHandColor, Color.BLACK)
            mBorderDotsEnabled = getBoolean(
                R.styleable.AnalogueClockView_enableBorderDots,
                true
            )
            mBackgroundColor = getColor(
                R.styleable.AnalogueClockView_backgroundColor,
                Color.parseColor("#e4e0dd")
            )
            mBorderColor = getColor(
                R.styleable.AnalogueClockView_borderColor, Color.BLACK
            )
            mBorderDotsColor = getColor(
                R.styleable.AnalogueClockView_borderDotsColor,
                Color.BLACK
            )
            mNumeralsColor = getColor(
                R.styleable.AnalogueClockView_numeralsColor,
                Color.BLACK
            )
            mCenterPinColor = getColor(
                R.styleable.AnalogueClockView_centerPinColor,
                Color.GRAY
            )
            mNumeralsEnabled = getBoolean(
                R.styleable.AnalogueClockView_enableNumerals,
                true
            )
            recycle()
        }
    }

    private fun initializeVariables() {
        mHeight = height
        mWidth = width
        mCentreX = (mWidth / 2).toFloat()
        mCentreY = (mHeight / 2).toFloat()
        mMinimum = min(mHeight, mWidth)

        mPadding = mMinimum / 20

        mRadius = (mMinimum / 2 - mPadding).toFloat()
        mPinRadius = mRadius / 35
        mBorderDotRadius = mPinRadius / 2
        mBorderWidth = (mRadius * 0.07).toFloat()
        mHourHandSize = (mRadius - mRadius / 2).toInt()
        mHourHandTailSize = mHourHandSize / 8
        mHourHandWidth = 3 * mPinRadius
        mHandSize = (mRadius - mRadius / 4).toInt()
        mHandWidth = 1.1f * mPinRadius
        mHandTailSize = mHandSize / 8
        mIsInit = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!mIsInit) {
            initializeVariables()
        }
        canvas?.let {
            drawBase(it)
            if (mNumeralsEnabled) {
                drawNumerals(it)
            }
            drawHands(it)
            drawCenterPin(it)
            postInvalidateDelayed(500)
        }
    }

    private fun drawBase(canvas: Canvas) {
        //draw background circle
        setPaintAttributes(mBackgroundColor, Paint.Style.FILL)
        canvas.drawCircle(mCentreX, mCentreY, mRadius, mPaint)

        //draw circle of dots
        if (mBorderDotsEnabled) {
            setPaintAttributes(mBorderDotsColor, Paint.Style.FILL)
            val dotCircleRadius = mRadius - mRadius / 10
            for (i in 1..60) {
                mAngle = Math.PI * i / 30 - Math.PI / 2
                val x = (mCentreX + cos(mAngle) * dotCircleRadius).toFloat()
                val y = (mCentreY + sin(mAngle) * dotCircleRadius).toFloat()
                val radius = if (i % 5 == 0) 1.5f * mBorderDotRadius else mBorderDotRadius
                canvas.drawCircle(x, y, radius, mPaint)
            }
        }

        //draw border circle
        setPaintAttributes(mBorderColor, Paint.Style.STROKE, mBorderWidth)
        canvas.drawCircle(mCentreX, mCentreY, mRadius, mPaint)
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        mHour = (calendar.get(Calendar.HOUR_OF_DAY) % 12).toFloat()
        mMinute = calendar.get(Calendar.MINUTE).toFloat()
        mSecond = calendar.get(Calendar.SECOND).toFloat()

        drawHourHand(canvas, (mHour + mMinute / 60f) * 5f)
        drawMinuteHand(canvas, mMinute)
        drawSecondHand(canvas, mSecond)
    }

    private fun drawCenterPin(canvas: Canvas) {
        setPaintAttributes(mCenterPinColor, Paint.Style.FILL)
        canvas.drawCircle(mCentreX, mCentreY, mPinRadius, mPaint)
    }

    private fun drawMinuteHand(canvas: Canvas, location: Float) {
        setPaintAttributes(mMinuteHandColor, Paint.Style.STROKE, mHandWidth)
        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            (mCentreX - cos(mAngle) * mHandTailSize).toFloat(),
            (mCentreY - sin(mAngle) * mHandTailSize).toFloat(),
            (mCentreX + cos(mAngle) * mHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawHourHand(canvas: Canvas, location: Float) {
        setPaintAttributes(mHourHandColor, Paint.Style.STROKE, mHourHandWidth)
        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            (mCentreX - cos(mAngle) * mHourHandTailSize).toFloat(),
            (mCentreY - sin(mAngle) * mHourHandTailSize).toFloat(),
            (mCentreX + cos(mAngle) * mHourHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHourHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawSecondHand(canvas: Canvas, location: Float) {
        setPaintAttributes(mSecondHandColor, Paint.Style.STROKE, mHandWidth)
        mAngle = Math.PI * location / 30 - Math.PI / 2
        canvas.drawLine(
            (mCentreX - cos(mAngle) * mHandTailSize).toFloat(),
            (mCentreY - sin(mAngle) * mHandTailSize).toFloat(),
            (mCentreX + cos(mAngle) * mHandSize).toFloat(),
            (mCentreY + sin(mAngle) * mHandSize).toFloat(),
            mPaint
        )
    }

    private fun drawNumerals(canvas: Canvas) {
        setPaintAttributes(mNumeralsColor, Paint.Style.FILL_AND_STROKE, 2f)
        mPaint.textSize = mRadius / 4
        mPaint.typeface = mTypeface
        val numeralCircleRadius = mRadius - mRadius / 4
        for (number in mNumbers) {
            val numeral = number.toString()
            mPaint.getTextBounds(numeral, 0, numeral.length, mRect)
            mAngle = Math.PI / 6 * (number - 3)
            val x = (mCentreX + cos(mAngle) * numeralCircleRadius - mRect.width() / 2).toFloat()
            val y = (mCentreY + sin(mAngle) * numeralCircleRadius + mRect.height() / 2).toFloat()
            canvas.drawText(numeral, x, y, mPaint)
        }
    }

    private fun setPaintAttributes(
        color: Int,
        style: Paint.Style,
        strokeWidth: Float = 0f,
    ) {
        mPaint.reset()
        mPaint.color = color
        mPaint.style = style
        mPaint.strokeWidth = strokeWidth
    }
}