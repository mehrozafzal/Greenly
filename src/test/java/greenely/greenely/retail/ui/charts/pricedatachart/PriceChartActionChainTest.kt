package greenely.greenely.retail.ui.charts.pricedatachart

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.retail.ui.charts.next_day_pricing_chart.*
import greenely.greenely.retail.ui.charts.next_day_pricing_chart.StyleChart
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class StyleXAxisTest {

    private val xAxis = mock<XAxis>()

    private val chart = mock<LineChart> {
        on { this.xAxis }.thenReturn(xAxis)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleXAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(xAxis).granularity = 1.0f
        verify(xAxis).axisMinimum = 0.0f
        verify(xAxis).setDrawGridLines(false)
        verify(xAxis).setDrawLabels(false)
        verify(xAxis).setDrawAxisLine(false)

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock< PriceChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleXAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}


@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class)
class StyleLeftAxisTest {
    private val axis = mock<YAxis>()
    private val chart = mock<LineChart> {
        on { axisLeft }.thenReturn(axis)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleLeftAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(axis).isEnabled = false

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<PriceChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleLeftAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}

class StyleRightAxisTest {
    private val axis = mock<YAxis>()
    private val chart = mock<LineChart> {
        on { axisRight }.thenReturn(axis)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleRightAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(axis).isEnabled = false

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<PriceChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleRightAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class)
class StyleChartTest {

    private val description = mock<Description>()
    private val legend = mock<Legend>()
    private val chart = mock<LineChart> {
        on { this.description }.thenReturn(description)
        on { this.legend }.thenReturn(legend)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleChart()

        // When
        val nextChart = chain.applyToChart(chart)

        //Then
        verify(chart).setScaleEnabled(false)
        verify(description).isEnabled = false
        verify(legend).isEnabled = false
        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<NextDayChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleChart()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class)
class SetChartDataTest {
    private val context = mock<Context> {
        on { getString(R.string.prices) }.thenReturn("prices")
    }

    private val chart = mock<LineChart> {
        on { this.context }.thenReturn(context)
    }

    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.shadow)))
                .thenReturn(0)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.white)))
                .thenReturn(1)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val points = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
        val chain = SetChartData(points)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        val lineDataCaptor = argumentCaptor<LineData>()
        verify(chart).data = lineDataCaptor.capture()
        verify(chart).invalidate()

        val lineDataSet = lineDataCaptor.firstValue.dataSets
        val values = lineDataSet[0]

        Assertions.assertThat(values.mode).isEqualTo(LineDataSet.Mode.HORIZONTAL_BEZIER)
        Assertions.assertThat(values.color).isEqualTo(1)
        Assertions.assertThat(values.isDrawFilledEnabled).isTrue()
        Assertions.assertThat(values.lineWidth).isEqualTo(3.0f)
        Assertions.assertThat(values.isDrawCirclesEnabled).isFalse()
        Assertions.assertThat(values.fillColor).isEqualTo(0)
        Assertions.assertThat(values.isDrawValuesEnabled).isFalse()

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val points = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
        val differentChart = mock<LineChart>()
        val next = mock<PriceChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = SetChartData(points)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}


