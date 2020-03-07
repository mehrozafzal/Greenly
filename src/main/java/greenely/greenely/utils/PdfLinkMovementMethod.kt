package greenely.greenely.utils

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import greenely.greenely.utils.pdfview.PdfViewer
import kotlin.math.roundToInt

class PdfLinkMovementMethod(private val context: Context) : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP)
            return super.onTouchEvent(widget, buffer, event)

        val x = getXPosForEventInWidget(event, widget)
        val y = getYPosForEventInWidget(event, widget)

        val layout = widget.layout

        val line = layout.getLineForVertical(y.roundToInt())
        val off = layout.getOffsetForHorizontal(line, x)

        val links = buffer.getSpans(off, off, URLSpan::class.java)
        if (links.isNotEmpty()) {
            pdfLinkClicked(links[0].url)
        }

        return true
    }

    private fun getXPosForEventInWidget(event: MotionEvent, widget: TextView): Float =
            event.x - widget.totalPaddingLeft + widget.scrollX

    private fun getYPosForEventInWidget(event: MotionEvent, widget: TextView): Float =
            event.y - widget.totalPaddingTop + widget.scrollY

    private fun pdfLinkClicked(fileName: String) {
        context.startActivity(Intent(context, PdfViewer::class.java).apply { putExtra("fileName",fileName)
        })
    }
}
