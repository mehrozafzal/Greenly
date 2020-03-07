
package greenely.greenely.solaranalysis.ui.householdinfo.charting

import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class)
class ChartManagerTest {
    private lateinit var activity: SolarAnalysisActivity
    private lateinit var chart: BarChart
    private lateinit var chartStyler: ChartStyler
    private lateinit var valueFormatter: AxisValueFormatter
    private lateinit var chartManager: ChartManager

    @Before
    fun setUp() {
        activity = mock {
            on { getString(R.string.production) } doReturn "Production"
        }
        chart = mock {
            on { xAxis } doReturn mock<XAxis>()
        }
        chartStyler = mock()
        valueFormatter = mock()

        chartManager = ChartManager(activity, chartStyler, valueFormatter)

        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(activity, R.color.accent))
                .thenReturn(0)
    }

    @Test
    fun testSetUpChart() {
        // Given
        val chartData = (0 until 12).map {
            it.toFloat()
        }

        // When
        chartManager.setUpChart(chart, chartData)

        // Then
        val barDataCaptor = argumentCaptor<BarData>()

        verify(chart).data = barDataCaptor.capture()
        verify(chart.xAxis).valueFormatter = valueFormatter
        verify(chart.xAxis).labelCount = 12
        verify(chartStyler).styleChart(chart)

        val dataSet = barDataCaptor.lastValue.dataSets[0]
        assertThat(dataSet.color).isEqualTo(0)
        for (i in (0 until 12)) {
            assertThat(dataSet.getEntriesForXValue(i.toFloat()).map { it.y })
                    .isEqualTo(listOf(i.toFloat()))
        }
    }
}
