package greenely.greenely.feed.ui.monthlyreport

import android.content.Context
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.cardview.widget.CardView
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import greenely.greenely.databinding.FeedMonthItemBinding
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.MonthlyChartData
import greenely.greenely.feed.models.feeditems.MonthlyReport
import greenely.greenely.feed.utils.charting.ChartManager
import greenely.greenely.feed.utils.charting.MonthlyReportChartManager
import greenely.greenely.feed.utils.charting.MultiChartManager
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*


class FeedMonthlyReportView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defaultStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attributeSet, defaultStyleAttr) {

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormat
                .forPattern("d")
                .withLocale(Locale("sv", "SE"))
    }

    @VisibleForTesting
    val binding = FeedMonthItemBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundColor(resources.getColor(android.R.color.transparent,null))
        }
        else
            setBackgroundColor(resources.getColor(android.R.color.transparent))

    }

    var monthlyReport: MonthlyReport?
        get() = binding.monthlyReport
        set(value) {
            binding.monthlyReport = value
            val chartData = value?.chartData

            chartData?.let {
                setUpChart(chartData)
            }


            binding.executePendingBindings()
        }

    private fun setUpChart(chartData: MonthlyChartData?) {
        binding.chartArea.visibility = View.VISIBLE

        chartData?.let {
            binding.headerModel = HeaderModel(context, it)
        }
        MonthlyReportChartManager(binding.chart, dateFormatter).chartData = chartData
    }



}

