package greenely.greenely.home.ui.latestcomparison

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.home.data.Comparison
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * A [StyleXAxis].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class StyleXAxisTest {

    private val xAxis = mock<XAxis>()
    private val chart = mock<HorizontalBarChart> {
        on { this.xAxis }.thenReturn(xAxis)
    }
    private val valueFormatter = mock<XAxisValueFormatter>()

    /**
     * Set up static mock for [ContextCompat].
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.axis_value_text_color)))
                .thenReturn(0)
    }

    /**
     * Should style the x axis.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleXAxis(valueFormatter,200f)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(chart)
        verify(chart).xAxis
        verify(xAxis).position = XAxis.XAxisPosition.BOTTOM
        verify(xAxis).valueFormatter = valueFormatter
        verify(xAxis).granularity = 1.0f
        verify(xAxis).textSize = 14.0f
        verify(xAxis).setDrawGridLines(false)
        verify(xAxis).textColor = 0
    }

    /**
     * Should execute the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<HorizontalBarChart>()
        val next = mock<ChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }

        val chain = StyleXAxis(valueFormatter,200f)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [StyleLeftAxis].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class StyleLeftAxisText {
    private val leftAxis = mock<YAxis>()
    private val chart = mock<HorizontalBarChart> {
        on { axisLeft }.thenReturn(leftAxis)
    }
    private val valueFormatter = mock<IAxisValueFormatter>()

    /**
     * Set up static mock for [ContextCompat].
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.axis_value_text_color)))
                .thenReturn(0)
    }

    /**
     * Should style the left axis.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleLeftAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        assertThat(nextChart).isEqualTo(chart)
        verify(leftAxis).axisMinimum = 0.0f
        verify(leftAxis).axisMaximum = 10f
        verify(leftAxis).enableGridDashedLine(30.0f, 2.0f, 10.0f)
        verify(leftAxis).setDrawAxisLine(false)
        verify(leftAxis).textColor = 0
    }

    /**
     * Should execute the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<HorizontalBarChart>()
        val next = mock<ChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }

        val chain = StyleLeftAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [StyleRightAxis].
 */
class StyleRightAxisTest {
    private val valueFormatter = mock<XAxisValueFormatter>()
    private val axis = mock<YAxis>()
    private val chart = mock<HorizontalBarChart> {
        on { axisRight }.thenReturn(axis)
    }

    /**
     * Should style the right axis.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleRightAxis(100f,valueFormatter)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(axis).isEnabled = false
        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<HorizontalBarChart>()
        val next = mock<ChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }
        val chain = StyleRightAxis(100f,valueFormatter)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [StyleChart].
 */
class StyleChartTest {
    private val description = mock<Description>()
    private val legend = mock<Legend>()
    private val chart = mock<HorizontalBarChart> {
        on { this.description }.thenReturn(description)
        on { this.legend }.thenReturn(legend)
    }

    /**
     * Should style the chart.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleChart()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(chart)
        verify(chart).setTouchEnabled(false)
        verify(chart).setScaleEnabled(false)
        verify(chart).extraBottomOffset = 8.0f
        verify(description).isEnabled = false
        verify(legend).isEnabled = false
    }

    /**
     * Should execute the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<HorizontalBarChart>()
        val next = mock<ChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }
        val chain = StyleChart()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [SetChartData].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class SetChartDataTest {
    private val context = mock<Context> {
        on { getString(R.string.me) }.thenReturn("Me")
        on { getString(R.string.others) }.thenReturn("Others")
        on { getString(R.string.top) }.thenReturn("Top")
    }
    private val valueFormatter = mock<IValueFormatter>()

    private val chart = mock<HorizontalBarChart> {
        on { this.context }.thenReturn(context)
    }

    /**
     * Initialize static mock of [ContextCompat].
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_me)))
                .thenReturn(0)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_others)))
                .thenReturn(1)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_top)))
                .thenReturn(2)
    }

    /**
     * Should add chart data for the chart based on the given [Comparison].
     */
    @Test
    fun testApplyToChart() {
        // Given
        val comparison = Comparison(0, 10000, 20000, 30000)
        val chain = SetChartData(comparison)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(chart)

        val barDataCaptor = argumentCaptor<BarData>()
        verify(chart).data = barDataCaptor.capture()
        verify(chart).invalidate()

        val barData = barDataCaptor.firstValue
        val meSet = barData.dataSets[0]
        val othersSet = barData.dataSets[1]
        val bestSet = barData.dataSets[2]

        assertThat(barData.barWidth).isEqualTo(0.25f)

        assertThat(meSet.getEntriesForXValue(1.0f).first().y).isEqualTo(10.0f)
        assertThat(meSet.label).isEqualTo("Me")
        assertThat(meSet.color).isEqualTo(0)
        assertThat(meSet.valueTextSize).isEqualTo(16.0f)

        assertThat(othersSet.getEntriesForXValue(2.0f).first().y).isEqualTo(20.0f)
        assertThat(othersSet.label).isEqualTo("Others")
        assertThat(othersSet.color).isEqualTo(1)
        assertThat(othersSet.valueTextSize).isEqualTo(16.0f)

        assertThat(bestSet.getEntriesForXValue(3.0f).first().y).isEqualTo(30.0f)
        assertThat(bestSet.label).isEqualTo("Top")
        assertThat(bestSet.color).isEqualTo(2)
        assertThat(bestSet.valueTextSize).isEqualTo(16.0f)
    }

    /**
     * Should execute the next part of the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val comparison = Comparison(0, 10000, 20000, 30000)
        val differentChart = mock<HorizontalBarChart>()
        val next = mock<ChartAction> {
            on { applyToChart(any()) }.thenReturn(differentChart)
        }

        val chain = SetChartData(comparison)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [LatestComparisonChartSetupFactory].
 */
class LatestComparisonChartActionFactoryTest {

    /**
     * Should create a chain with:
     *
     * [StyleXAxis] -> [StyleLeftAxis] -> [StyleRightAxis] -> [StyleChart] -> [SetChartData]
     */
    @Test
    fun testCreateChartChainWithData() {
        // Given
        val factory = LatestComparisonChartSetupFactory(mock())

        // When
        val chain = factory.createChartSetupWithData(Comparison(0, 0, 0, 0), 10f)

        // Then
        val styleXAxis = chain as? StyleXAxis
        val styleLeftAxis = styleXAxis?.next as? StyleLeftAxis
        val styleRightAxis = styleLeftAxis?.next as? StyleRightAxis
        val styleChart = styleRightAxis?.next as? StyleChart
        val setChartData = styleChart?.next as? SetChartData

        assertThat(styleXAxis).isNotNull()
        assertThat(styleLeftAxis).isNotNull()
        assertThat(styleRightAxis).isNotNull()
        assertThat(styleChart).isNotNull()
        assertThat(setChartData).isNotNull()
    }
}

/**
 * An [XAxisValueFormatter].
 */
@RunWith(Parameterized::class)
class XAxisValueFormatterTest(
        private val index: Float,
        private val stringResource: Int,
        private val expectedText: String
) {

    companion object {
        /**
         * Create the test data.
         */
        @JvmStatic
        @Parameters(name = "{index}: getFormattedValue({0})={2}")
        fun data(): Array<Array<Any>> {
            return arrayOf(
                    arrayOf(
                            1.0f,
                            R.string.me,
                            "Me"
                    ),
                    arrayOf(
                            2.0f,
                            R.string.others,
                            "Others"
                    ),
                    arrayOf(
                            3.0f,
                            R.string.top,
                            "Top"
                    )
            )
        }
    }

    /**
     * Should return expected text for each index
     */
    @Test
    fun testGetFormattedValue() {
        // Given
        val context = mock<Context> {
            on { getString(stringResource) }.thenReturn(expectedText)
        }
        val formatter = XAxisValueFormatter(context)

        // When
        val label = formatter.getFormattedValue(index, XAxis())

        // Then
        assertThat(label).isEqualTo(expectedText)
    }
}

