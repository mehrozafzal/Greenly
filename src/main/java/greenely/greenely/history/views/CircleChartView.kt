package greenely.greenely.history.views

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.widget.TextView
import greenely.greenely.R
import greenely.greenely.extensions.dp

/**
 * View to chow a circle progress with text in the middle
 */
class CircleChartView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : TextView(context, attributeSet, defStyleAttr) {

    private val circleRect = RectF()
    private val arcRect = RectF()

    private val STROKE_WIDTH_DP=6
    private val STROKE_WIDTH=context.dp(STROKE_WIDTH_DP)

    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    private val arcPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.green_1)
        strokeCap=Paint.Cap.ROUND
        strokeJoin=Paint.Join.ROUND
        strokeWidth=STROKE_WIDTH
    }

    /**
     * The progress to be shown by the view.
     */
    var progress: Int = 0
        set(value) {
            field = value
            text = context.getString(R.string.percentage_format).format(value)
        }

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        circleRect.left = 0.0f
        circleRect.top = 0.0f
        circleRect.bottom = height.toFloat()
        circleRect.right = width.toFloat()

        arcRect.left = STROKE_WIDTH/2
        arcRect.top = STROKE_WIDTH/2
        arcRect.bottom = height.toFloat()-STROKE_WIDTH/2
        arcRect.right = width.toFloat()-STROKE_WIDTH/2

        canvas?.drawCircle(
                circleRect.centerX(),
                circleRect.centerY(),
                circleRect.width() / 2,
                circlePaint.apply {
                    color = ContextCompat.getColor(context, R.color.grey20)
                }
        )
        canvas?.drawArc(
                arcRect,
                -90.0f,
                360.0f * progress.toFloat() / 100.0f,
                false,
                arcPaint
        )
        canvas?.drawCircle(
                circleRect.centerX(),
                circleRect.centerY(),
                circleRect.width() / 2 - STROKE_WIDTH,
                circlePaint.apply {
                    color = ContextCompat.getColor(context, R.color.white)
                }
        )

        super.onDraw(canvas)
    }
}

