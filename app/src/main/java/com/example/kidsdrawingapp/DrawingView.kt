package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

// Drawing needs to be done in a 'View' type
class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.WHITE  // The initial color will be white
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if (mPaths.size > 0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }

    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize) // These 2 var were not empty so we can directly use
        mDrawPaint!!.color = color                // Since the var can be null so non-null asserted call
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        // mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Change Canvas to Canvas? if it fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        for (path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }
        if (!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x  // To get the x axis position when touched on screen
        val touchY = event?.y  // To get the y axis position when touched on screen
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {  // This means when we touch our finger on screen
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {  // When we are moving our finger in the screen
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {   // When we remove our finger from the screen
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()  // To force a view to draw, call invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float){
        // This will adjust the brush size on different screens with different pixel density i.e. the "TypedValue" function
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)  // Parse the color string into color int.
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path() {

    }
}
