package greenely.greenely.retail.ui.charts

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import greenely.greenely.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class LineChartMarkView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        if (e is CandleEntry) {

            val ce = e as CandleEntry?

            tvContent.text = Utils.formatNumber(ce!!.high, 0, false)

        } else {

            val formattedDate = e?.x?.toBigDecimal()?.toPlainString()

            if (e != null) {
                tvContent.text = Utils.formatNumber(e.y / 100, 1, false) + " öre/kWh | " + formattedDate?.let { getDateTime(it) }
            }
        }

        super.refreshContent(e, highlight)
    }

    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("Europe/Stockholm")
            val netDate = toNearestWholeHour(Date(s.toLong() * 1000))
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    private fun toNearestWholeHour(d: Date): Date {
        val calendar = GregorianCalendar()
        calendar.time = d

        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        return calendar.time
    }
}
