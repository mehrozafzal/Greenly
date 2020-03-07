package greenely.greenely.feed.ui.weeklyreport

import android.content.Context
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.DataPoint
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class HeaderModelTest {
    private lateinit var context: Context

    private val dataPointList = listOf(
            DataPoint(DateTime(2017, 1, 2, 0, 0), 2000),
            DataPoint(DateTime(2017, 1, 8, 0, 0), 3000)
    )

    @Before
    fun setUp() {
        context = mock {
            on { getString(R.string.week_x) } doReturn "week %s"
            on { getString(R.string.x_kwh) } doReturn "%s kWh"
        }
    }

    @Test
    fun testTimeRangeUnit() {
        // Given
        val chartData = ChartData(dataPointList)
        val model = HeaderModel(context, chartData)

        // Then
        assertThat(model.timeRangeUnit).isEqualTo("week 1")
    }

    @Test
    fun testTimeRange() {
        // Given
        val chartData = ChartData(dataPointList)
        val model = HeaderModel(context, chartData)

        // Then
        assertThat(model.timeRange).isEqualTo("02 jan - 08 jan")
    }

    @Test
    fun testConsumption() {
        // Given
        val chartData = ChartData(dataPointList)
        val model = HeaderModel(context, chartData)

        // Then
        assertThat(model.consumption).isEqualTo("5.00 kWh")
    }
}