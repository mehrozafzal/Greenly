package greenely.greenely.solaranalysis.ui.householdinfo.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import javax.inject.Inject

@OpenClassOnDebug
class ChartManager @Inject constructor(
        private val activity: SolarAnalysisActivity,
        private val styler: ChartStyler,
        private val valueFormatter: AxisValueFormatter
) {
    fun setUpChart(chart: BarChart, data: List<Float>) {
        chart.xAxis.valueFormatter = valueFormatter
        chart.xAxis.labelCount = 12
        chart.xAxis.granularity = 1.0f
        styler.styleChart(chart)
        setChartData(data, chart)
        chart.invalidate()
    }

    private fun setChartData(data: List<Float>, chart: BarChart) {
        val chartEntries = data.mapIndexed { index, d -> BarEntry(index.toFloat(), d) }
        val dataSet = BarDataSet(
                chartEntries,
                activity.getString(R.string.production)
        ).apply {
            color = ContextCompat.getColor(activity, R.color.accent)
            setDrawValues(false)
        }

        chart.data = BarData(dataSet)
    }
}

