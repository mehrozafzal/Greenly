package greenely.greenely.signature.ui

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import greenely.greenely.R
import java.io.Serializable


/**
 * Draw actions represented as a percentage of the view in order to handle configuration changes.
 */
sealed class PercentageDrawAction : Serializable {
    /**
     * Move the cursor.
     */
    data class Move(val x: Float, val y: Float) : PercentageDrawAction(), Serializable

    /**
     * Draw a quad line.
     */
    data class Quad(
            val x: Float, val y: Float, val xTarget: Float, val yTarget: Float
    ) : PercentageDrawAction(), Serializable
}

/**
 * View that allows for drawing a signature.
 */
class SignatureView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    companion object {
        private val TOUCH_LIMIT = 4
    }

    private val paint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = resources.getDimensionPixelSize(R.dimen.signature_stroke_width).toFloat()
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    }
    private val path: Path = Path()
    private var xPos: Float = 0.toFloat()
    private var yPos: Float = 0.toFloat()
    private val drawActionList: MutableList<PercentageDrawAction> = mutableListOf()
    private val drawActionChangeListeners = mutableListOf<OnBitmapChangeListener>()

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun performClick(): Boolean {
        super.performClick()
        drawActionChangeListeners.forEach {
            it.bitmapChanged(createBitmap())
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = Math.abs(xPos - event.x)
                val dy = Math.abs(yPos - event.y)
                if (dx > TOUCH_LIMIT || dy > TOUCH_LIMIT) {
                    drawActionList += PercentageDrawAction.Quad(
                            xPos / width,
                            yPos / height,
                            ((xPos + event.x) / 2) / width,
                            ((yPos + event.y) / 2) / height
                    )
                    drawActionList += PercentageDrawAction.Move(
                            ((xPos + event.x) / 2) / width,
                            ((yPos + event.y) / 2) / height
                    )
                    xPos = event.x
                    yPos = event.y
                }

                invalidate()
            }
            MotionEvent.ACTION_DOWN -> {
                xPos = event.x
                yPos = event.y
                drawActionList += PercentageDrawAction.Move(
                        event.x / width,
                        event.y / height
                )
            }
            MotionEvent.ACTION_UP -> {
                return performClick()
            }
            else -> return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(createPath(), paint)
    }


    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState(), drawActionList)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            drawActionList.addAll(state.drawActionList)
        } else {
            // Should never happen
            super.onRestoreInstanceState(state)
        }
    }

    fun addListener(drawActionsChangeListener: OnBitmapChangeListener) {
        drawActionChangeListeners.add(drawActionsChangeListener)
    }

    fun removeListener(drawActionsChangeListener: OnBitmapChangeListener) {
        drawActionChangeListeners.remove(drawActionsChangeListener)
    }

    private fun createPath(): Path {
        path.reset()
        drawActionList.forEach {
            when (it) {
                is PercentageDrawAction.Quad -> {
                    path.quadTo(
                            it.x * width,
                            it.y * height,
                            it.xTarget * width,
                            it.yTarget * height
                    )
                }
                is PercentageDrawAction.Move -> {
                    path.moveTo(
                            it.x * width,
                            it.y * height
                    )
                }
            }
        }
        return path
    }

    fun reset() {
        drawActionList.clear()
        invalidate()
        drawActionChangeListeners.forEach { it.bitmapChanged(null) }
    }

    /**
     * Get the a bitmap representation of what the user has drawn.

     * @return A representation of whats drawn on the view.
     */
    fun createBitmap(): Bitmap {
        val width = if (width > 0) width else 1
        val height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawPath(createPath(), paint)

        return bitmap
    }

    interface OnBitmapChangeListener {
        fun bitmapChanged(bitmap: Bitmap?)
    }

    private class SavedState : View.BaseSavedState {

        /**
         * A list of draw actions.
         */
        val drawActionList: List<PercentageDrawAction>

        constructor(superParcel: Parcelable, drawActionList: List<PercentageDrawAction>) : super(superParcel) {
            this.drawActionList = drawActionList
        }

        constructor(input: Parcel) : super(input) {
            val len = input.readInt()
            drawActionList = (0..len).map { input.readSerializable() as? PercentageDrawAction }
                    .filterNotNull()
        }

        /**
         * Write the state to the parcel.
         */
        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            //out?.writeInt(drawActionList.size)
            drawActionList.forEach { out?.writeSerializable(it) }
        }
    }
}
