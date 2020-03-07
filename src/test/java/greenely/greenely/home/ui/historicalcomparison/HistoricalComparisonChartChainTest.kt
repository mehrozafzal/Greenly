package greenely.greenely.home.ui.historicalcomparison

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.TypedValue
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.home.data.Comparison
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * A [StyleXAxis].
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(ContextCompat::class)
class StyleXAxisTest {

    private val formatterFactory = mock<XAxisValueFormatterFactory> {
        on { createValueFormatterForData(any(), eq(1)) }.thenReturn(mock())
    }

    private val xAxis = mock<XAxis>()

    private val chart = mock<LineChart> {
        on { this.xAxis }.thenReturn(xAxis)
    }

    /**
     * Initialize static mocks.
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.axis_value_text_color)))
                .thenReturn(0)
    }

    /**
     * Should style the x-axis.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val comparisons = listOf<Comparison>()
        val chain = StyleXAxis(comparisons, 1, formatterFactory)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(xAxis).position = XAxis.XAxisPosition.BOTTOM
        verify(xAxis).granularity = 1.0f
        verify(xAxis).setDrawGridLines(false)
        verify(xAxis).axisMinimum = -0.5f
        verify(xAxis).axisMaximum = 6.5f
        verify(xAxis).textColor = 0

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val comparisons = listOf<Comparison>()
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleXAxis(comparisons, 1, formatterFactory)
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
@PrepareOnlyThisForTest(ContextCompat::class)
class StyleLeftAxisTest {
    private val axis = mock<YAxis>()
    private val chart = mock<LineChart> {
        on { axisLeft }.thenReturn(axis)
    }
    private val valueFormatter = mock<IAxisValueFormatter>()

    /**
     * Initialize static mocks.
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
        val chain = StyleLeftAxis(10f)

        // When
        val nextChart = chain.applyToChart(chart)

        //Then
        verify(axis).axisMinimum = 0.0f
        verify(axis).axisMaximum = 10f
        verify(axis).enableGridDashedLine(30.0f, 2.0f, 10.0f)
        verify(axis).setDrawAxisLine(false)
        verify(axis).textColor = 0

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleLeftAxis(10f)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(nextChart)
    }
}

/**
 * A [StyleLegend].
 */
@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class, TypedValue::class)
class StyleLegendTest {

    private val legend = mock<Legend>()

    private val context = mock<Context> {
        on { resources }.thenReturn(mock())
        on { getString(R.string.me) }.thenReturn("Me")
        on { getString(R.string.others) }.thenReturn("Others")
        on { getString(R.string.top) }.thenReturn("Top")
    }

    private val chart = mock<LineChart> {
        on { this.context }.thenReturn(context)
        on { this.legend }.thenReturn(legend)
    }

    /**
     * Initialize static mocks.
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.mockStatic(TypedValue::class.java)

        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_me)))
                .thenReturn(0)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_others)))
                .thenReturn(1)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.comparison_top)))
                .thenReturn(2)

        PowerMockito.`when`(TypedValue.applyDimension(any(), any(), any())).thenReturn(0.0f)
        val field = PowerMockito.field(TypedValue::class.java, "COMPLEX_UNIT_DIP")
        field.set(TypedValue::class.java, 0)
    }

    /**
     * Should apply styling to the legend.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleLegend()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        val entryCaptor = argumentCaptor<MutableList<LegendEntry>>()

        verify(legend).isWordWrapEnabled = true
        verify(legend).setCustom(entryCaptor.capture())
        verify(legend).textSize = 14.0f
        verify(legend).xEntrySpace = 0.0f

        val entryList = entryCaptor.firstValue
        val meEntry = entryList[0]
        val othersEntry = entryList[1]
        val topEntry = entryList[2]

        assertThat(meEntry.label).isEqualTo("Me")
        assertThat(meEntry.formColor).isEqualTo(0)
        assertThat(meEntry.form).isEqualTo(Legend.LegendForm.LINE)

        assertThat(othersEntry.label).isEqualTo("Others")
        assertThat(othersEntry.formColor).isEqualTo(1)
        assertThat(othersEntry.form).isEqualTo(Legend.LegendForm.LINE)

        assertThat(topEntry.label).isEqualTo("Top")
        assertThat(topEntry.formColor).isEqualTo(2)
        assertThat(topEntry.form).isEqualTo(Legend.LegendForm.LINE)

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleLegend()
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
    private val axis = mock<YAxis>()
    private val chart = mock<LineChart> {
        on { axisRight }.thenReturn(axis)
    }

    /**
     * Should just disable the right axis.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleRightAxis()

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        verify(axis).isEnabled = false

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = StyleRightAxis()
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [StyleChart]
 */
@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(ContextCompat::class)
class StyleChartTest {

    private val description = mock<Description>()
    private val context = mock<Context> {
        on { getString(R.string.loading) }.thenReturn("mocked")
    }
    private val chart = mock<LineChart> {
        on { this.context }.thenReturn(context)
        on { this.description }.thenReturn(description)
    }

