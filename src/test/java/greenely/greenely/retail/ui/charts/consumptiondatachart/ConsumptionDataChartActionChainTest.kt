package greenely.greenely.retail.ui.charts.consumptiondatachart

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.utils.ViewPortHandler
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.retail.models.CurrentMonthPointsJson
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class StyleXAxisTest {

    private val xAxis = mock<XAxis>()
    private val chart = mock<BarChart> {
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
        verify(xAxis).setDrawGridLines(false)
        verify(xAxis).setDrawLabels(false)
        verify(xAxis).setDrawAxisLine(false)

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<BarChart>()
        val next = mock<ConsumptionBarChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
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
@PrepareForTest(ContextCompat::class)
class StyleLeftAxisText {
    private val leftAxis = mock<YAxis>()
    private val chart = mock<BarChart> {
        on { axisLeft }.thenReturn(leftAxis)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleLeftAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(leftAxis).axisMinimum = 0.0f
        verify(leftAxis).setDrawGridLines(false)
        verify(leftAxis).setDrawAxisLine(false)
        verify(leftAxis).setDrawLabels(false)

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<BarChart>()
        val next = mock<ConsumptionBarChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
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
    private val chart = mock<BarChart> {
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
        val differentChart = mock<BarChart>()
        val next = mock<ConsumptionBarChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }
        val chain = StyleRightAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}

class StyleChartTest {
    private val description = mock<Description>()
    private val legend = mock<Legend>()
    private val animator = mock<ChartAnimator>()
    private val renderer = mock<CustomBarChartRenderer>()
    private val viewPortHandler = mock<ViewPortHandler>()
    private val chart = mock<BarChart> {
        on { this.description }.thenReturn(description)
        on { this.legend }.thenReturn(legend)
        on { this.animator }.thenReturn(animator)
        on { this.renderer }.thenReturn(renderer)
        on { this.viewPortHandler }.thenReturn(viewPortHandler)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleChart()

        // When
        val nextChart = chain.applyToChart(chart)

        //Then
        verify(chart).setTouchEnabled(true)
        verify(chart).setScaleEnabled(false)
        verify(description).isEnabled = false
        verify(legend).isEnabled = false
        verify(chart).setViewPortOffsets(0f, 0f, 0f, 0f)

        Assertions.assertThat(nextChart).isEqualTo(chart)
    }

    @Test
    fun testNext() {
        // Given
        val differentChart = mock<BarChart>()
        val next = mock<ConsumptionBarChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
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
@PrepareForTest(ContextCompat::class)
class SetChartDataTest {
    private val context = mock<Context> {
        on { getString(R.string.consumption_per_month) }.thenReturn("consumption")
    }

    private val chart = mock<BarChart> {
        on { this.context }.thenReturn(context)
    }

    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.consumption_chart)))
                .thenReturn(0)
    }

    @Test
    fun testApplyToChart() {
        // Given
        val points = listOf(CurrentMonthPointsJson(100,"1551211293"), CurrentMonthPointsJson(200,"1551211294"))
        val chain = SetChartData(points)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(chart)

        val barDataCaptor = argumentCaptor<BarData>()
        verify(chart).data = barDataCaptor.capture()
        verify(chart).invalidate()

        val barData = barDataCaptor.firstValue
        val set = barData.dataSets[0]

        Assertions.assertThat(barData.barWidth).isEqualTo(0.6f)

        Assertions.assertThat(set.color).isEqualTo(0)
        Assertions.assertThat(set.isDrawValuesEnabled).isFalse()
    }

    @Test
    fun testNext() {
        // Given
        val points = listOf(CurrentMonthPointsJson(100,"1551211293"), CurrentMonthPointsJson(200,"1551211294"))
        val differentChart = mock<BarChart>()
        val next = mock<ConsumptionBarChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }

        val chain = SetChartData(points)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        Assertions.assertThat(nextChart).isEqualTo(differentChart)
    }
}
