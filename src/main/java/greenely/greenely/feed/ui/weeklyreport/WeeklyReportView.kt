package greenely.greenely.feed.ui.weeklyreport

import android.content.Context
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.cardview.widget.CardView
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import greenely.greenely.R
import greenely.greenely.databinding.FeedWeeklyItemBinding
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.feeditems.WeeklyReport
import greenely.greenely.feed.utils.charting.ChartManager
import greenely.greenely.feed.utils.charting.WeeklyReportChartManager
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

class WeeklyReportView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defaultStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attributeSet, defaultStyleAttr) {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormat
                .forPattern("EE")
                .withLocale(Locale("sv", "SE"))
    }

    @VisibleForTesting
    val binding = FeedWeeklyItemBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
    )

    init {
        binding.description.movementMethod = LinkMovementMethod()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundColor(resources.getColor(android.R.color.transparent,null))
        }
        else
            setBackgroundColor(resources.getColor(android.R.color.transparent))

    }

    var weeklyReport: WeeklyReport?
        get() = binding.weeklyReport
        set(value) {
            binding.weeklyReport = value
            val currentWeekChartData = value?.currentPeriodChartData

            value?.previousPeriodTitle = value?.previousPeriodChartData?.date?.let {
                context.getString(R.string.week_x).format(it.weekOfWeekyear)
            }

            if(value?.previousPeriodTitle==null)
                value?.previousPeriodTitle=context.getString(R.string.week_x).format(currentWeekChartData?.date?.weekOfWeekyear?.minus(1))

            if (currentWeekChartData != null) setUpChart(currentWeekChartData,value.previousPeriodChartData)
            else binding.chartArea.visibility = View.GONE

            binding.executePendingBindings()
        }

    private fun setUpChart(currentWeekChartData: ChartData,previousWeekChartData: ChartData?) {
        binding.chartArea.visibility = View.VISIBLE
        binding.headerModel = HeaderModel(context, currentWeekChartData)
        WeeklyReportChartManager(binding.chart,previousWeekChartData, dateFormatter).chartData=currentWeekChartData
    }
}