    /**
     * Set up static mocks.
     */
    @Before
    fun setUp() {
        PowerMockito.mockStatic(ContextCompat::class.java)
        PowerMockito.`when`(ContextCompat.getColor(any(), eq(R.color.accent))).thenReturn(0)
    }

    /**
     * Should apply styling to the chart.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val chain = StyleChart()

        // When
        val nextChart = chain.applyToChart(chart)

        //Then
        verify(chart).setNoDataText("mocked")
        verify(chart).setNoDataTextColor(0)
        verify(chart).setScaleEnabled(false)
        verify(chart).setTouchEnabled(false)
        verify(description).isEnabled = false

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
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
@PrepareOnlyThisForTest(ContextCompat::class)
class SetChartDataTest {
    private val context = mock<Context> {
        on { getString(R.string.me) }.thenReturn("Me")
        on { getString(R.string.others) }.thenReturn("Others")
        on { getString(R.string.top) }.thenReturn("Top")
    }

    private val chart = mock<LineChart> {
        on { this.context }.thenReturn(context)
    }

    /**
     * Initialize static mocks.
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
     * Should set the data for the chart.
     */
    @Test
    fun testApplyToChart() {
        // Given
        val comparisons = listOf(Comparison(0, 0, 0, 0))
        val chain = SetChartData(comparisons)

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        val combinedDataCaptor = argumentCaptor<LineData>()
        verify(chart).data = combinedDataCaptor.capture()
        verify(chart).invalidate()

        val lineDataSets = combinedDataCaptor.firstValue.dataSets
        val meSet = lineDataSets[0]
        val othersSet = lineDataSets[1]
        val topSet = lineDataSets[2]

        assertThat(meSet.getEntriesForXValue(0.0f).first().y).isEqualTo(0.0f)
        assertThat(meSet.color).isEqualTo(0)
        assertThat(meSet.getCircleColor(0)).isEqualTo(0)
        assertThat(meSet.lineWidth).isEqualTo(SetChartData.LINE_WIDTH)
        assertThat(meSet.circleHoleRadius).isEqualTo(3.0f)
        assertThat(meSet.circleRadius).isEqualTo(5.0f)
        assertThat(meSet.isDrawValuesEnabled).isFalse()

        assertThat(othersSet.getEntriesForXValue(0.0f).first().y).isEqualTo(0.0f)
        assertThat(othersSet.color).isEqualTo(1)
        assertThat(othersSet.lineWidth).isEqualTo(SetChartData.LINE_WIDTH)
        assertThat(othersSet.isDrawValuesEnabled).isFalse()

        assertThat(topSet.getEntriesForXValue(0.0f).first().y).isEqualTo(0.0f)
        assertThat(topSet.color).isEqualTo(2)
        assertThat(topSet.lineWidth).isEqualTo(SetChartData.LINE_WIDTH)
        assertThat(topSet.isDrawValuesEnabled).isFalse()

        assertThat(nextChart).isEqualTo(chart)
    }

    /**
     * Should execute the next action in the chain if there is one.
     */
    @Test
    fun testNext() {
        // Given
        val comparisons = listOf(Comparison(0, 0, 0, 0))
        val differentChart = mock<LineChart>()
        val next = mock<ChartAction> {
            on { applyToChart(chart) }.thenReturn(differentChart)
        }
        val chain = SetChartData(comparisons)
        chain.next = next

        // When
        val nextChart = chain.applyToChart(chart)

        // Then
        assertThat(nextChart).isEqualTo(differentChart)
    }
}

/**
 * A [HistoricalComparisonChartSetupFactory].
 */
class HistoricalComparisonChartActionFactoryTest {

    /**
     * Should create a [ChartAction] with:
     *
     * [StyleXAxis] -> [StyleLeftAxis] -> [StyleLegend] -> [StyleRightAxis] -> [StyleChart] -> [SetChartData].
     */
    @Test
    fun testCreateChartChainWithData() {
        // Given
        val factory = HistoricalComparisonChartSetupFactory(mock(), mock())
        val comparisons = listOf<Comparison>()

        // When
        val chain = factory.createChartSetupWithData(comparisons, 1, 10f)

        // Then
        val styleXAxis = chain as? StyleXAxis
        val styleLeftAxis = styleXAxis?.next as? StyleLeftAxis
        val styleLegend = styleLeftAxis?.next as? StyleLegend
        val styleRightAxis = styleLegend?.next as? StyleRightAxis
        val styleChart = styleRightAxis?.next as? StyleChart
        val setChartData = styleChart?.next as? SetChartData

        assertThat(styleXAxis).isNotNull()
        assertThat(styleLeftAxis).isNotNull()
        assertThat(styleLegend).isNotNull()
        assertThat(styleRightAxis).isNotNull()
        assertThat(styleChart).isNotNull()
        assertThat(setChartData).isNotNull()
    }
}
