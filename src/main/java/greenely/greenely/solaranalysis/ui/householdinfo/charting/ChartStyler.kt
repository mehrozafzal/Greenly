package greenely.greenely.solaranalysis.ui.householdinfo.charting

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import greenely.greenely.OpenClassOnDebug
import javax.inject.Inject

@OpenClassOnDebug
class ChartStyler @Inject constructor() {
    fun styleChart(chart: BarChart) {
        chart.setScaleEnabled(false)
        chart.setTouchEnabled(false)
        chart.axisRight.isEnabled = false

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.setDrawGridLines(false)

        chart.axisLeft.setDrawAxisLine(false)
        chart.axisLeft.axisMinimum = 0.0f

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
    }
}

